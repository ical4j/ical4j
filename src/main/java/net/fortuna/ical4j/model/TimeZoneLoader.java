package net.fortuna.ical4j.model;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.ResourceLoader;
import net.fortuna.ical4j.util.TimeZoneCache;
import org.apache.commons.lang3.Validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Period;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.zone.ZoneOffsetTransition;
import java.util.TimeZone;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TimeZoneLoader {

    private static final String TZ_CACHE_IMPL = "net.fortuna.ical4j.timezone.cache.impl";

    // Use the Map-based TimeZoneCache by default
    private static final String DEFAULT_TZ_CACHE_IMPL = "net.fortuna.ical4j.util.MapTimeZoneCache";

    private static final String MESSAGE_MISSING_DEFAULT_TZ_CACHE_IMPL = "Error loading default cache implementation. Please ensure the JCache API dependency is included in the classpath, or override the cache implementation (e.g. via configuration: net.fortuna.ical4j.timezone.cache.impl=net.fortuna.ical4j.util.MapTimeZoneCache)";

    private static final Set<String> TIMEZONE_DEFINITIONS = new HashSet<String>();
    private static final String DATE_TIME_TPL = "yyyyMMdd'T'HHmmss";
    private static final String RRULE_TPL = "FREQ=YEARLY;BYMONTH=%d;BYDAY=%d%s";
    private static final Standard NO_TRANSITIONS;

    static {
        NO_TRANSITIONS = new Standard();
        NO_TRANSITIONS.add(new TzOffsetFrom(ZoneOffset.UTC));
        NO_TRANSITIONS.add(new TzOffsetTo(ZoneOffset.UTC));
        NO_TRANSITIONS.add(new DtStart<>(Instant.EPOCH));
    }

    private static final Map<String, TimeZoneLoader> LOADER_MAP = new ConcurrentHashMap<>();

    private final String resourcePrefix;

    private final TimeZoneUpdater zoneUpdater;

    private final TimeZoneCache cache;

    public TimeZoneLoader(String resourcePrefix) {
        this(resourcePrefix, cacheInit());
    }

    public TimeZoneLoader(String resourcePrefix, TimeZoneCache cache) {
        this.resourcePrefix = resourcePrefix;
        this.zoneUpdater = new TimeZoneUpdater();
        this.cache = cache;
    }

    public String[] getAvailableIDs() {
        return new BufferedReader(new InputStreamReader(
                ResourceLoader.getResourceAsStream(resourcePrefix + "tz.availableIds")))
                .lines().toArray(String[]::new);
    }

    /**
     * Loads an existing VTimeZone from the classpath corresponding to the specified Java timezone.
     */
    public VTimeZone loadVTimeZone(String id) throws IOException, ParserException {
        Validate.notBlank(id, "Invalid TimeZone ID: [%s]", id);
        if (!cache.containsId(id)) {
            final var resource = ResourceLoader.getResource(resourcePrefix + id + ".ics");
            if (resource != null) {
                try (var in = resource.openStream()) {
                    final var builder = new CalendarBuilder();
                    final var calendar = builder.build(in);
                    final Optional<VTimeZone> vTimeZone = calendar.getComponent(Component.VTIMEZONE);
                    // load any available updates for the timezone.. can be explicitly disabled via configuration
                    vTimeZone.ifPresent(timeZone -> cache.putIfAbsent(id, zoneUpdater.updateDefinition(timeZone)));
                }
            } else {
                return generateTimezoneForId(id);
            }
        }
        return cache.getTimezone(id);
    }

    private static VTimeZone generateTimezoneForId(String timezoneId) {
        if (!TIMEZONE_DEFINITIONS.contains(timezoneId)) {
            return null;
        }
        var javaTz = TimeZone.getTimeZone(timezoneId);

        var zoneId = ZoneId.of(javaTz.getID(), ZoneId.SHORT_IDS);

        int rawTimeZoneOffsetInSeconds = javaTz.getRawOffset() / 1_000;

        var timezone = new VTimeZone();

        timezone.add(new TzId(timezoneId));

        addTransitions(zoneId, timezone, rawTimeZoneOffsetInSeconds);

        addTransitionRules(zoneId, rawTimeZoneOffsetInSeconds, timezone);

        if (timezone.getObservances() == null || timezone.getObservances().isEmpty()) {
            timezone.add(NO_TRANSITIONS);
        }

        return timezone;
    }

    private static void addTransitionRules(ZoneId zoneId, int rawTimeZoneOffsetInSeconds, VTimeZone result) {
        ZoneOffsetTransition zoneOffsetTransition = null;

        if (!zoneId.getRules().getTransitions().isEmpty()) {
            Collections.min(zoneId.getRules().getTransitions(),
                    Comparator.comparing(ZoneOffsetTransition::getDateTimeBefore));
        }

        var startDate = LocalDateTime.now(zoneId);

        for (var transitionRule : zoneId.getRules().getTransitionRules()) {
            int transitionRuleMonthValue = transitionRule.getMonth().getValue();
            var transitionRuleDayOfWeek = transitionRule.getDayOfWeek();
            var ldt = LocalDateTime.now(zoneId)
                    .with(TemporalAdjusters.firstInMonth(transitionRuleDayOfWeek))
                    .withMonth(transitionRuleMonthValue)
                    .with(transitionRule.getLocalTime());
            var month = ldt.getMonth();

            TreeSet<Integer> allDaysOfWeek = new TreeSet<Integer>();

            do {
                allDaysOfWeek.add(ldt.getDayOfMonth());
            } while ((ldt = ldt.plus(Period.ofWeeks(1))).getMonth() == month);

            var dayOfMonth = allDaysOfWeek.ceiling(transitionRule.getDayOfMonthIndicator());
            if (dayOfMonth == null) {
                dayOfMonth = allDaysOfWeek.last();
            }

            int weekdayIndexInMonth = 0;
            for (Iterator<Integer> it = allDaysOfWeek.iterator(); it.hasNext() && it.next() != dayOfMonth; ) {
                weekdayIndexInMonth++;
            }

            weekdayIndexInMonth = weekdayIndexInMonth >= 3 ? weekdayIndexInMonth - allDaysOfWeek.size() : weekdayIndexInMonth;

            var rruleText = String.format(RRULE_TPL, transitionRuleMonthValue, weekdayIndexInMonth, transitionRuleDayOfWeek.name().substring(0, 2));

            var offsetFrom = new TzOffsetFrom(transitionRule.getOffsetBefore());
            var offsetTo = new TzOffsetTo(transitionRule.getOffsetAfter());
            RRule<?> rrule = new RRule<>(rruleText);

            var observance = (transitionRule.getOffsetAfter().getTotalSeconds() > rawTimeZoneOffsetInSeconds) ? new Daylight() : new Standard();

            observance.add(offsetFrom);
            observance.add(offsetTo);
            observance.add(rrule);
            observance.add(new DtStart<LocalDateTime>(startDate.withMonth(transitionRule.getMonth().getValue())
                    .withDayOfMonth(transitionRule.getDayOfMonthIndicator())
                    .with(transitionRule.getDayOfWeek()).format(DateTimeFormatter.ofPattern(DATE_TIME_TPL))));

            result.add(observance);
        }
    }

    private static void addTransitions(ZoneId zoneId, VTimeZone result, int rawTimeZoneOffsetInSeconds) {
        Map<ZoneOffsetKey, Set<ZoneOffsetTransition>> zoneTransitionsByOffsets = new HashMap<ZoneOffsetKey, Set<ZoneOffsetTransition>>();

        for (var zoneTransitionRule : zoneId.getRules().getTransitions()) {
            var offfsetKey = ZoneOffsetKey.of(zoneTransitionRule.getOffsetBefore(), zoneTransitionRule.getOffsetAfter());

            Set<ZoneOffsetTransition> transitionRulesForOffset = zoneTransitionsByOffsets.computeIfAbsent(offfsetKey, k -> new HashSet<ZoneOffsetTransition>(1));
            transitionRulesForOffset.add(zoneTransitionRule);
        }


        for (Map.Entry<ZoneOffsetKey, Set<ZoneOffsetTransition>> e : zoneTransitionsByOffsets.entrySet()) {

            var observance = (e.getKey().offsetAfter.getTotalSeconds() > rawTimeZoneOffsetInSeconds) ? new Daylight() : new Standard();

            var start = Collections.min(e.getValue()).getDateTimeBefore();

            DtStart<?> dtStart = new DtStart<>(start.format(DateTimeFormatter.ofPattern(DATE_TIME_TPL)));
            var offsetFrom = new TzOffsetFrom(e.getKey().offsetBefore);
            var offsetTo = new TzOffsetTo(e.getKey().offsetAfter);

            observance.add(dtStart);
            observance.add(offsetFrom);
            observance.add(offsetTo);

            for (var transition : e.getValue()) {
                RDate<?> rDate = new RDate<>(new ParameterList(),
                        transition.getDateTimeBefore().format(DateTimeFormatter.ofPattern(DATE_TIME_TPL)));
                observance.add(rDate);
            }
            result.add(observance);
        }
    }

    private static TimeZoneCache cacheInit() {
        Optional<TimeZoneCache> property = Configurator.getObjectProperty(TZ_CACHE_IMPL);
        return property.orElseGet(() -> {
            try {
                return (TimeZoneCache) Class.forName(DEFAULT_TZ_CACHE_IMPL).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoClassDefFoundError e) {
                throw new RuntimeException(MESSAGE_MISSING_DEFAULT_TZ_CACHE_IMPL, e);
            }
        });
    }

    public static TimeZoneLoader getInstance(String resourcePrefix) {
        var loader = LOADER_MAP.get(resourcePrefix);
        if (loader == null) {
            LOADER_MAP.put(resourcePrefix, new TimeZoneLoader(resourcePrefix));
        }
        return LOADER_MAP.get(resourcePrefix);
    }

    private static class ZoneOffsetKey {
        private final ZoneOffset offsetBefore;
        private final ZoneOffset offsetAfter;

        private ZoneOffsetKey(ZoneOffset offsetBefore, ZoneOffset offsetAfter) {
            this.offsetBefore = offsetBefore;
            this.offsetAfter = offsetAfter;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return true;
            }
            if (!(obj instanceof ZoneOffsetKey)) {
                return false;
            }
            var otherZoneOffsetKey = (ZoneOffsetKey) obj;
            return Objects.equals(this.offsetBefore, otherZoneOffsetKey.offsetBefore) && Objects.equals(this.offsetAfter, otherZoneOffsetKey.offsetAfter);
        }

        @Override
        public int hashCode() {
            int result = 31;
            result = result * (this.offsetBefore == null ? 1 : this.offsetBefore.hashCode());
            result = result * (this.offsetAfter == null ? 1 : this.offsetAfter.hashCode());

            return result;
        }

        static ZoneOffsetKey of(ZoneOffset offsetBefore, ZoneOffset offsetAfter) {
            return new ZoneOffsetKey(offsetBefore, offsetAfter);
        }
    }
}