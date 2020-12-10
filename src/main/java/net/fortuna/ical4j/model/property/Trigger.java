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

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
import java.util.Optional;
import java.util.function.Predicate;

import static net.fortuna.ical4j.model.Parameter.RELATED;
import static net.fortuna.ical4j.model.Parameter.VALUE;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.None;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a TRIGGER iCalendar component property.
 * <p/>
 * <pre>
 *     4.8.6.3 Trigger
 *
 *        Property Name: TRIGGER
 *
 *        Purpose: This property specifies when an alarm will trigger.
 *
 *        Value Type: The default value type is DURATION. The value type can be
 *        set to a DATE-TIME value type, in which case the value MUST specify a
 *        UTC formatted DATE-TIME value.
 *
 *        Property Parameters: Non-standard, value data type, time zone
 *        identifier or trigger relationship property parameters can be
 *        specified on this property. The trigger relationship property
 *        parameter MUST only be specified when the value type is DURATION.
 *
 *        Conformance: This property MUST be specified in the &quot;VALARM&quot; calendar
 *        component.
 *
 *        Description: Within the &quot;VALARM&quot; calendar component, this property
 *        defines when the alarm will trigger. The default value type is
 *        DURATION, specifying a relative time for the trigger of the alarm.
 *        The default duration is relative to the start of an event or to-do
 *        that the alarm is associated with. The duration can be explicitly set
 *
 *        to trigger from either the end or the start of the associated event
 *        or to-do with the &quot;RELATED&quot; parameter. A value of START will set the
 *        alarm to trigger off the start of the associated event or to-do. A
 *        value of END will set the alarm to trigger off the end of the
 *        associated event or to-do.
 *
 *        Either a positive or negative duration may be specified for the
 *        &quot;TRIGGER&quot; property. An alarm with a positive duration is triggered
 *        after the associated start or end of the event or to-do. An alarm
 *        with a negative duration is triggered before the associated start or
 *        end of the event or to-do.
 *
 *        The &quot;RELATED&quot; property parameter is not valid if the value type of
 *        the property is set to DATE-TIME (i.e., for an absolute date and time
 *        alarm trigger). If a value type of DATE-TIME is specified, then the
 *        property value MUST be specified in the UTC time format. If an
 *        absolute trigger is specified on an alarm for a recurring event or
 *        to-do, then the alarm will only trigger for the specified absolute
 *        date/time, along with any specified repeating instances.
 *
 *        If the trigger is set relative to START, then the &quot;DTSTART&quot; property
 *        MUST be present in the associated &quot;VEVENT&quot; or &quot;VTODO&quot; calendar
 *        component. If an alarm is specified for an event with the trigger set
 *        relative to the END, then the &quot;DTEND&quot; property or the &quot;DSTART&quot; and
 *        &quot;DURATION' properties MUST be present in the associated &quot;VEVENT&quot;
 *        calendar component. If the alarm is specified for a to-do with a
 *        trigger set relative to the END, then either the &quot;DUE&quot; property or
 *        the &quot;DSTART&quot; and &quot;DURATION' properties MUST be present in the
 *        associated &quot;VTODO&quot; calendar component.
 *
 *        Alarms specified in an event or to-do which is defined in terms of a
 *        DATE value type will be triggered relative to 00:00:00 UTC on the
 *        specified date. For example, if &quot;DTSTART:19980205, then the duration
 *        trigger will be relative to19980205T000000Z.
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          trigger    = &quot;TRIGGER&quot; (trigrel / trigabs)
 *
 *          trigrel    = *(
 *
 *                     ; the following are optional,
 *                     ; but MUST NOT occur more than once
 *
 *                       (&quot;;&quot; &quot;VALUE&quot; &quot;=&quot; &quot;DURATION&quot;) /
 *                       (&quot;;&quot; trigrelparam) /
 *
 *                     ; the following is optional,
 *                     ; and MAY occur more than once
 *
 *                       (&quot;;&quot; xparam)
 *                       ) &quot;:&quot;  dur-value
 *
 *          trigabs    = 1*(
 *
 *                     ; the following is REQUIRED,
 *                     ; but MUST NOT occur more than once
 *
 *                       (&quot;;&quot; &quot;VALUE&quot; &quot;=&quot; &quot;DATE-TIME&quot;) /
 *
 *                     ; the following is optional,
 *                     ; and MAY occur more than once
 *
 *                       (&quot;;&quot; xparam)
 *
 *                       ) &quot;:&quot; date-time
 * </pre>
 *
 * @author Ben Fortuna
 */
