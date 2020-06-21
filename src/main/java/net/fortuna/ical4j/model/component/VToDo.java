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
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.component.VToDoValidator;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jooq.lambda.Unchecked;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.*;

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
public class VToDo extends CalendarComponent {

    private static final long serialVersionUID = -269658210065896668L;

    private final Map<Method, Validator<VToDo>> methodValidators = new HashMap<>();
    {
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
    
    private ComponentList<VAlarm> alarms = new ComponentList<>();

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
        super(VTODO, properties);
        this.alarms = alarms;
    }

    /**
     * Constructs a new VTODO instance starting at the specified time with the specified summary.
     * @param start the start date of the new todo
     * @param summary the todo summary
     */
    public VToDo(final Temporal start, final String summary) {
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
    public VToDo(final Temporal start, final Temporal due, final String summary) {
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
    public VToDo(final Temporal start, final TemporalAmount duration, final String summary) {
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
        return alarms;
    }

    /**
     * {@inheritDoc}
     */
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
    public final void validate(final boolean recurse) throws ValidationException {

        // validate that getAlarms() only contains VAlarm components
        for (VAlarm component : getAlarms()) {
            component.validate(recurse);
        }

        if (!CompatibilityHints
                .isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {

            // From "4.8.4.7 Unique Identifier":
            // Conformance: The property MUST be specified in the "VEVENT", "VTODO",
            // "VJOURNAL" or "VFREEBUSY" calendar components.
            PropertyValidator.assertOne(Property.UID,
                    getProperties());

            // From "4.8.7.2 Date/Time Stamp":
            // Conformance: This property MUST be included in the "VEVENT", "VTODO",
            // "VJOURNAL" or "VFREEBUSY" calendar components.
            PropertyValidator.assertOne(Property.DTSTAMP,
                    getProperties());
        }

        /*
         * ; the following are optional, ; but MUST NOT occur more than once class / completed / created / description /
         * dtstamp / dtstart / geo / last-mod / location / organizer / percent / priority / recurid / seq / status /
         * summary / uid / url /
         */
        Arrays.asList(Property.CLASS, Property.COMPLETED, Property.CREATED, Property.DESCRIPTION,
                Property.DTSTAMP, Property.DTSTART, Property.GEO, Property.LAST_MODIFIED, Property.LOCATION, Property.ORGANIZER,
                Property.PERCENT_COMPLETE, Property.PRIORITY, Property.RECURRENCE_ID, Property.SEQUENCE, Property.STATUS,
                Property.SUMMARY, Property.UID, Property.URL).forEach(property -> PropertyValidator.assertOneOrLess(property, getProperties()));

        final Optional<Status> status = getProperty(Property.STATUS);
        if (status.isPresent() && !Status.VTODO_NEEDS_ACTION.getValue().equals(status.get().getValue())
                && !Status.VTODO_COMPLETED.getValue().equals(status.get().getValue())
                && !Status.VTODO_IN_PROCESS.getValue().equals(status.get().getValue())
                && !Status.VTODO_CANCELLED.getValue().equals(status.get().getValue())) {
            throw new ValidationException("Status property ["
                    + status.toString() + "] may not occur in VTODO");
        }

        /*
         * ; either 'due' or 'duration' may appear in ; a 'todoprop', but 'due' and 'duration' ; MUST NOT occur in the
         * same 'todoprop' due / duration /
         */
        try {
            PropertyValidator.assertNone(Property.DUE,
                    getProperties());
        }
        catch (ValidationException ve) {
            PropertyValidator.assertNone(Property.DURATION,
                    getProperties());
        }

        /*
         * ; the following are optional, ; and MAY occur more than once attach / attendee / categories / comment /
         * contact / exdate / exrule / rstatus / related / resources / rdate / rrule / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * Performs method-specific ITIP validation.
     * @param method the applicable method
     * @throws ValidationException where the component does not comply with RFC2446
     */
    public void validate(Method method) throws ValidationException {
        final Validator<VToDo> validator = methodValidators.get(method);
        if (validator != null) {
            validator.validate(this);
        }
        else {
            super.validate(method);
        }
    }

    /**
     * @return the optional access classification property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Clazz> getClassification() {
        return getProperty(Property.CLASS);
    }

    /**
     * @return the optional date completed property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Completed> getDateCompleted() {
        return getProperty(Property.COMPLETED);
    }

    /**
     * @return the optional creation-time property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Created> getCreated() {
        return getProperty(Property.CREATED);
    }

    /**
     * @return the optional description property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Description> getDescription() {
        return getProperty(Property.DESCRIPTION);
    }

    /**
     * Convenience method to pull the DTSTART out of the property list.
     * @return The DtStart object representation of the start Date
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<DtStart<?>> getStartDate() {
        return getProperty(Property.DTSTART);
    }

    /**
     * @return the optional geographic position property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Geo> getGeographicPos() {
        return getProperty(Property.GEO);
    }

    /**
     * @return the optional last-modified property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<LastModified> getLastModified() {
        return getProperty(Property.LAST_MODIFIED);
    }

    /**
     * @return the optional location property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Location> getLocation() {
        return getProperty(Property.LOCATION);
    }

    /**
     * @return the optional organizer property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Organizer> getOrganizer() {
        return getProperty(Property.ORGANIZER);
    }

    /**
     * @return the optional percentage complete property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<PercentComplete> getPercentComplete() {
        return getProperty(Property.PERCENT_COMPLETE);
    }

    /**
     * @return the optional priority property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Priority> getPriority() {
        return getProperty(Property.PRIORITY);
    }

    /**
     * @return the optional date-stamp property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<DtStamp> getDateStamp() {
        return getProperty(Property.DTSTAMP);
    }

    /**
     * @return the optional sequence number property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Sequence> getSequence() {
        return getProperty(Property.SEQUENCE);
    }

    /**
     * @return the optional status property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Status> getStatus() {
        return getProperty(Property.STATUS);
    }

    /**
     * @return the optional summary property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Summary> getSummary() {
        return getProperty(Property.SUMMARY);
    }

    /**
     * @return the optional URL property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Url> getUrl() {
        return getProperty(Property.URL);
    }

    /**
     * @return the optional recurrence identifier property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<RecurrenceId<?>> getRecurrenceId() {
        return getProperty(Property.RECURRENCE_ID);
    }

    /**
     * @return the optional Duration property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Duration> getDuration() {
        return getProperty(Property.DURATION);
    }

    /**
     * @return the optional due property
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Due<?>> getDue() {
        return getProperty(Property.DUE);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     * @deprecated use {@link VToDo#getProperty(String)}
     */
    @Deprecated
    public final Optional<Uid> getUid() {
        return getProperty(Property.UID);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object arg0) {
        if (arg0 instanceof VToDo) {
            return super.equals(arg0)
                    && Objects.equals(alarms, ((VToDo) arg0).getAlarms());
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getProperties())
                .append(getAlarms()).toHashCode();
    }

    /**
     * Overrides default copy method to add support for copying alarm sub-components.
     * @return a copy of the instance
     * @see net.fortuna.ical4j.model.Component#copy()
     */
    public VToDo copy() {
        return new Factory().createComponent(getProperties(), getAlarms());
    }

    @Override
    protected ComponentFactory<VToDo> newFactory() {
        return new Factory();
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

        @Override
        public VToDo createComponent(PropertyList properties, ComponentList subComponents) {
            return new VToDo(properties, subComponents);
        }
    }
}
