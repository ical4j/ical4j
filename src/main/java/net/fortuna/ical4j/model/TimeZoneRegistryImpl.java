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
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.ResourceLoader;
import org.apache.commons.lang3.Validate;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRules;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    private static final Map<String, TimeZone> DEFAULT_TIMEZONES = new ConcurrentHashMap<>();

    private static final Properties ALIASES = new Properties();

    static {
        // load default tz aliases..
        for (var aliasResource : Arrays.asList("net/fortuna/ical4j/model/tz.alias",
                "net/fortuna/ical4j/transform/compliance/msTimezoneNames",
                "net/fortuna/ical4j/transform/compliance/msTimezoneIds")) {

            try (var aliasInputStream = ResourceLoader.getResourceAsStream(aliasResource)) {
                ALIASES.load(aliasInputStream);
            } catch (IOException | NullPointerException e) {
                LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn(
                        "Error loading timezone aliases: {}", e.getMessage());
            }
        }

        // load custom tz aliases..
        try (var aliasInputStream = ResourceLoader.getResourceAsStream("tz.alias")) {
            ALIASES.load(aliasInputStream);
        } catch (IOException | NullPointerException e) {
            LoggerFactory.getLogger(TimeZoneRegistryImpl.class).debug(
                    "No custom timezone aliases: {}", e.getMessage());
        }

        for (var alias : ALIASES.stringPropertyNames()) {
            TimeZoneRegistry.ZONE_ALIASES.put(alias, ALIASES.getProperty(alias));
        }

    }

    private final TimeZoneLoader timeZoneLoader;

    private final Map<String, TimeZone> timezones;

    private final Map<String, ZoneRules> zoneRules;

    private final Map<String, String> zoneIds;

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
        this.timeZoneLoader = TimeZoneLoader.getInstance(resourcePrefix);
        timezones = new ConcurrentHashMap<>();
        zoneRules = new ConcurrentHashMap<>();
        zoneIds = new HashMap<>();
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
            } catch (IOException | ParserException e) {
                var log = LoggerFactory.getLogger(TimeZoneRegistryImpl.class);
                log.warn("Error occurred loading VTimeZone", e);
            }
        } else {
            timezones.put(timezone.getID(), timezone);
        }

        // use latest timezone definition to build zone rules..
        var newZoneRules = new ZoneRulesBuilder().vTimeZone(timezones.get(timezone.getID()).getVTimeZone())
                .build();
        var globalId = "ical4j~" + UUID.randomUUID();
        zoneIds.put(globalId, timezone.getID());
        zoneRules.put(globalId, newZoneRules);
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
        var timezone = timezones.get(id);
        if (timezone == null) {
            /* A blank TZID is only invalid if it is not declared under the
             * TZID property in the BEGIN:TIMEZONE section. */
            Validate.notBlank(id, "Invalid TimeZone ID: [%s]", id);
            timezone = DEFAULT_TIMEZONES.get(id);
            if (timezone == null) {
                // if timezone not found with identifier, try loading an alias..
                final var alias = ALIASES.getProperty(id);
                if (alias != null) {
                    return getTimeZone(alias);
                } else {
                    synchronized (DEFAULT_TIMEZONES) {
                        // check again as it may be loaded now..
                        timezone = DEFAULT_TIMEZONES.get(id);
                        if (timezone == null) {
                            try {
                                final var vTimeZone = timeZoneLoader.loadVTimeZone(id);
                                if (vTimeZone != null) {
                                    timezone = new TimeZone(vTimeZone);
                                    DEFAULT_TIMEZONES.put(timezone.getID(), timezone);
                                    if (!timezone.getID().equals(id)) {
                                        DEFAULT_TIMEZONES.put(id, timezone);
                                    }
                                } else if (lenientTzResolution) {
                                    // strip global part of id and match on default tz..
                                    var matcher = TZ_ID_SUFFIX.matcher(id);
                                    if (matcher.find()) {
                                        return getTimeZone(matcher.group());
                                    }
                                }
                            } catch (IOException | ParserException e) {
                                var log = LoggerFactory.getLogger(TimeZoneRegistryImpl.class);
                                log.warn("Error occurred loading VTimeZone", e);
                            }
                        }
                    }
                }
            }
        }
        return timezone;
    }

    @Override
    public Map<String, ZoneRules> getZoneRules() {
        return zoneRules;
    }

    @Override
    public ZoneId getZoneId(String tzId) {
        return ZoneId.of(zoneIds.entrySet().stream().filter(entry -> entry.getValue().equals(tzId))
                .findFirst().orElseThrow(() -> new DateTimeException(String.format("Unknown timezone identifier [%s]", tzId))).getKey(),
                TimeZoneRegistry.ZONE_ALIASES);
    }

    @Override
    public String getTzId(String zoneId) {
        return zoneIds.get(zoneId);
    }
}
