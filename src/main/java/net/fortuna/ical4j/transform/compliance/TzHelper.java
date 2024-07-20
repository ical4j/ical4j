/*
 *  Copyright (c) 2014-2024, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.fortuna.ical4j.transform.compliance;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DateProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * 
 * @author corneliu dobrota
 * @author daniel grigore
 *
 */
public class TzHelper {

    private static final String MS_TIMEZONES_FILE = "msTimezones";
    private static final Map<String, String> MS_TIMEZONE_IDS = new HashMap<String, String>();
    private static final Map<String, String> MS_TIMEZONE_NAMES = new HashMap<String, String>();

    private static final TimeZoneRegistry TIMEZONE_REGISTRY;
    static {
        TimeZoneRegistryFactory timeZoneRegistryFactory = TimeZoneRegistryFactory.getInstance();
        TIMEZONE_REGISTRY = timeZoneRegistryFactory.createRegistry();
    }

    private static final Logger LOG = LoggerFactory.getLogger(TzHelper.class);
    static {
        initMsTimezones();
    }

    private static void initMsTimezones() {
        try (Scanner scanner = new Scanner(TzHelper.class.getResourceAsStream(MS_TIMEZONES_FILE))) {
            while (scanner.hasNext()) {
                String[] arr = scanner.nextLine().split("=");
                String standardTzId = arr[1];
                String[] displayNameAndMsTzId = arr[0].split(";");
                MS_TIMEZONE_NAMES.put(displayNameAndMsTzId[0], standardTzId);
                MS_TIMEZONE_IDS.put(displayNameAndMsTzId[1], standardTzId);
            }
        } catch (RuntimeException e) { // avoid NoClassDefFoundError
            LOG.error("Could not load MS timezones", e);
            throw new RuntimeException("Unable to load resource file " + MS_TIMEZONES_FILE, e);
        }
    }

    static void correctTzParameterFrom(Property property) {
        if (!property.getParameter(Parameter.TZID).isPresent()) {
            String newTimezoneId = getCorrectedTimezoneFromTzParameter(property);
            correctTzParameter(property, newTimezoneId);
        }
    }

    static void correctTzParameterFrom(DateProperty property) {
        if (property.getValue() != null && property.getValue().endsWith("Z")) {
            property.removeAll(Parameter.TZID);
            return;
        }
        if (property.getParameter(Parameter.TZID).isPresent()) {
            String newTimezone = getCorrectedTimezoneFromTzParameter(property);
            correctTzParameter(property, newTimezone);
        }
    }

    private static void correctTzParameter(Property property, String newTimezoneId) {
        property.removeAll(Parameter.TZID);
        if (newTimezoneId != null) {
            property.add(new TzId(newTimezoneId));
        }
    }

    private static String getCorrectedTimezoneFromTzParameter(Property property) {
        Optional<TzId> tzId = property.getParameter(Parameter.TZID);
        String tzIdValue = tzId.get().getValue();
        return getCorrectedTimeZoneIdFrom(tzIdValue);
    }

    static void correctTzValueOf(net.fortuna.ical4j.model.property.TzId tzProperty) {
        String validTimezone = getCorrectedTimeZoneIdFrom(tzProperty.getValue());
        if (validTimezone != null) {
            tzProperty.setValue(validTimezone);
        }
    }

    /**
     * Gets a valid timezoneId for the specified timezoneValue or <code>null</code> in case the specified time zone
     * value does not match anything known.
     * 
     * @param value
     *            time zone value read from ICS file. The value can be a Microsoft time zone id or an invalid time zone
     *            value
     * @return a valid timezoneId for the specified timezoneValue or <code>null</code> in case the specified time zone
     *         value does not match anything known
     */
    public static String getCorrectedTimeZoneIdFrom(String value) {
        if (value != null) {
            value = value.contains("\"") ? value.replaceAll("\"", "") : value;
            if (TIMEZONE_REGISTRY.getTimeZone(value) != null) {
                return TIMEZONE_REGISTRY.getTimeZone(value).getID();
            }
            String nameCandidate = MS_TIMEZONE_NAMES.get(value);
            if (nameCandidate != null) {
                return TIMEZONE_REGISTRY.getTimeZone(nameCandidate) != null
                        ? TIMEZONE_REGISTRY.getTimeZone(nameCandidate).getID() : nameCandidate;
            }
            return MS_TIMEZONE_IDS.get(value);
        }
        return null;
    }
}