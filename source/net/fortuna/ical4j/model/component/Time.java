/*
 * $Id$ [05-Apr-2004]
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

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.PropertyValidator;

/**
 * Defines an iCalendar timezone type component. Class made abstract such that
 * only Standard and Daylight instances are valid.
 *
 * @author benf
 */
public abstract class Time extends Component {

    /**
     * one of 'standardc' or 'daylightc' MUST occur and each MAY occur more than
     * once.
     */
    public static final String STANDARD = "STANDARD";

    public static final String DAYLIGHT = "DAYLIGHT";

    /**
     * Constructs a time component with the specified name
     * and no properties.
     * @param name the name of this time component
     */
    protected Time(final String name) {
        super(name);
    }

    /**
     * Constructor protected to enforce use of sub-classes
     * from this library.
     * @param name the name of the time type
     * @param properties a list of properties
     */
    protected Time(final String name, final PropertyList properties) {
        super(name, properties);
    }

    /**
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(boolean recurse) throws ValidationException {

        /*

                ; the following are each REQUIRED,
                ; but MUST NOT occur more than once

                dtstart / tzoffsetto / tzoffsetfrom /
         */
        PropertyValidator.getInstance().validateOne(Property.DTSTART,
                getProperties());
        PropertyValidator.getInstance().validateOne(Property.TZOFFSETTO,
                getProperties());
        PropertyValidator.getInstance().validateOne(Property.TZOFFSETFROM,
                getProperties());

        /*

                ; the following are optional,
                ; and MAY occur more than once

                comment / rdate / rrule / tzname / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }
}