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
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.time.Period;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.util.TimeZone;
import java.util.*;

public class TimeZoneLoader {

    private static final String UPDATE_ENABLED = "net.fortuna.ical4j.timezone.update.enabled";
    private static final String UPDATE_CONNECT_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.connect";
    private static final String UPDATE_READ_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.read";
    private static final String UPDATE_PROXY_ENABLED = "net.fortuna.ical4j.timezone.update.proxy.enabled";
    private static final String UPDATE_PROXY_TYPE = "net.fortuna.ical4j.timezone.update.proxy.type";
    private static final String UPDATE_PROXY_HOST = "net.fortuna.ical4j.timezone.update.proxy.host";
    private static final String UPDATE_PROXY_PORT = "net.fortuna.ical4j.timezone.update.proxy.port";

    private static final String TZ_CACHE_IMPL = "net.fortuna.ical4j.timezone.cache.impl";

    private static final String DEFAULT_TZ_CACHE_IMPL = "net.fortuna.ical4j.util.JCacheTimeZoneCache";

    private static final String MESSAGE_MISSING_DEFAULT_TZ_CACHE_IMPL = "Error loading default cache implementation. Please ensure the JCache API dependency is included in the classpath, or override the cache implementation (e.g. via configuration: net.fortuna.ical4j.timezone.cache.impl=net.fortuna.ical4j.util.MapTimeZoneCache)";

    private static Proxy proxy = null;
    private static final String DATE_TIME_TPL = "yyyyMMdd'T'HHmmss";
    private static final String RRULE_TPL = "FREQ=YEARLY;BYMONTH=%d;BYDAY=%d%s";
    private static final Standard NO_TRANSITIONS;

    static {
        NO_TRANSITIONS = new Standard();
        TzOffsetFrom offsetFrom = new TzOffsetFrom(ZoneOffset.UTC);
        TzOffsetTo offsetTo = new TzOffsetTo(ZoneOffset.UTC);
        NO_TRANSITIONS.getProperties().add(offsetFrom);
        NO_TRANSITIONS.getProperties().add(offsetTo);
        DtStart start = new DtStart();
        start.setDate(Instant.EPOCH);
        NO_TRANSITIONS.getProperties().add(start);

        // Proxy configuration..
        try {
            if ("true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED).orElse("false"))) {
                final Proxy.Type type = Configurator.getEnumProperty(Proxy.Type.class, UPDATE_PROXY_TYPE).orElse(Proxy.Type.DIRECT);
                final String proxyHost = Configurator.getProperty(UPDATE_PROXY_HOST).orElse("");
                final int proxyPort = Configurator.getIntProperty(UPDATE_PROXY_PORT).orElse(-1);
                proxy = new Proxy(type, new InetSocketAddress(proxyHost, proxyPort));
            }
        }
        catch (Throwable e) {
            LoggerFactory.getLogger(TimeZoneLoader.class).warn(
                    "Error loading proxy server configuration: " + e.getMessage());
        }
    }

    private final String resourcePrefix;
    private final TimeZoneCache cache;

    public TimeZoneLoader(String resourcePrefix) {
        this(resourcePrefix, cacheInit());
    }

    public TimeZoneLoader(String resourcePrefix, TimeZoneCache cache) {
        this.resourcePrefix = resourcePrefix;
        this.cache = cache;
    }

    public String[] getAvailableIDs() {
        return new BufferedReader(new InputStreamReader(
                ResourceLoader.getResourceAsStream("net/fortuna/ical4j/model/tz.availableIds")))
                .lines().toArray(String[]::new);
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
                    final Optional<VTimeZone> vTimeZone = calendar.getComponent(Component.VTIMEZONE);
                    // load any available updates for the timezone.. can be explicility enabled via configuration
                    if (vTimeZone.isPresent()
                            && "true".equals(Configurator.getProperty(UPDATE_ENABLED).orElse("false"))) {
                        return updateDefinition(vTimeZone.get());
                    }
                    if (vTimeZone.isPresent()) {
                        cache.putIfAbsent(id, vTimeZone.get());
                    }
                }
            } else {
                return generateTimezoneForId(id);
            }
        }
        return cache.getTimezone(id);
    }

    /**
     * @param vTimeZone
     * @return
     */
    private VTimeZone updateDefinition(VTimeZone vTimeZone) throws IOException, ParserException {
        final Optional<TzUrl> tzUrl = vTimeZone.getProperty(Property.TZURL);
        if (tzUrl.isPresent()) {
            final int connectTimeout = Configurator.getIntProperty(UPDATE_CONNECT_TIMEOUT).orElse(0);
            final int readTimeout = Configurator.getIntProperty(UPDATE_READ_TIMEOUT).orElse(0);

            URLConnection connection;
            URL url = tzUrl.get().getUri().toURL();

            if ("true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED).orElse("false")) && proxy != null) {
                connection = url.openConnection(proxy);
            } else {
                connection = url.openConnection();
            }

            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            final CalendarBuilder builder = new CalendarBuilder();

            final Calendar calendar = builder.build(connection.getInputStream());
            final Optional<VTimeZone> updatedVTimeZone = calendar.getComponent(Component.VTIMEZONE);
            if (updatedVTimeZone.isPresent()) {
                return updatedVTimeZone.get();
            }
        }
        return vTimeZone;
    }

    private static VTimeZone generateTimezoneForId(String timezoneId) throws ParseException {
        if (!ZoneId.getAvailableZoneIds().contains(timezoneId)) {
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
                RDate rDate = new RDate(new ArrayList<>(),
                        transition.getDateTimeBefore().format(DateTimeFormatter.ofPattern(DATE_TIME_TPL)));
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