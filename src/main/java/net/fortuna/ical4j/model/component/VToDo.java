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
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.validate.*;
import net.fortuna.ical4j.validate.component.VToDoValidator;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableStatus.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines an iCalendar VTODO component.
 *
 * <pre>
 *       4.6.2 To-do Component
 *
 *          Component Name: VTODO
 *
 *          Purpose: Provide a grouping of calendar properties that describe a
 *          to-do.
 *
 *          Formal Definition: A &quot;VTODO&quot; calendar component is defined by the
 *          following notation:
 *
 *            todoc      = &quot;BEGIN&quot; &quot;:&quot; &quot;VTODO&quot; CRLF
 *                         todoprop *alarmc
 *                         &quot;END&quot; &quot;:&quot; &quot;VTODO&quot; CRLF
 *
 *            todoprop   = *(
 *
 *                       ; the following are optional,
 *                       ; but MUST NOT occur more than once
 *
 *                       class / completed / created / description / dtstamp /
 *                       dtstart / geo / last-mod / location / organizer /
 *                       percent / priority / recurid / seq / status /
 *                       summary / uid / url /
 *
 *                       ; either 'due' or 'duration' may appear in
 *                       ; a 'todoprop', but 'due' and 'duration'
 *                       ; MUST NOT occur in the same 'todoprop'
 *
 *                       due / duration /
 *
 *                       ; the following are optional,
 *                       ; and MAY occur more than once
 *                       attach / attendee / categories / comment / contact /
 *                       exdate / exrule / rstatus / related / resources /
 *                       rdate / rrule / x-prop
 *
 *                       )
 * </pre>
 *
 * Example 1 - Creating a todo of two (2) hour duration starting tomorrow:
 *
 * <pre><code>
 * java.util.Calendar cal = java.util.Calendar.getInstance();
 * // tomorrow..
 * cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
 * cal.set(java.util.Calendar.HOUR_OF_DAY, 11);
 * cal.set(java.util.Calendar.MINUTE, 00);
 *
 * VToDo documentation = new VEvent(cal.getTime(), 1000 * 60 * 60 * 2,
 *         &quot;Document calendar component usage&quot;);
 *
 * // add timezone information..
 * VTimeZone tz = VTimeZone.getDefault();
 * TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID)
 *         .getValue());
 * documentation.getProperties().getProperty(Property.DTSTART).getParameters()
 *         .add(tzParam);
 * </code></pre>
 *
 * @author Ben Fortuna
 */
