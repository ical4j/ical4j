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
package net.fortuna.ical4j.model.property;

import java.text.ParseException;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.ParameterValidator;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a RECURRENCE-ID iCalendar component property.
 * 
 * <pre>
 *     4.8.4.4 Recurrence ID
 *     
 *        Property Name: RECURRENCE-ID
 *     
 *        Purpose: This property is used in conjunction with the &quot;UID&quot; and
 *        &quot;SEQUENCE&quot; property to identify a specific instance of a recurring
 *        &quot;VEVENT&quot;, &quot;VTODO&quot; or &quot;VJOURNAL&quot; calendar component. The property
 *        value is the effective value of the &quot;DTSTART&quot; property of the
 *        recurrence instance.
 *     
 *        Value Type: The default value type for this property is DATE-TIME.
 *        The time format can be any of the valid forms defined for a DATE-TIME
 *        value type. See DATE-TIME value type definition for specific
 *        interpretations of the various forms. The value type can be set to
 *        DATE.
 *     
 *        Property Parameters: Non-standard property, value data type, time
 *        zone identifier and recurrence identifier range parameters can be
 *        specified on this property.
 *     
 *        Conformance: This property can be specified in an iCalendar object
 *        containing a recurring calendar component.
 *     
 *        Description: The full range of calendar components specified by a
 *        recurrence set is referenced by referring to just the &quot;UID&quot; property
 *        value corresponding to the calendar component. The &quot;RECURRENCE-ID&quot;
 *        property allows the reference to an individual instance within the
 *        recurrence set.
 *     
 *        If the value of the &quot;DTSTART&quot; property is a DATE type value, then the
 *        value MUST be the calendar date for the recurrence instance.
 *     
 *        The date/time value is set to the time when the original recurrence
 *        instance would occur; meaning that if the intent is to change a
 *        Friday meeting to Thursday, the date/time is still set to the
 *        original Friday meeting.
 *     
 *        The &quot;RECURRENCE-ID&quot; property is used in conjunction with the &quot;UID&quot;
 *        and &quot;SEQUENCE&quot; property to identify a particular instance of a
 *        recurring event, to-do or journal. For a given pair of &quot;UID&quot; and
 *        &quot;SEQUENCE&quot; property values, the &quot;RECURRENCE-ID&quot; value for a
 *        recurrence instance is fixed. When the definition of the recurrence
 *        set for a calendar component changes, and hence the &quot;SEQUENCE&quot;
 *        property value changes, the &quot;RECURRENCE-ID&quot; for a given recurrence
 *        instance might also change.The &quot;RANGE&quot; parameter is used to specify
 *        the effective range of recurrence instances from the instance
 *        specified by the &quot;RECURRENCE-ID&quot; property value. The default value
 *        for the range parameter is the single recurrence instance only. The
 *        value can also be &quot;THISANDPRIOR&quot; to indicate a range defined by the
 *        given recurrence instance and all prior instances or the value can be
 *        &quot;THISANDFUTURE&quot; to indicate a range defined by the given recurrence
 *        instance and all subsequent instances.
 *     
 *        Format Definition: The property is defined by the following notation:
 *     
 *          recurid    = &quot;RECURRENCE-ID&quot; ridparam &quot;:&quot; ridval CRLF
 *     
 *          ridparam   = *(
 *     
 *                     ; the following are optional,
 *                     ; but MUST NOT occur more than once
 *     
 *                     (&quot;;&quot; &quot;VALUE&quot; &quot;=&quot; (&quot;DATE-TIME&quot; / &quot;DATE)) /
 *                     (&quot;;&quot; tzidparam) / (&quot;;&quot; rangeparam) /
 *     
 *                     ; the following is optional,
 *                     ; and MAY occur more than once
 *     
 *                     (&quot;;&quot; xparam)
 *     
 *                     )
 *     
 *          ridval     = date-time / date
 *          ;Value MUST match value type
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class RecurrenceId extends DateProperty {

    private static final long serialVersionUID = 4456883817126011006L;

    /**
     * Default constructor.
     */
    public RecurrenceId() {
        super(RECURRENCE_ID, PropertyFactoryImpl.getInstance());
        setDate(new DateTime());
    }

    /**
     * Creates a new RECURRENCE_ID property initialised with the specified timezone.
     * @param timezone initial timezone
     */
    public RecurrenceId(TimeZone timezone) {
        super(RECURRENCE_ID, timezone, PropertyFactoryImpl.getInstance());
    }

    /**
     * Creates a new instance initialised with the parsed value.
     * @param value the RECURRENCE_ID value string to parse
     * @throws ParseException where the specified string is not a valid RECURRENCE_ID value representation
     */
    public RecurrenceId(final String value) throws ParseException {
        super(RECURRENCE_ID, PropertyFactoryImpl.getInstance());
        setValue(value);
    }

    /**
     * Creates a new RECURRENCE_ID property initialised with the specified timezone and value.
     * @param value a string representation of a RECURRENCE_ID value
     * @param timezone initial timezone
     * @throws ParseException where the specified value is not a valid string
     * representation
     */
    public RecurrenceId(String value, TimeZone timezone) throws ParseException {
        super(RECURRENCE_ID, timezone, PropertyFactoryImpl.getInstance());
        setValue(value);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     * @throws ParseException where the specified value string is not a valid date-time/date representation
     */
    public RecurrenceId(final ParameterList aList, final String aValue)
            throws ParseException {
        super(RECURRENCE_ID, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence of a VALUE parameter.
     * @param aDate a date representation of a date or date-time
     */
    public RecurrenceId(final Date aDate) {
        super(RECURRENCE_ID, PropertyFactoryImpl.getInstance());
        setDate(aDate);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence of a VALUE parameter.
     * @param aList a list of parameters for this component
     * @param aDate a date representation of a date or date-time
     */
    public RecurrenceId(final ParameterList aList, final Date aDate) {
        super(RECURRENCE_ID, aList, PropertyFactoryImpl.getInstance());
        setDate(aDate);
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        super.validate();

        /*
         * ; the following are optional, ; but MUST NOT occur more than once (";" "VALUE" "=" ("DATE-TIME" / "DATE)) /
         * (";" tzidparam) / (";" rangeparam) /
         */

        ParameterValidator.getInstance().assertOneOrLess(Parameter.RANGE,
                getParameters());

        /*
         * ; the following is optional, ; and MAY occur more than once (";" xparam)
         */
    }
}
