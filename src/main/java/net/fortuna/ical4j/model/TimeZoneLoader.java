package net.fortuna.ical4j.model;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.ResourceLoader;
import net.fortuna.ical4j.util.TimeZoneCache;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.time.Month;
import java.time.Period;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.util.TimeZone;
import java.util.*;

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
        TIMEZONE_DEFINITIONS.addAll(Arrays.asList(net.fortuna.ical4j.model.TimeZone.getAvailableIDs()));

        NO_TRANSITIONS = new Standard();
        TzOffsetFrom offsetFrom = new TzOffsetFrom(ZoneOffset.UTC);
        TzOffsetTo offsetTo = new TzOffsetTo(ZoneOffset.UTC);
        NO_TRANSITIONS.getProperties().add(offsetFrom);
        NO_TRANSITIONS.getProperties().add(offsetTo);
        DtStart start = new DtStart();
        start.setDate(new DateTime(0L));
        NO_TRANSITIONS.getProperties().add(start);
    }

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

    /**
     * Loads an existing VTimeZone from the classpath corresponding to the specified Java timezone.
     *
     * @throws ParseException
     */
    public VTimeZone loadVTimeZone(String id) throws IOException, ParserException, ParseException {
        Validate.notBlank(id, "Invalid TimeZone ID: [%s]", id);
        if (!cache.containsId(id)) {
            final URL resource = ResourceLoader.getResource(resourcePrefix + id + ".ics");
            if (resource != null) {
                try (InputStream in = resource.openStream()) {
                    final CalendarBuilder builder = new CalendarBuilder();
                    final Calendar calendar = builder.build(in);
                    // load any available updates for the timezone.. can be explicility disabled via configuration
                    final VTimeZone vTimeZone = zoneUpdater.updateDefinition(calendar.getComponent(Component.VTIMEZONE));
                    if (vTimeZone != null) {
                        cache.putIfAbsent(id, vTimeZone);
                    }
                }
            } else {
                return generateTimezoneForId(id);
            }
        }
        return cache.getTimezone(id);
    }

    private static VTimeZone generateTimezoneForId(String timezoneId) throws ParseException {
        if (!TIMEZONE_DEFINITIONS.contains(timezoneId)) {
            return null;
        }
        TimeZone javaTz = TimeZone.getTimeZone(timezoneId);

        ZoneId zoneId = ZoneId.of(javaTz.getID(), ZoneId.SHORT_IDS);

        int rawTimeZoneOffsetInSeconds = javaTz.getRawOffset() / 1000;

        VTimeZone timezone = new VTimeZone();

        timezone.getProperties().add(new TzId(timezoneId));

        addTransitions(zoneId, timezone, rawTimeZoneOffsetInSeconds);

        addTransitionRules(zoneId, rawTimeZoneOffsetInSeconds, timezone);

        if (timezone.getObservances() == null || timezone.getObservances().isEmpty()) {
            timezone.getObservances().add(NO_TRANSITIONS);
        }

        return timezone;
    }

    private static void addTransitionRules(ZoneId zoneId, int rawTimeZoneOffsetInSeconds, VTimeZone result) {
        ZoneOffsetTransition zoneOffsetTransition = null;

        if (!zoneId.getRules().getTransitions().isEmpty()) {
            Collections.min(zoneId.getRules().getTransitions(),
                    Comparator.comparing(ZoneOffsetTransition::getDateTimeBefore));
        }

        LocalDateTime startDate = null;
        if (zoneOffsetTransition != null) {
            startDate = zoneOffsetTransition.getDateTimeBefore();
        } else {
            startDate = LocalDateTime.now(zoneId);
        }

        for (ZoneOffsetTransitionRule transitionRule : zoneId.getRules().getTransitionRules()) {
            int transitionRuleMonthValue = transitionRule.getMonth().getValue();
            DayOfWeek transitionRuleDayOfWeek = transitionRule.getDayOfWeek();
            LocalDateTime ldt = LocalDateTime.now(zoneId)
                    .with(TemporalAdjusters.firstInMonth(transitionRuleDayOfWeek))
                    .withMonth(transitionRuleMonthValue)
                    .with(transitionRule.getLocalTime());
            Month month = ldt.getMonth();

            TreeSet<Integer> allDaysOfWeek = new TreeSet<Integer>();

            do {
                allDaysOfWeek.add(ldt.getDayOfMonth());
            } while ((ldt = ldt.plus(Period.ofWeeks(1))).getMonth() == month);

            Integer dayOfMonth = allDaysOfWeek.ceiling(transitionRule.getDayOfMonthIndicator());
            if (dayOfMonth == null) {
                dayOfMonth = allDaysOfWeek.last();
            }

            int weekdayIndexInMonth = 0;
            for (Iterator<Integer> it = allDaysOfWeek.iterator(); it.hasNext() && it.next() != dayOfMonth; ) {
                weekdayIndexInMonth++;
            }

            weekdayIndexInMonth = weekdayIndexInMonth >= 3 ? weekdayIndexInMonth - allDaysOfWeek.size() : weekdayIndexInMonth;

            String rruleText = String.format(RRULE_TPL, transitionRuleMonthValue, weekdayIndexInMonth, transitionRuleDayOfWeek.name().substring(0, 2));

            try {
                TzOffsetFrom offsetFrom = new TzOffsetFrom(transitionRule.getOffsetBefore());
                TzOffsetTo offsetTo = new TzOffsetTo(transitionRule.getOffsetAfter());
                RRule rrule = new RRule(rruleText);

                Observance observance = (transitionRule.getOffsetAfter().getTotalSeconds() > rawTimeZoneOffsetInSeconds) ? new Daylight() : new Standard();

                observance.getProperties().add(offsetFrom);
                observance.getProperties().add(offsetTo);
                observance.getProperties().add(rrule);
                observance.getProperties().add(new DtStart(startDate.withMonth(transitionRule.getMonth().getValue())
                        .withDayOfMonth(transitionRule.getDayOfMonthIndicator())
                        .with(transitionRule.getDayOfWeek()).format(DateTimeFormatter.ofPattern(DATE_TIME_TPL))));

                result.getObservances().add(observance);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void addTransitions(ZoneId zoneId, VTimeZone result, int rawTimeZoneOffsetInSeconds) throws ParseException {
        Map<ZoneOffsetKey, Set<ZoneOffsetTransition>> zoneTransitionsByOffsets = new HashMap<ZoneOffsetKey, Set<ZoneOffsetTransition>>();

        for (ZoneOffsetTransition zoneTransitionRule : zoneId.getRules().getTransitions()) {
            ZoneOffsetKey offfsetKey = ZoneOffsetKey.of(zoneTransitionRule.getOffsetBefore(), zoneTransitionRule.getOffsetAfter());

            Set<ZoneOffsetTransition> transitionRulesForOffset = zoneTransitionsByOffsets.computeIfAbsent(offfsetKey, k -> new HashSet<ZoneOffsetTransition>(1));
            transitionRulesForOffset.add(zoneTransitionRule);
        }


        for (Map.Entry<ZoneOffsetKey, Set<ZoneOffsetTransition>> e : zoneTransitionsByOffsets.entrySet()) {

            Observance observance = (e.getKey().offsetAfter.getTotalSeconds() > rawTimeZoneOffsetInSeconds) ? new Daylight() : new Standard();

            LocalDateTime start = Collections.min(e.getValue()).getDateTimeBefore();

            DtStart dtStart = new DtStart(start.format(DateTimeFormatter.ofPattern(DATE_TIME_TPL)));
            TzOffsetFrom offsetFrom = new TzOffsetFrom(e.getKey().offsetBefore);
            TzOffsetTo offsetTo = new TzOffsetTo(e.getKey().offsetAfter);

            observance.getProperties().add(dtStart);
            observance.getProperties().add(offsetFrom);
            observance.getProperties().add(offsetTo);

            for (ZoneOffsetTransition transition : e.getValue()) {
                RDate rDate = new RDate(new ParameterList(), transition.getDateTimeBefore().format(DateTimeFormatter.ofPattern(DATE_TIME_TPL)));
                observance.getProperties().add(rDate);
            }
            result.getObservances().add(observance);
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
            ZoneOffsetKey otherZoneOffsetKey = (ZoneOffsetKey) obj;
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