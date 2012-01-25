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

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.TzUrl;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.ResourceLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;

/**
 * $Id$
 *
 * Created on 18/09/2005
 *
 * The default implementation of a <code>TimeZoneRegistry</code>. This implementation will search the classpath for
 * applicable VTimeZone definitions used to back the provided TimeZone instances.
 * @author Ben Fortuna
 */
public class TimeZoneRegistryImpl implements TimeZoneRegistry {

    private static final String DEFAULT_RESOURCE_PREFIX = "zoneinfo/";

    private static final Pattern TZ_ID_SUFFIX = Pattern.compile("(?<=/)[^/]*/[^/]*$");
    
    private static final String UPDATE_ENABLED = "net.fortuna.ical4j.timezone.update.enabled";

    private static final Map DEFAULT_TIMEZONES = new ConcurrentHashMap();

    private static final Properties ALIASES = new Properties();
    static {
        try {
            ALIASES.load(ResourceLoader.getResourceAsStream("net/fortuna/ical4j/model/tz.alias"));
        }
        catch (IOException ioe) {
            LogFactory.getLog(TimeZoneRegistryImpl.class).warn(
                    "Error loading timezone aliases: " + ioe.getMessage());
        }
    }

    private Map timezones;

    private String resourcePrefix;

    /**
     * Default constructor.
     */
    public TimeZoneRegistryImpl() {
        this(DEFAULT_RESOURCE_PREFIX);
    }

    /**
     * Creates a new instance using the specified resource prefix.
     * @param resourcePrefix a prefix prepended to classpath resource lookups for default timezones
     */
    public TimeZoneRegistryImpl(final String resourcePrefix) {
        this.resourcePrefix = resourcePrefix;
        timezones = new ConcurrentHashMap();
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
    	}
    	else {
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
        TimeZone timezone = (TimeZone) timezones.get(id);
        if (timezone == null) {
            timezone = (TimeZone) DEFAULT_TIMEZONES.get(id);
            if (timezone == null) {
                // if timezone not found with identifier, try loading an alias..
                final String alias = ALIASES.getProperty(id);
                if (alias != null) {
                    return getTimeZone(alias);
                }
                else {
                    synchronized (DEFAULT_TIMEZONES) {
                    	// check again as it may be loaded now..
                    	timezone = (TimeZone) DEFAULT_TIMEZONES.get(id);
                    	if (timezone == null) {
                            try {
                                final VTimeZone vTimeZone = loadVTimeZone(id);
                                if (vTimeZone != null) {
                                    // XXX: temporary kludge..
                                    // ((TzId) vTimeZone.getProperties().getProperty(Property.TZID)).setValue(id);
                                    timezone = new TimeZone(vTimeZone);
                                    DEFAULT_TIMEZONES.put(timezone.getID(), timezone);
                                }
                                else if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
                                    // strip global part of id and match on default tz..
                                    Matcher matcher = TZ_ID_SUFFIX.matcher(id);
                                    if (matcher.find()) {
                                        return getTimeZone(matcher.group());
                                    }
                                }
                            }
                            catch (Exception e) {
                                Log log = LogFactory.getLog(TimeZoneRegistryImpl.class);
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
     */
    private VTimeZone loadVTimeZone(final String id) throws IOException, ParserException {
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
        return null;
    }
    
    /**
     * @param vTimeZone
     * @return
     */
    private VTimeZone updateDefinition(VTimeZone vTimeZone) {
        final TzUrl tzUrl = vTimeZone.getTimeZoneUrl();
        if (tzUrl != null) {
            try {
                final CalendarBuilder builder = new CalendarBuilder();
                final Calendar calendar = builder.build(tzUrl.getUri().toURL().openStream());
                final VTimeZone updatedVTimeZone = (VTimeZone) calendar.getComponent(Component.VTIMEZONE);
                if (updatedVTimeZone != null) {
                    return updatedVTimeZone;
                }
            }
            catch (Exception e) {
                Log log = LogFactory.getLog(TimeZoneRegistryImpl.class);
                log.warn("Unable to retrieve updates for timezone: " + vTimeZone.getTimeZoneId().getValue(), e);
            }
        }
        return vTimeZone;
    }
}
