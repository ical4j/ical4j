/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.*;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalAdjusters;
import org.threeten.bp.zone.ZoneOffsetTransition;
import org.threeten.bp.zone.ZoneOffsetTransitionRule;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * $Id$
 * <p/>
 * Created on 18/09/2005
 * <p/>
 * The default implementation of a <code>TimeZoneRegistry</code>. This implementation will search the classpath for
 * applicable VTimeZone definitions used to back the provided TimeZone instances.
 *
 * @author Ben Fortuna
 */
public class TimeZoneRegistryImpl implements TimeZoneRegistry {

    private static final String DEFAULT_RESOURCE_PREFIX = "zoneinfo/";

    private static final Pattern TZ_ID_SUFFIX = Pattern.compile("(?<=/)[^/]*/[^/]*$");

    private static final String UPDATE_ENABLED = "net.fortuna.ical4j.timezone.update.enabled";
    private static final String UPDATE_CONNECT_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.connect";
    private static final String UPDATE_READ_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.read";
    private static final String UPDATE_PROXY_ENABLED = "net.fortuna.ical4j.timezone.update.proxy.enabled";
    private static final String UPDATE_PROXY_TYPE = "net.fortuna.ical4j.timezone.update.proxy.type";
    private static final String UPDATE_PROXY_HOST = "net.fortuna.ical4j.timezone.update.proxy.host";
    private static final String UPDATE_PROXY_PORT = "net.fortuna.ical4j.timezone.update.proxy.port";

    private static Proxy proxy = null;

    private static final Map<String, TimeZone> DEFAULT_TIMEZONES = new ConcurrentHashMap<String, TimeZone>();

    private static final Properties ALIASES = new Properties();

    static {
        InputStream aliasInputStream = null;
        try {
            aliasInputStream = ResourceLoader.getResourceAsStream("net/fortuna/ical4j/model/tz.alias");
            ALIASES.load(aliasInputStream);
        } catch (IOException ioe) {
            LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn(
                    "Error loading timezone aliases: " + ioe.getMessage());
        } finally {
            if (aliasInputStream != null) {
                try {
                    aliasInputStream.close();
                } catch (IOException e) {
                    LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn(
                            "Error closing resource stream: " + e.getMessage());
                }
            }
        }

        try {
            aliasInputStream = ResourceLoader.getResourceAsStream("tz.alias");
        	ALIASES.load(aliasInputStream);
        } catch (Exception e) {
            LoggerFactory.getLogger(TimeZoneRegistryImpl.class).debug(
        			"Error loading custom timezone aliases: " + e.getMessage());
        } finally {
            if (aliasInputStream != null) {
                try {
                    aliasInputStream.close();
                } catch (IOException e) {
                    LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn(
                            "Error closing resource stream: " + e.getMessage());
                }
            }
        }
        try {
            if ("true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED))) {
                final Proxy.Type type = Proxy.Type.valueOf(Configurator.getProperty(UPDATE_PROXY_TYPE));
                final String proxyHost = Configurator.getProperty(UPDATE_PROXY_HOST);
                final int proxyPort = Integer.parseInt(Configurator.getProperty(UPDATE_PROXY_PORT));
                proxy = new Proxy(type, new InetSocketAddress(proxyHost, proxyPort));
            }
        }
        catch (Throwable e) {
            LoggerFactory.getLogger(TimeZoneRegistryImpl.class).debug(
                    "Error loading proxy server configuration: " + e.getMessage());
        }
    }

    private Map<String, TimeZone> timezones;

    private String resourcePrefix;

    /**
     * Default constructor.
     */
    public TimeZoneRegistryImpl() {
        this(DEFAULT_RESOURCE_PREFIX);
    }

    /**
     * Creates a new instance using the specified resource prefix.
     *
     * @param resourcePrefix a prefix prepended to classpath resource lookups for default timezones
     */
    public TimeZoneRegistryImpl(final String resourcePrefix) {
        this.resourcePrefix = resourcePrefix;
        timezones = new ConcurrentHashMap<String, TimeZone>();
    }

    /**
     * {@inheritDoc}
     */
    public final void register(final TimeZone timezone) {
        // for now we only apply updates to included definitions by default..
        register(timezone, false);
    }

