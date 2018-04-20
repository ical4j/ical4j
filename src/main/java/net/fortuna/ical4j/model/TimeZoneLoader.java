package net.fortuna.ical4j.model;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.JCacheTimeZoneCache;
import net.fortuna.ical4j.util.ResourceLoader;
import net.fortuna.ical4j.util.TimeZoneCache;
import org.apache.commons.lang3.Validate;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.time.*;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.util.*;
import java.util.TimeZone;
import java.util.function.Supplier;

public class TimeZoneLoader {

    private static final String UPDATE_ENABLED = "net.fortuna.ical4j.timezone.update.enabled";
    private static final String UPDATE_CONNECT_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.connect";
    private static final String UPDATE_READ_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.read";
    private static final String UPDATE_PROXY_ENABLED = "net.fortuna.ical4j.timezone.update.proxy.enabled";
    private static final String UPDATE_PROXY_TYPE = "net.fortuna.ical4j.timezone.update.proxy.type";
    private static final String UPDATE_PROXY_HOST = "net.fortuna.ical4j.timezone.update.proxy.host";
    private static final String UPDATE_PROXY_PORT = "net.fortuna.ical4j.timezone.update.proxy.port";

    private static final String TZ_CACHE_IMPL = "net.fortuna.ical4j.timezone.cache.impl";

    private static Proxy proxy = null;
    private static final Set<String> TIMEZONE_DEFINITIONS = new HashSet<String>();
    private static final String DATE_TIME_TPL = "yyyyMMdd'T'HHmmss";
    private static final String RRULE_TPL = "FREQ=YEARLY;BYMONTH=%d;BYDAY=%d%s";
    private static final Standard NO_TRANSITIONS;

    static {
        for(String timezoneId : net.fortuna.ical4j.model.TimeZone.getAvailableIDs() ){
            TIMEZONE_DEFINITIONS.add(timezoneId);
        }

        NO_TRANSITIONS = new Standard();
        TzOffsetFrom offsetFrom = new TzOffsetFrom(new UtcOffset(0));
        TzOffsetTo offsetTo = new TzOffsetTo(new UtcOffset(0));
        NO_TRANSITIONS.getProperties().add(offsetFrom);
        NO_TRANSITIONS.getProperties().add(offsetTo);
        DtStart start = new DtStart();
        start.setDate(new DateTime(0L));
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
                final CalendarBuilder builder = new CalendarBuilder();
                final Calendar calendar = builder.build(resource.openStream());
                final VTimeZone vTimeZone = (VTimeZone) calendar.getComponent(Component.VTIMEZONE);
                // load any available updates for the timezone.. can be explicility disabled via configuration
                if (!"false".equals(Configurator.getProperty(UPDATE_ENABLED).orElse("true"))) {
                    return updateDefinition(vTimeZone);
                }
                if (vTimeZone != null) {
                    cache.putIfAbsent(id, vTimeZone);
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
        final TzUrl tzUrl = vTimeZone.getTimeZoneUrl();
        if (tzUrl != null) {
            final int connectTimeout = Configurator.getIntProperty(UPDATE_CONNECT_TIMEOUT).orElse(0);
            final int readTimeout = Configurator.getIntProperty(UPDATE_READ_TIMEOUT).orElse(0);

            URLConnection connection;
            URL url = tzUrl.getUri().toURL();

            if ("true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED).orElse("false")) && proxy != null) {
                connection = url.openConnection(proxy);
            } else {
                connection = url.openConnection();
            }

            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);

            final CalendarBuilder builder = new CalendarBuilder();

            final Calendar calendar = builder.build(connection.getInputStream());
            final VTimeZone updatedVTimeZone = (VTimeZone) calendar.getComponent(Component.VTIMEZONE);
            if (updatedVTimeZone != null) {
                return updatedVTimeZone;
            }
        }
        return vTimeZone;
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
                    new Comparator<ZoneOffsetTransition>() {
                        @Override
                        public int compare(ZoneOffsetTransition z1, ZoneOffsetTransition z2) {
                            return z1.getDateTimeBefore().compareTo(z2.getDateTimeBefore());
                        }
                    });
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

            String rruleTemplate = RRULE_TPL;
            String rruleText = String.format(rruleTemplate, transitionRuleMonthValue, weekdayIndexInMonth, transitionRuleDayOfWeek.name().substring(0, 2));

            try {
                TzOffsetFrom offsetFrom = new TzOffsetFrom(new UtcOffset(transitionRule.getOffsetBefore().getTotalSeconds() * 1000L));
                TzOffsetTo offsetTo = new TzOffsetTo(new UtcOffset(transitionRule.getOffsetAfter().getTotalSeconds() * 1000L));
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

            Set<ZoneOffsetTransition> transitionRulesForOffset = zoneTransitionsByOffsets.get(offfsetKey);
            if (transitionRulesForOffset == null) {
                transitionRulesForOffset = new HashSet<ZoneOffsetTransition>(1);
                zoneTransitionsByOffsets.put(offfsetKey, transitionRulesForOffset);
            }
            transitionRulesForOffset.add(zoneTransitionRule);
        }


        for (Map.Entry<ZoneOffsetKey, Set<ZoneOffsetTransition>> e : zoneTransitionsByOffsets.entrySet()) {

            Observance observance = (e.getKey().offsetAfter.getTotalSeconds() > rawTimeZoneOffsetInSeconds) ? new Daylight() : new Standard();

            LocalDateTime start = Collections.min(e.getValue()).getDateTimeBefore();

            DtStart dtStart = new DtStart(start.format(DateTimeFormatter.ofPattern(DATE_TIME_TPL)));
            TzOffsetFrom offsetFrom = new TzOffsetFrom(new UtcOffset(e.getKey().offsetBefore.getTotalSeconds() * 1000L));
            TzOffsetTo offsetTo = new TzOffsetTo(new UtcOffset(e.getKey().offsetAfter.getTotalSeconds() * 1000L));

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
        return property.orElseGet(new Supplier<TimeZoneCache>() {
            @Override
            public TimeZoneCache get() {
                return new JCacheTimeZoneCache();
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