public class VToDo extends CalendarComponent implements Prototype<VToDo>, ComponentContainer<Component>, RecurrenceSupport<VToDo>,
        DescriptivePropertyAccessor, ChangeManagementPropertyAccessor, DateTimePropertyAccessor,
        RelationshipPropertyAccessor, AlarmsAccessor, ParticipantsAccessor, LocationsAccessor, ResourcesAccessor {

    private static final long serialVersionUID = -269658210065896668L;

    private static final Map<Method, Validator<VToDo>> methodValidators = new HashMap<>();
    static {
        methodValidators.put(ADD, new VToDoValidator(new ValidationRule<>(One, DTSTAMP, ORGANIZER, PRIORITY, SEQUENCE, SUMMARY, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RESOURCES, STATUS, URL),
                new ValidationRule<>(None, RECURRENCE_ID, REQUEST_STATUS)));
        methodValidators.put(CANCEL, new VToDoValidator(false, new ValidationRule<>(One, UID, DTSTAMP, ORGANIZER, SEQUENCE),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, PRIORITY, STATUS, URL),
                new ValidationRule<>(None, REQUEST_STATUS)));
        methodValidators.put(COUNTER, new VToDoValidator(new ValidationRule<>(OneOrMore, ATTENDEE),
                new ValidationRule<>(One, DTSTAMP, ORGANIZER, PRIORITY, SUMMARY, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, RRULE, SEQUENCE, STATUS,
                        URL)));
        methodValidators.put(DECLINE_COUNTER, new VToDoValidator(false, new ValidationRule<>(OneOrMore, ATTENDEE),
                new ValidationRule<>(One, DTSTAMP, ORGANIZER, SEQUENCE, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, LOCATION, PERCENT_COMPLETE, PRIORITY, RECURRENCE_ID, RESOURCES, STATUS,
                        URL)));
        methodValidators.put(PUBLISH, new VToDoValidator(new ValidationRule<>(One, DTSTAMP, SUMMARY, UID),
                new ValidationRule<>(One, true, ORGANIZER, PRIORITY),
                new ValidationRule<>(OneOrLess, DTSTART, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DUE, DURATION,
                        GEO, LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, STATUS, URL),
                new ValidationRule<>(None, ATTENDEE, REQUEST_STATUS)));
        methodValidators.put(REFRESH, new VToDoValidator(false, new ValidationRule<>(One, ATTENDEE, DTSTAMP, UID),
                new ValidationRule<>(OneOrLess, RECURRENCE_ID),
                new ValidationRule<>(None, ATTACH, CATEGORIES, CLASS, CONTACT, CREATED, DESCRIPTION, DTSTART, DUE,
                        DURATION, EXDATE, EXRULE, GEO, LAST_MODIFIED, LOCATION, ORGANIZER, PERCENT_COMPLETE, PRIORITY,
                        RDATE, RELATED_TO, REQUEST_STATUS, RESOURCES, RRULE, SEQUENCE, STATUS, URL)));
        methodValidators.put(REPLY, new VToDoValidator(false, new ValidationRule<>(OneOrMore, ATTENDEE),
                new ValidationRule<>(One, DTSTAMP, ORGANIZER, UID),
                new ValidationRule<>(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, PRIORITY, RESOURCES, RECURRENCE_ID, SEQUENCE, STATUS,
                        SUMMARY, URL)));
        methodValidators.put(REQUEST, new VToDoValidator(new ValidationRule<>(OneOrMore, ATTENDEE),
                new ValidationRule<>(One, DTSTAMP, DTSTART, ORGANIZER, PRIORITY, SUMMARY, UID),
                new ValidationRule<>(OneOrLess, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, STATUS, URL),
                new ValidationRule<>(None, REQUEST_STATUS)));
    }

    /**
     * Default constructor.
     */
    public VToDo() {
        this(true);
    }

    public VToDo(boolean initialise) {
        super(VTODO);
        if (initialise) {
            add(new DtStamp());
        }
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VToDo(final PropertyList properties) {
        super(VTODO, properties);
    }

    public VToDo(PropertyList properties, ComponentList<VAlarm> alarms) {
        super(VTODO, properties, alarms);
    }

    /**
     * Constructs a new VTODO instance starting at the specified time with the specified summary.
     * @param start the start date of the new todo
     * @param summary the todo summary
     */
    public VToDo(final Temporal start, final String summary) {
        this();
        add(new DtStart<>(start));
        add(new Summary(summary));
    }

    /**
     * Constructs a new VTODO instance starting and ending at the specified times with the specified summary.
     * @param start the start date of the new todo
     * @param due the due date of the new todo
     * @param summary the todo summary
     */
    public VToDo(final Temporal start, final Temporal due, final String summary) {
        this();
        add(new DtStart<>(start));
        add(new Due<>(due));
        add(new Summary(summary));
    }

    /**
     * Constructs a new VTODO instance starting at the specified times, for the specified duration, with the specified
     * summary.
     * @param start the start date of the new todo
     * @param duration the duration of the new todo
     * @param summary the todo summary
     */
    public VToDo(final Temporal start, final TemporalAmount duration, final String summary) {
        this();
        add(new DtStart<>(start));
        add(new Duration(duration));
        add(new Summary(summary));
    }

    /**
     *
     * @return Returns the underlying component list.
     */
    @Override
    public ComponentList<Component> getComponentList() {
        //noinspection unchecked
        return (ComponentList<Component>) components;
    }

    @Override
    public void setComponentList(ComponentList<Component> components) {
        this.components = components;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResult validate(final boolean recurse) throws ValidationException {
        var result = ComponentValidator.VTODO.validate(this);
        // validate that getAlarms() only contains VAlarm components
        for (var component : getAlarms()) {
            component.validate(recurse);
        }

        final Optional<Status> status = getStatus();
        if (status.isPresent() && !VTODO_NEEDS_ACTION.equals(status.get())
                && !VTODO_COMPLETED.equals(status.get())
                && !VTODO_IN_PROCESS.equals(status.get())
                && !VTODO_CANCELLED.equals(status.get())) {
            throw new ValidationException("Status property ["
                    + status + "] may not occur in VTODO");
        }

        if (recurse) {
            result = result.merge(validateProperties());
        }
        return result;
    }

    /**
     * Performs method-specific ITIP validation.
     * @param method the applicable method
     * @throws ValidationException where the component does not comply with RFC2446
     */
    @Override
    public ValidationResult validate(Method method) throws ValidationException {
        final Validator<VToDo> validator = methodValidators.get(method);
        if (validator != null) {
            return validator.validate(this);
        }
        else {
            return super.validate(method);
        }
    }

    /**
     * @return the optional date completed property
     * @deprecated use {@link DateTimePropertyAccessor#getDateTimeCompleted()}
     */
    @Deprecated
    public final Optional<Completed> getDateCompleted() {
        return getDateTimeCompleted();
    }

    /**
     * Convenience method to pull the DTSTART out of the property list.
     * @return The DtStart object representation of the start Date
     * @deprecated use {@link DateTimePropertyAccessor#getDateTimeStart()}
     */
    @Deprecated
    public final <T extends Temporal> Optional<DtStart<T>> getStartDate() {
        return getDateTimeStart();
    }

    /**
     * @return the date-stamp property
     * @deprecated use {@link ChangeManagementPropertyAccessor#getDateTimeStamp()}
     */
    @Deprecated
    public final Optional<DtStamp> getDateStamp() {
        return getDateTimeStamp();
    }

    /**
     * @return the optional due property
     * @deprecated use {@link DateTimePropertyAccessor#getDateTimeDue()}
     */
    @Deprecated
    public final <T extends Temporal> Optional<Due<T>> getDue() {
        return getDateTimeDue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object arg0) {
        if (arg0 instanceof VToDo) {
            return super.equals(arg0)
                    && Objects.equals(getAlarms(), ((VToDo) arg0).getAlarms());
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getProperties())
                .append(getAlarms()).toHashCode();
    }

    @Override
    protected ComponentFactory<VToDo> newFactory() {
        return new Factory();
    }

    @Override
    public VToDo copy() {
        return newFactory().createComponent(new PropertyList(getProperties().parallelStream()
                        .map(Property::copy).collect(Collectors.toList())),
                new ComponentList<>(getComponents().parallelStream()
                        .map(Component::copy).collect(Collectors.toList())));
    }

    public static class Factory extends Content.Factory implements ComponentFactory<VToDo> {

        public Factory() {
            super(VTODO);
        }

        @Override
        public VToDo createComponent() {
            return new VToDo(false);
        }

        @Override
        public VToDo createComponent(PropertyList properties) {
            return new VToDo(properties);
        }

        @Override @SuppressWarnings("unchecked")
        public VToDo createComponent(PropertyList properties, ComponentList<?> subComponents) {
            return new VToDo(properties, (ComponentList<VAlarm>) subComponents);
        }
    }
}