public class Trigger extends DateProperty<Instant> {

    private static final long serialVersionUID = 5049421499261722194L;

    private TemporalAmountAdapter duration;

    private final Validator<Trigger> validator = new PropertyValidator<>(
            new ValidationRule<>(OneOrLess, VALUE),
            new ValidationRule<>(None, (Predicate<Trigger> & Serializable) Trigger::isAbsolute, RELATED)
    );

    /**
     * Default constructor.
     */
    public Trigger() {
        this(Instant.now());
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Trigger(final ParameterList aList, final String aValue) {
        super(TRIGGER, aList, CalendarDateFormat.UTC_DATE_TIME_FORMAT, Value.DATE_TIME);
        setValue(aValue);
    }

    /**
     * @param duration a duration in milliseconds
     */
    @Deprecated
    public Trigger(final Dur duration) {
        this(TemporalAmountAdapter.from(duration));
    }

    /**
     * @param duration a duration in milliseconds
     */
    public Trigger(final TemporalAmount duration) {
        this(new TemporalAmountAdapter(duration));
    }

    private Trigger(final TemporalAmountAdapter duration) {
        super(TRIGGER, CalendarDateFormat.UTC_DATE_TIME_FORMAT, Value.DATE_TIME);
        this.duration = duration;
    }

    /**
     * @param aList    a list of parameters for this component
     * @param duration a duration in milliseconds
     */
    @Deprecated
    public Trigger(final ParameterList aList, final Dur duration) {
        this(aList, TemporalAmountAdapter.from(duration));
    }

    /**
     * @param aList    a list of parameters for this component
     * @param duration a duration in milliseconds
     */
    public Trigger(final ParameterList aList, final TemporalAmount duration) {
        this(aList, new TemporalAmountAdapter(duration));
    }

    private Trigger(final ParameterList aList, final TemporalAmountAdapter duration) {
        super(TRIGGER, aList, CalendarDateFormat.UTC_DATE_TIME_FORMAT, Value.DURATION);
        this.duration = duration;
    }

    /**
     * @param dateTime a date representation of a date-time
     */
    public Trigger(final Instant dateTime) {
        super(TRIGGER, CalendarDateFormat.UTC_DATE_TIME_FORMAT, Value.DURATION);
        setDate(dateTime);
    }

    /**
     * @param aList    a list of parameters for this component
     * @param dateTime a date representation of a date-time
     */
    public Trigger(final ParameterList aList, final Instant dateTime) {
        super(TRIGGER, aList, CalendarDateFormat.UTC_DATE_TIME_FORMAT, Value.DURATION);
        setDate(dateTime);
    }

    /**
     * Indicates whether the trigger is relative or absolute.
     * @return true if the trigger is absolute
     */
    public boolean isAbsolute() {
        return Optional.of(Value.DATE_TIME).equals(getParameters().getFirst(VALUE));
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        super.validate();

        validator.validate(this);
    }

    /**
     * @return Returns the duration.
     */
    public final TemporalAmount getDuration() {
        if (duration != null) {
            return duration.getDuration();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        try {
            super.setValue(aValue);
            duration = null;
        } catch (DateTimeParseException pe) {
            LoggerFactory.getLogger(Trigger.class).debug(String.format("Not a valid DATE-TIME value: %s", aValue));
            duration = TemporalAmountAdapter.parse(aValue);
            super.setDate(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        if (duration != null) {
            return duration.toString();
        }
        return super.getValue();
    }

    /**
     * @param dateTime The dateTime to set.
     */
    public void setDate(final Instant dateTime) {
        super.setDate(dateTime);
        duration = null;

        setParameters((ParameterList) getParameters().replace(Value.DATE_TIME));
    }

    /**
     * @param duration The duration to set.
     */
    public final void setDuration(final TemporalAmount duration) {
        this.duration = new TemporalAmountAdapter(duration);
        super.setDate(null);
        // duration is the default value type for Trigger..
        setParameters((ParameterList) getParameters().replace(Value.DURATION));
    }

    @Override
    protected PropertyFactory<Trigger> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Trigger> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(TRIGGER);
        }

        public Trigger createProperty(final ParameterList parameters, final String value) {
            return new Trigger(parameters, value);
        }

        public Trigger createProperty() {
            return new Trigger();
        }
    }
}