    /**
     * {@inheritDoc}
     */
    public final void register(final TimeZone timezone, boolean update) {
        if (update) {
            // load any available updates for the timezone..
            timezones.put(timezone.getID(), new TimeZone(updateDefinition(timezone.getVTimeZone())));
        } else {
            timezones.put(timezone.getID(), timezone);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void clear() {
        timezones.clear();
    }

    /**
     * {@inheritDoc}
     */
    public final TimeZone getTimeZone(final String id) {
    	if (id == null) {
    		return null;
    	}

        TimeZone timezone = timezones.get(id);
        if (timezone == null) {
            timezone = DEFAULT_TIMEZONES.get(id);
            if (timezone == null) {
                // if timezone not found with identifier, try loading an alias..
                final String alias = ALIASES.getProperty(id);
                if (alias != null) {
                    return getTimeZone(alias);
                } else {
                    synchronized (DEFAULT_TIMEZONES) {
                        // check again as it may be loaded now..
                        timezone = DEFAULT_TIMEZONES.get(id);
                        if (timezone == null) {
                            try {
                                final VTimeZone vTimeZone = loadVTimeZone(id);
                                if (vTimeZone != null) {
                                    // XXX: temporary kludge..
                                    // ((TzId) vTimeZone.getProperties().getProperty(Property.TZID)).setValue(id);
                                    timezone = new TimeZone(vTimeZone);
                                    DEFAULT_TIMEZONES.put(timezone.getID(), timezone);
                                } else if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
                                    // strip global part of id and match on default tz..
                                    Matcher matcher = TZ_ID_SUFFIX.matcher(id);
                                    if (matcher.find()) {
                                        return getTimeZone(matcher.group());
                                    }
                                }
                            } catch (Exception e) {
                                Logger log = LoggerFactory.getLogger(TimeZoneRegistryImpl.class);
                                log.warn("Error occurred loading VTimeZone", e);
                            }
                        }
                    }
                }
            }
        }
        return timezone;
    }

    /**
     * Loads an existing VTimeZone from the classpath corresponding to the specified Java timezone.
     * @throws ParseException 
     */
    private VTimeZone loadVTimeZone(final String id) throws IOException, ParserException, ParseException {
        final URL resource = ResourceLoader.getResource(resourcePrefix + id + ".ics");
        if (resource != null) {
            final CalendarBuilder builder = new CalendarBuilder();
            final Calendar calendar = builder.build(resource.openStream());
            final VTimeZone vTimeZone = (VTimeZone) calendar.getComponent(Component.VTIMEZONE);
            // load any available updates for the timezone.. can be explicility disabled via configuration
            if (!"false".equals(Configurator.getProperty(UPDATE_ENABLED))) {
                return updateDefinition(vTimeZone);
            }
            return vTimeZone;
        }
        return generateTimezoneForId(id);
    }

    /**
     * @param vTimeZone
     * @return
     */
    private VTimeZone updateDefinition(VTimeZone vTimeZone) {
        final TzUrl tzUrl = vTimeZone.getTimeZoneUrl();
        if (tzUrl != null) {
            try {
                final String connectTimeoutProperty = Configurator.getProperty(UPDATE_CONNECT_TIMEOUT);
                final String readTimeoutProperty = Configurator.getProperty(UPDATE_READ_TIMEOUT);

                final int connectTimeout = connectTimeoutProperty != null ? Integer.parseInt(connectTimeoutProperty) : 0;
                final int readTimeout = readTimeoutProperty != null ? Integer.parseInt(readTimeoutProperty) : 0;

                URLConnection connection;
                URL url = tzUrl.getUri().toURL();

                if ("true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED)) && proxy != null) {
                    connection = url.openConnection(proxy);
                }
                else {
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
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger(TimeZoneRegistryImpl.class);
                log.warn("Unable to retrieve updates for timezone: " + vTimeZone.getTimeZoneId().getValue(), e);
            }
        }
        return vTimeZone;
    }
    

    
    private static final Set<String> TIMEZONE_DEFINITIONS = new HashSet<>();
    
//    private static final String DATE_TIME_TPL = "%1$tY%1$tm%1$tdT%1$tH%1$tM%1$tS";
    private static final String DATE_TIME_TPL = "yyyyMMdd'T'HHmmss";

    private static final String RRULE_TPL = "FREQ=YEARLY;BYMONTH=%d;BYDAY=%d%s";
    
    private static final Standard NO_TRANSITIONS;
    
    static {
        for(String timezoneId : TimeZone.getAvailableIDs() ){
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
                
    }

    private static VTimeZone generateTimezoneForId(String timezoneId) throws ParseException {
    	if(!TIMEZONE_DEFINITIONS.contains(timezoneId)){
    		return null;
    	}
		java.util.TimeZone javaTz = java.util.TimeZone.getTimeZone(timezoneId);
		
		ZoneId zoneId = ZoneId.of(javaTz.getID(), ZoneId.SHORT_IDS);
		
		int rawTimeZoneOffsetInSeconds = javaTz.getRawOffset() / 1000;
		
		VTimeZone timezone = new VTimeZone();
		
		timezone.getProperties().add(new TzId(timezoneId));
		
		addTransitions(zoneId, timezone, rawTimeZoneOffsetInSeconds);
		
		addTransitionRules(zoneId, rawTimeZoneOffsetInSeconds, timezone);
		
		if(timezone.getObservances() == null || timezone.getObservances().isEmpty()){
			timezone.getObservances().add(NO_TRANSITIONS);
		}
		
		return timezone;
	}
    
    private static void addTransitionRules(ZoneId zoneId, int rawTimeZoneOffsetInSeconds, VTimeZone result) {
        ZoneOffsetTransition zoneOffsetTransition = Collections.min(zoneId.getRules().getTransitions(),
                new Comparator<ZoneOffsetTransition>() {
                    @Override
                    public int compare(ZoneOffsetTransition z1, ZoneOffsetTransition z2) {
                        return z1.getDateTimeBefore().compareTo(z2.getDateTimeBefore());
                    }
                });

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
			
			TreeSet<Integer> allDaysOfWeek = new TreeSet<>();
			
			do{
				allDaysOfWeek.add(ldt.getDayOfMonth());
			}while((ldt = ldt.plus(org.threeten.bp.Period.ofWeeks(1))).getMonth() == month);
			
			Integer dayOfMonth = allDaysOfWeek.ceiling(transitionRule.getDayOfMonthIndicator());
			if (dayOfMonth == null) {
			    dayOfMonth = allDaysOfWeek.last();
			}
			
			int weekdayIndexInMonth = 0;
			for(Iterator<Integer> it = allDaysOfWeek.iterator(); it.hasNext() && it.next() != dayOfMonth;){
				weekdayIndexInMonth++;
			}
			
			weekdayIndexInMonth = weekdayIndexInMonth >= 3 ? weekdayIndexInMonth - allDaysOfWeek.size()  : weekdayIndexInMonth;
			
			String rruleTemplate = RRULE_TPL;
			String rruleText = String.format(rruleTemplate,transitionRuleMonthValue, weekdayIndexInMonth, transitionRuleDayOfWeek.name().substring(0, 2));
			
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
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
    
    private static void addTransitions(ZoneId zoneId, VTimeZone result, int rawTimeZoneOffsetInSeconds) throws ParseException {
		Map<ZoneOffsetKey, Set<ZoneOffsetTransition>> zoneTransitionsByOffsets = new HashMap<>();
		
		for(ZoneOffsetTransition zoneTransitionRule : zoneId.getRules().getTransitions()){
			ZoneOffsetKey offfsetKey = ZoneOffsetKey.of(zoneTransitionRule.getOffsetBefore(), zoneTransitionRule.getOffsetAfter());
			
			Set<ZoneOffsetTransition> transitionRulesForOffset = zoneTransitionsByOffsets.get(offfsetKey);
			if(transitionRulesForOffset == null){
				transitionRulesForOffset = new HashSet<>(1);
				zoneTransitionsByOffsets.put(offfsetKey, transitionRulesForOffset);
			}
			transitionRulesForOffset.add(zoneTransitionRule);
		}
		
		
		for(Map.Entry<ZoneOffsetKey, Set<ZoneOffsetTransition>> e : zoneTransitionsByOffsets.entrySet()){
			
			Observance observance = (e.getKey().offsetAfter.getTotalSeconds() > rawTimeZoneOffsetInSeconds) ? new Daylight() : new Standard();
			
			LocalDateTime start = Collections.min(e.getValue()).getDateTimeBefore();
			
			DtStart dtStart = new DtStart(start.format(DateTimeFormatter.ofPattern(DATE_TIME_TPL)));
			TzOffsetFrom offsetFrom = new TzOffsetFrom(new UtcOffset(e.getKey().offsetBefore.getTotalSeconds() * 1000L));
			TzOffsetTo offsetTo = new TzOffsetTo(new UtcOffset(e.getKey().offsetAfter.getTotalSeconds() * 1000L)); 
			
			observance.getProperties().add(dtStart);
			observance.getProperties().add(offsetFrom);
			observance.getProperties().add(offsetTo);
			
			for(ZoneOffsetTransition transition : e.getValue()){
				RDate rDate = new RDate(new ParameterList(), transition.getDateTimeBefore().format(DateTimeFormatter.ofPattern(DATE_TIME_TPL)));
				observance.getProperties().add(rDate);
			}
			result.getObservances().add(observance);
		}
	}
    
    private static class ZoneOffsetKey{
    	private final ZoneOffset offsetBefore;
    	private final ZoneOffset offsetAfter;
    	
    	private ZoneOffsetKey(ZoneOffset offsetBefore, ZoneOffset offsetAfter){
    		this.offsetBefore = offsetBefore;
    		this.offsetAfter = offsetAfter;
    	}
    	
    	@Override
    	public boolean equals(Object obj) {
    		if(obj == this){
    			return true;
    		}
    		if(!(obj instanceof ZoneOffsetKey)){
    			return false;
    		}
    		ZoneOffsetKey otherZoneOffsetKey = (ZoneOffsetKey)obj;
    		return Objects.equals(this.offsetBefore, otherZoneOffsetKey.offsetBefore) && Objects.equals(this.offsetAfter, otherZoneOffsetKey.offsetAfter); 
    	}
    	
    	@Override
    	public int hashCode() {
    		int result = 31;
    		result = result * (this.offsetBefore == null ? 1 : this.offsetBefore.hashCode());
    		result = result * (this.offsetAfter == null ? 1 : this.offsetAfter.hashCode());
    		
    		return result;
    	}
    	
    	static ZoneOffsetKey of (ZoneOffset offsetBefore, ZoneOffset offsetAfter){
    		return new ZoneOffsetKey(offsetBefore, offsetAfter);
    	}
    }
	
}	
