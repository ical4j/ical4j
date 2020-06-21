/*
 * TzHelper.java Feb 21, 2014
 * 
 * Copyright (c) 2014 1&1 Internet AG. All rights reserved.
 * 
 * $Id$
 */
package net.fortuna.ical4j.transform.property;

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
class TzHelper {

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
        if (property.getParameter(Parameter.TZID).isPresent()) {
            String newTimezoneId = getCorrectedTimezoneFromTzParameter(property);
            correctTzParameter(property, newTimezoneId);
        }
    }

    static void correctTzParameterFrom(DateProperty property) {
        if (property.getValue() != null && property.getValue().endsWith("Z")) {
            property.removeAll(Parameter.TZID);
            return;
        }
        if (property.getParameters().getFirst(Parameter.TZID).isPresent()) {
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
    private static String getCorrectedTimeZoneIdFrom(String value) {
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