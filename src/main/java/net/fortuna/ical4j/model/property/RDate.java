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
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import net.fortuna.ical4j.validate.property.RecurrencePropertyValidators;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines an RDATE iCalendar component property.
 * <p/>
 * <pre>
 *     4.8.5.3 Recurrence Date/Times
 *
 *        Property Name: RDATE
 *
 *        Purpose: This property defines the list of date/times for a
 *        recurrence set.
 *
 *        Value Type: The default value type for this property is DATE-TIME.
 *        The value type can be set to DATE or PERIOD.
 *
 *        Property Parameters: Non-standard, value data type and time zone
 *        identifier property parameters can be specified on this property.
 *
 *        Conformance: The property can be specified in &quot;VEVENT&quot;, &quot;VTODO&quot;,
 *        &quot;VJOURNAL&quot; or &quot;VTIMEZONE&quot; calendar components.
 *
 *        Description: This property can appear along with the &quot;RRULE&quot; property
 *        to define an aggregate set of repeating occurrences. When they both
 *        appear in an iCalendar object, the recurring events are defined by
 *        the union of occurrences defined by both the &quot;RDATE&quot; and &quot;RRULE&quot;.
 *
 *        The recurrence dates, if specified, are used in computing the
 *        recurrence set. The recurrence set is the complete set of recurrence
 *        instances for a calendar component. The recurrence set is generated
 *        by considering the initial &quot;DTSTART&quot; property along with the &quot;RRULE&quot;,
 *        &quot;RDATE&quot;, &quot;EXDATE&quot; and &quot;EXRULE&quot; properties contained within the
 *        iCalendar object. The &quot;DTSTART&quot; property defines the first instance
 *        in the recurrence set. Multiple instances of the &quot;RRULE&quot; and &quot;EXRULE&quot;
 *        properties can also be specified to define more sophisticated
 *        recurrence sets. The final recurrence set is generated by gathering
 *        all of the start date/times generated by any of the specified &quot;RRULE&quot;
 *        and &quot;RDATE&quot; properties, and excluding any start date/times which fall
 *        within the union of start date/times generated by any specified
 *        &quot;EXRULE&quot; and &quot;EXDATE&quot; properties. This implies that start date/times
 *        within exclusion related properties (i.e., &quot;EXDATE&quot; and &quot;EXRULE&quot;)
 *        take precedence over those specified by inclusion properties (i.e.,
 *        &quot;RDATE&quot; and &quot;RRULE&quot;). Where duplicate instances are generated by the
 *        &quot;RRULE&quot; and &quot;RDATE&quot; properties, only one recurrence is considered.
 *        Duplicate instances are ignored.
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          rdate      = &quot;RDATE&quot; rdtparam &quot;:&quot; rdtval *(&quot;,&quot; rdtval) CRLF
 *
 *          rdtparam   = *(
 *
 *                     ; the following are optional,
 *                     ; but MUST NOT occur more than once
 *
 *                     (&quot;;&quot; &quot;VALUE&quot; &quot;=&quot; (&quot;DATE-TIME&quot;
 *                      / &quot;DATE&quot; / &quot;PERIOD&quot;)) /
 *                     (&quot;;&quot; tzidparam) /
 *
 *                     ; the following is optional,
 *                     ; and MAY occur more than once
 *
 *                     (&quot;;&quot; xparam)
 *
 *                     )
 *
 *          rdtval     = date-time / date / period
 *          ;Value MUST match value type
 *
 *        Example: The following are examples of this property:
 *
 *          RDATE:19970714T123000Z
 *
 *          RDATE;TZID=US-EASTERN:19970714T083000
 *
 *          RDATE;VALUE=PERIOD:19960403T020000Z/19960403T040000Z,
 *           19960404T010000Z/PT3H
 *
 *          RDATE;VALUE=DATE:19970101,19970120,19970217,19970421
 *           19970526,19970704,19970901,19971014,19971128,19971129,19971225
 * </pre>
 *
 * @author Ben Fortuna
 */
public class RDate<T extends Temporal> extends DateListProperty<T> {

    private static final long serialVersionUID = -3320381650013860193L;

    private PeriodList<T> periods;

    /**
     * Default constructor.
     */
    public RDate() {
        super(RDATE);
        periods = null;
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public RDate(final ParameterList aList, final String aValue) {
        super(RDATE, aList, Value.DATE_TIME);
        periods = null;
        setValue(aValue);
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence of a VALUE parameter.
     *
     * @param dates a list of dates
     */
    public RDate(final DateList<T> dates) {
        super(RDATE, dates);
        periods = null;
    }

    /**
     * Constructor. Date or Date-Time format is determined based on the presence of a VALUE parameter.
     *
     * @param aList a list of parameters for this component
     * @param dates a list of dates
     */
    public RDate(final ParameterList aList, final DateList<T> dates) {
        super(RDATE, aList, dates, Value.DATE_TIME);
        periods = null;
    }

    /**
     * Constructor.
     *
     * @param periods a list of periods
     */
    public RDate(final List<Period<T>> periods) {
        super(RDATE, new DateList<>());
        this.periods = new PeriodList<>(periods);
    }

    /**
     * Constructor.
     *
     * @param aList   a list of parameters for this component
     * @param periods a list of periods
     */
    public RDate(final ParameterList aList, final List<Period<T>> periods) {
        super(RDATE, aList, new DateList<>(), Value.DATE_TIME);
        this.periods = new PeriodList<>(periods);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResult validate() throws ValidationException {
        return RecurrencePropertyValidators.RDATE.validate(this);
    }

    /**
     * @return Returns the period list.
     */
    public final Optional<Set<Period<T>>> getPeriods() {
        if (periods != null) {
            return Optional.of(periods.getPeriods());
        } else {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setValue(final String aValue) {
        if (getParameter(Parameter.VALUE).equals(Optional.of(Value.PERIOD))) {
            periods = PeriodList.parse(aValue);
        } else {
            super.setValue(aValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        if (periods != null) {
            return Strings.valueOf(periods);
        }
        return super.getValue();
    }

    @Override
    protected PropertyFactory<RDate<T>> newFactory() {
        return new Factory<>();
    }

    public static class Factory<T extends Temporal> extends Content.Factory implements PropertyFactory<RDate<T>> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(RDATE);
        }

        @Override
        public RDate<T> createProperty(final ParameterList parameters, final String value) {
            return new RDate<>(parameters, value);
        }

        @Override
        public RDate<T> createProperty() {
            return new RDate<>();
        }
    }

}
