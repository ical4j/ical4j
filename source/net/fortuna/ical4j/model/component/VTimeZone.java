/*
 * $Id$ [Apr 5, 2004]
 *
 * Copyright (c) 2004, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model.component;

import java.io.IOException;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fortuna.ical4j.data.BuilderException;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzName;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.util.PropertyValidator;
import net.fortuna.ical4j.util.TimeZoneUtils;

/**
 * Defines an iCalendar VTIMEZONE component.
 *
 * @author benf
 */
public class VTimeZone extends Component {

    private static Log log = LogFactory.getLog(VTimeZone.class);

    private ComponentList types;

    /**
     * Default constructor.
     */
    public VTimeZone() {
        super(VTIMEZONE);
        this.types = new ComponentList();
    }

    /**
     * Constructs a new vtimezone component with no properties and the specified
     * list of type components.
     *
     * @param types
     *            a list of type components
     */
    public VTimeZone(final ComponentList types) {
        super(VTIMEZONE);
        this.types = types;
    }

    /**
     * Constructor.
     *
     * @param properties
     *            a list of properties
     * @param types
     *            a list of timezone types
     */
    public VTimeZone(final PropertyList properties, final ComponentList types) {
        super(VTIMEZONE, properties);
        this.types = types;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        return BEGIN + ":" + getName() + "\r\n" + getProperties() + types + END
                + ":" + getName() + "\r\n";
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(boolean recurse) throws ValidationException {

        /*
         * ; 'tzid' is required, but MUST NOT occur more ; than once
         *
         * tzid /
         */
        PropertyValidator.getInstance().validateOne(Property.TZID,
                getProperties());

        /*
         * ; 'last-mod' and 'tzurl' are optional, but MUST NOT occur more than
         * once last-mod / tzurl /
         */
        PropertyValidator.getInstance().validateOneOrLess(
                Property.LAST_MODIFIED, getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.TZURL,
                getProperties());

        /*
         * ; one of 'standardc' or 'daylightc' MUST occur ..; and each MAY occur
         * more than once.
         *
         * standardc / daylightc /
         */
        if (types.getComponent(Time.STANDARD) == null
                && types.getComponent(Time.DAYLIGHT) == null) { throw new ValidationException(
                "Sub-components [" + Time.STANDARD + "," + Time.DAYLIGHT
                        + "] must be specified at least once"); }

        /*
         * ; the following is optional, ; and MAY occur more than once
         *
         * x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * Returns an instance of VTimeZone representing the user's default
     * timezone.
     *
     * @return a VTimeZone
     */
    public static VTimeZone getDefault() {
        return getVTimeZone(TimeZone.getDefault());
    }

    /**
     * Returns an instance of VTimeZone representing the specified Java
     * timezone.
     *
     * @return a VTimeZone
     */
    public static VTimeZone getVTimeZone(final TimeZone timezone) {
        try {
            VTimeZone vTimezone = loadVTimeZone(timezone);

            if (vTimezone != null) { return vTimezone; }
        }
        catch (Exception e) {
            log.debug("Error loading VTimeZone", e);
        }

        return createVTimeZone(timezone);
    }

    /**
     * Loads an existing VTimeZone from the classpath corresponding to the
     * specified Java timezone.
     */
    private static VTimeZone loadVTimeZone(final TimeZone timezone)
            throws IOException, BuilderException {
        String resource = "/" + timezone.getID() + ".ics";

        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar = builder.build(VTimeZone.class
                .getResourceAsStream(resource));

        return (VTimeZone) calendar.getComponents().getComponent(
                Component.VTIMEZONE);
    }

    /**
     * Creates a new VTimeZone based on the specified Java timezone.
     */
    private static VTimeZone createVTimeZone(final TimeZone timezone) {
        TzId tzId = new TzId(timezone.getID());

        PropertyList tzProps = new PropertyList();
        tzProps.add(tzId);

        ComponentList tzComponents = new ComponentList();

        TzName standardTzName = new TzName(new ParameterList(), timezone
                .getDisplayName());
        DtStart standardTzStart = new DtStart(new ParameterList(),
                TimeZoneUtils.getDaylightEnd(timezone));
        TzOffsetTo standardTzOffsetTo = new TzOffsetTo(new ParameterList(),
                timezone.getRawOffset());
        TzOffsetFrom standardTzOffsetFrom = new TzOffsetFrom(timezone
                .getRawOffset()
                + timezone.getDSTSavings());

        PropertyList standardTzProps = new PropertyList();
        standardTzProps.add(standardTzName);
        standardTzProps.add(standardTzStart);
        standardTzProps.add(standardTzOffsetTo);
        standardTzProps.add(standardTzOffsetFrom);

        tzComponents.add(new Standard(standardTzProps));

        if (timezone.useDaylightTime()) {
            TzName daylightTzName = new TzName(new ParameterList(), timezone
                    .getDisplayName()
                    + " (DST)");
            DtStart daylightTzStart = new DtStart(new ParameterList(),
                    TimeZoneUtils.getDaylightStart(timezone));
            TzOffsetTo daylightTzOffsetTo = new TzOffsetTo(new ParameterList(),
                    timezone.getRawOffset() + timezone.getDSTSavings());
            TzOffsetFrom daylightTzOffsetFrom = new TzOffsetFrom(timezone
                    .getRawOffset());

            PropertyList daylightTzProps = new PropertyList();
            daylightTzProps.add(daylightTzName);
            daylightTzProps.add(daylightTzStart);
            daylightTzProps.add(daylightTzOffsetTo);
            daylightTzProps.add(daylightTzOffsetFrom);

            tzComponents.add(new Daylight(daylightTzProps));
        }

        return new VTimeZone(tzProps, tzComponents);
    }

    /**
     * @return Returns the types.
     */
    public final ComponentList getTypes() {
        return types;
    }
}