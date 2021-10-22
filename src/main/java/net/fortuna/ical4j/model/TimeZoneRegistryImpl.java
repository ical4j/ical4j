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

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.ResourceLoader;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;
import java.util.Properties;
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
        } catch (IOException | NullPointerException e) {
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
    }

    private final TimeZoneLoader timeZoneLoader;

    private Map<String, TimeZone> timezones;

    private final boolean lenientTzResolution;

    /**
     * Default constructor.
     */
    public TimeZoneRegistryImpl() {
        this(DEFAULT_RESOURCE_PREFIX, CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING));
    }

    /**
     * Creates a new instance using the specified resource prefix.
     *
     * @param resourcePrefix a prefix prepended to classpath resource lookups for default timezones
     */
    public TimeZoneRegistryImpl(final String resourcePrefix) {
        this(resourcePrefix, CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING));
    }

    public TimeZoneRegistryImpl(final String resourcePrefix, boolean lenientTzResolution) {
        this.timeZoneLoader = new TimeZoneLoader(resourcePrefix);
        timezones = new ConcurrentHashMap<>();
        this.lenientTzResolution = lenientTzResolution;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void register(final TimeZone timezone) {
        // for now we only apply updates to included definitions by default..
        register(timezone, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void register(final TimeZone timezone, boolean update) {
        if (update) {
            try {
                // load any available updates for the timezone..
                timezones.put(timezone.getID(), new TimeZone(timeZoneLoader.loadVTimeZone(timezone.getID())));
            } catch (IOException | ParserException | ParseException e) {
                Logger log = LoggerFactory.getLogger(TimeZoneRegistryImpl.class);
                log.warn("Error occurred loading VTimeZone", e);
            }
        } else {
            timezones.put(timezone.getID(), timezone);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void clear() {
        timezones.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final TimeZone getTimeZone(final String id) {
        TimeZone timezone = timezones.get(id);
        if (timezone == null) {
            /* A blank TZID is only invalid if it is not declared under the
             * TZID property in the BEGIN:TIMEZONE section. */
            Validate.notBlank(id, "Invalid TimeZone ID: [%s]", id);
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
                                final VTimeZone vTimeZone = timeZoneLoader.loadVTimeZone(id);
                                if (vTimeZone != null) {
                                    // XXX: temporary kludge..
                                    // ((TzId) vTimeZone.getProperties().getProperty(Property.TZID)).setValue(id);
                                    timezone = new TimeZone(vTimeZone);
                                    DEFAULT_TIMEZONES.put(timezone.getID(), timezone);
                                } else if (lenientTzResolution) {
                                    // strip global part of id and match on default tz..
                                    Matcher matcher = TZ_ID_SUFFIX.matcher(id);
                                    if (matcher.find()) {
                                        return getTimeZone(matcher.group());
                                    }
                                }
                            } catch (IOException | ParserException | ParseException e) {
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
}
