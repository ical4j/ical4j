/*
 * Copyright (c) 2005, Ben Fortuna
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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.VTimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private static final String DEFAULT_RESOURCE_PREFIX = "/zoneinfo/";

    private Log log = LogFactory.getLog(TimeZoneRegistryImpl.class);

    private static final Map DEFAULT_TIMEZONES = new HashMap();

    private static final Properties ALIASES = new Properties();
    static {
        try {
            ALIASES.load(TimeZoneRegistryImpl.class
                    .getResourceAsStream("/net/fortuna/ical4j/model/tz.alias"));
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
        timezones = new HashMap();
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.TimeZoneRegistry#register(net.fortuna.ical4j.model.TimeZone)
     */
    public final void register(final TimeZone timezone) {
        timezones.put(timezone.getID(), timezone);
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.TimeZoneRegistry#clear()
     */
    public final void clear() {
        timezones.clear();
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.TimeZoneRegistry#getTimeZone(java.lang.String)
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
                        try {
                            final VTimeZone vTimeZone = loadVTimeZone(id);
                            if (vTimeZone != null) {
                                // XXX: temporary kludge..
                                // ((TzId) vTimeZone.getProperties().getProperty(Property.TZID)).setValue(id);
                                timezone = new TimeZone(vTimeZone);
                                DEFAULT_TIMEZONES.put(timezone.getID(), timezone);
                            }
                        }
                        catch (Exception e) {
                            log.warn("Error occurred loading VTimeZone", e);
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
        final URL resource = TimeZoneRegistryImpl.class.getResource(resourcePrefix + id + ".ics");
        if (resource != null) {
            final CalendarBuilder builder = new CalendarBuilder();
            final Calendar calendar = builder.build(resource.openStream());
            return (VTimeZone) calendar.getComponent(Component.VTIMEZONE);
        }
        return null;
    }
}
