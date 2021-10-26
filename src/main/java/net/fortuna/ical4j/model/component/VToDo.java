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
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.component.VToDoValidator;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.fortuna.ical4j.model.Property.*;
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
public class VToDo extends CalendarComponent implements ComponentContainer<Component> {

    private static final long serialVersionUID = -269658210065896668L;

    private static final Map<Method, Validator> methodValidators = new HashMap<Method, Validator>();
    static {
        methodValidators.put(Method.ADD, new VToDoValidator(new ValidationRule(One, DTSTAMP, ORGANIZER, PRIORITY, SEQUENCE, SUMMARY, UID),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RESOURCES, STATUS, URL),
                new ValidationRule(None, RECURRENCE_ID, REQUEST_STATUS)));
        methodValidators.put(Method.CANCEL, new VToDoValidator(false, new ValidationRule(One, UID, DTSTAMP, ORGANIZER, SEQUENCE),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, PRIORITY, STATUS, URL),
                new ValidationRule(None, REQUEST_STATUS)));
        methodValidators.put(Method.COUNTER, new VToDoValidator(new ValidationRule(OneOrMore, ATTENDEE),
                new ValidationRule(One, DTSTAMP, ORGANIZER, PRIORITY, SUMMARY, UID),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, RRULE, SEQUENCE, STATUS,
                        URL)));
        methodValidators.put(Method.DECLINE_COUNTER, new VToDoValidator(false, new ValidationRule(OneOrMore, ATTENDEE),
                new ValidationRule(One, DTSTAMP, ORGANIZER, SEQUENCE, UID),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, LOCATION, PERCENT_COMPLETE, PRIORITY, RECURRENCE_ID, RESOURCES, STATUS,
                        URL)));
        methodValidators.put(Method.PUBLISH, new VToDoValidator(new ValidationRule(One, DTSTAMP, SUMMARY, UID),
                new ValidationRule(One, true, ORGANIZER, PRIORITY),
                new ValidationRule(OneOrLess, DTSTART, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DUE, DURATION,
                        GEO, LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, STATUS, URL),
                new ValidationRule(None, ATTENDEE, REQUEST_STATUS)));
        methodValidators.put(Method.REFRESH, new VToDoValidator(false, new ValidationRule(One, ATTENDEE, DTSTAMP, UID),
                new ValidationRule(OneOrLess, RECURRENCE_ID),
                new ValidationRule(None, ATTACH, CATEGORIES, CLASS, CONTACT, CREATED, DESCRIPTION, DTSTART, DUE,
                        DURATION, EXDATE, EXRULE, GEO, LAST_MODIFIED, LOCATION, ORGANIZER, PERCENT_COMPLETE, PRIORITY,
                        RDATE, RELATED_TO, REQUEST_STATUS, RESOURCES, RRULE, SEQUENCE, STATUS, URL)));
        methodValidators.put(Method.REPLY, new VToDoValidator(false, new ValidationRule(OneOrMore, ATTENDEE),
                new ValidationRule(One, DTSTAMP, ORGANIZER, UID),
                new ValidationRule(OneOrLess, CATEGORIES, CLASS, CREATED, DESCRIPTION, DTSTART, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, PRIORITY, RESOURCES, RECURRENCE_ID, SEQUENCE, STATUS,
                        SUMMARY, URL)));
        methodValidators.put(Method.REQUEST, new VToDoValidator(new ValidationRule(OneOrMore, ATTENDEE),
                new ValidationRule(One, DTSTAMP, DTSTART, ORGANIZER, PRIORITY, SUMMARY, UID),
                new ValidationRule(OneOrLess, SEQUENCE, CATEGORIES, CLASS, CREATED, DESCRIPTION, DUE, DURATION, GEO,
                        LAST_MODIFIED, LOCATION, PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, STATUS, URL),
                new ValidationRule(None, REQUEST_STATUS)));
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
            getProperties().add(new DtStamp());
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
    public VToDo(final Date start, final String summary) {
        this();
        getProperties().add(new DtStart(start));
        getProperties().add(new Summary(summary));
    }

    /**
     * Constructs a new VTODO instance starting and ending at the specified times with the specified summary.
     * @param start the start date of the new todo
     * @param due the due date of the new todo
     * @param summary the todo summary
     */
    public VToDo(final Date start, final Date due, final String summary) {
        this();
        getProperties().add(new DtStart(start));
        getProperties().add(new Due(due));
        getProperties().add(new Summary(summary));
    }

    /**
     * Constructs a new VTODO instance starting at the specified times, for the specified duration, with the specified
     * summary.
     * @param start the start date of the new todo
     * @param duration the duration of the new todo
     * @param summary the todo summary
     */
    public VToDo(final Date start, final TemporalAmount duration, final String summary) {
        this();
        getProperties().add(new DtStart(start));
        getProperties().add(new Duration(duration));
        getProperties().add(new Summary(summary));
    }

    /**
     * Returns the list of alarms for this todo.
     * @return a component list
     */
    public final ComponentList<VAlarm> getAlarms() {
        return (ComponentList<VAlarm>) components;
    }

    @Override
    public ComponentList<Component> getComponents() {
        return (ComponentList<Component>) components;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return BEGIN +
                ':' +
                getName() +
                Strings.LINE_SEPARATOR +
                getProperties() +
                getAlarms() +
                END +
                ':' +
                getName() +
                Strings.LINE_SEPARATOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(final boolean recurse) throws ValidationException {
        ComponentValidator.VTODO.validate(this);
        // validate that getAlarms() only contains VAlarm components
        for (VAlarm component : getAlarms()) {
            component.validate(recurse);
        }

        final Status status = getProperty(Property.STATUS);
        if (status != null && !Status.VTODO_NEEDS_ACTION.getValue().equals(status.getValue())
                && !Status.VTODO_COMPLETED.getValue().equals(status.getValue())
                && !Status.VTODO_IN_PROCESS.getValue().equals(status.getValue())
                && !Status.VTODO_CANCELLED.getValue().equals(status.getValue())) {
            throw new ValidationException("Status property ["
                    + status.toString() + "] may not occur in VTODO");
        }

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Validator getValidator(Method method) {
        return methodValidators.get(method);
    }

    /**
     * @return the optional access classification property
     */
    public final Clazz getClassification() {
        return getProperty(Property.CLASS);
    }

    /**
     * @return the optional date completed property
     */
    public final Completed getDateCompleted() {
        return getProperty(Property.COMPLETED);
    }

    /**
     * @return the optional creation-time property
     */
    public final Created getCreated() {
        return getProperty(Property.CREATED);
    }

    /**
     * @return the optional description property
     */
    public final Description getDescription() {
        return getProperty(Property.DESCRIPTION);
    }

    /**
     * Convenience method to pull the DTSTART out of the property list.
     * @return The DtStart object representation of the start Date
     */
    public final DtStart getStartDate() {
        return getProperty(Property.DTSTART);
    }

    /**
     * @return the optional geographic position property
     */
    public final Geo getGeographicPos() {
        return getProperty(Property.GEO);
    }

    /**
     * @return the optional last-modified property
     */
    public final LastModified getLastModified() {
        return getProperty(Property.LAST_MODIFIED);
    }

    /**
     * @return the optional location property
     */
    public final Location getLocation() {
        return getProperty(Property.LOCATION);
    }

    /**
     * @return the optional organizer property
     */
    public final Organizer getOrganizer() {
        return getProperty(Property.ORGANIZER);
    }

    /**
     * @return the optional percentage complete property
     */
    public final PercentComplete getPercentComplete() {
        return getProperty(Property.PERCENT_COMPLETE);
    }

    /**
     * @return the optional priority property
     */
    public final Priority getPriority() {
        return getProperty(Property.PRIORITY);
    }

    /**
     * @return the optional date-stamp property
     */
    public final DtStamp getDateStamp() {
        return getProperty(Property.DTSTAMP);
    }

    /**
     * @return the optional sequence number property
     */
    public final Sequence getSequence() {
        return getProperty(Property.SEQUENCE);
    }

    /**
     * @return the optional status property
     */
    public final Status getStatus() {
        return getProperty(Property.STATUS);
    }

    /**
     * @return the optional summary property
     */
    public final Summary getSummary() {
        return getProperty(Property.SUMMARY);
    }

    /**
     * @return the optional URL property
     */
    public final Url getUrl() {
        return getProperty(Property.URL);
    }

    /**
     * @return the optional recurrence identifier property
     */
    public final RecurrenceId getRecurrenceId() {
        return getProperty(Property.RECURRENCE_ID);
    }

    /**
     * @return the optional Duration property
     */
    public final Duration getDuration() {
        return getProperty(Property.DURATION);
    }

    /**
     * @return the optional due property
     */
    public final Due getDue() {
        return getProperty(Property.DUE);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return getProperty(Property.UID);
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

    public static class Factory extends Content.Factory implements ComponentFactory<VToDo> {

        public Factory() {
            super(VTODO);
        }

        @Override
        public VToDo createComponent() {
            return new VToDo(false);
        }

        @Override
        public VToDo createComponent(PropertyList<Property> properties) {
            return new VToDo(properties);
        }

        @Override
        public VToDo createComponent(PropertyList properties, ComponentList subComponents) {
            return new VToDo(properties, subComponents);
        }
    }
}
