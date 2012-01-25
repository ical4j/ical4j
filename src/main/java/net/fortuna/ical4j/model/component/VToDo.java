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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.Validator;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Completed;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.ComponentValidator;
import net.fortuna.ical4j.util.PropertyValidator;
import net.fortuna.ical4j.util.Strings;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

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

    private final Map methodValidators = new HashMap();
    {
        methodValidators.put(Method.ADD, new AddValidator());
        methodValidators.put(Method.CANCEL, new CancelValidator());
        methodValidators.put(Method.COUNTER, new CounterValidator());
        methodValidators.put(Method.DECLINE_COUNTER, new DeclineCounterValidator());
        methodValidators.put(Method.PUBLISH, new PublishValidator());
        methodValidators.put(Method.REFRESH, new RefreshValidator());
        methodValidators.put(Method.REPLY, new ReplyValidator());
        methodValidators.put(Method.REQUEST, new RequestValidator());
    }
    
    private ComponentList alarms = new ComponentList();

    /**
     * Default constructor.
     */
    public VToDo() {
        super(VTODO);
        getProperties().add(new DtStamp());
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VToDo(final PropertyList properties) {
        super(VTODO, properties);
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
    public VToDo(final Date start, final Dur duration, final String summary) {
        this();
        getProperties().add(new DtStart(start));
        getProperties().add(new Duration(duration));
        getProperties().add(new Summary(summary));
    }

    /**
     * Returns the list of alarms for this todo.
     * @return a component list
     */
    public final ComponentList getAlarms() {
        return alarms;
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(BEGIN);
        buffer.append(':');
        buffer.append(getName());
        buffer.append(Strings.LINE_SEPARATOR);
        buffer.append(getProperties());
        buffer.append(getAlarms());
        buffer.append(END);
        buffer.append(':');
        buffer.append(getName());
        buffer.append(Strings.LINE_SEPARATOR);
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse)
            throws ValidationException {

        // validate that getAlarms() only contains VAlarm components
        final Iterator iterator = getAlarms().iterator();
        while (iterator.hasNext()) {
            final Component component = (Component) iterator.next();
            if (!(component instanceof VAlarm)) {
                throw new ValidationException("Component ["
                        + component.getName() + "] may not occur in VTODO");
            }
            ((VAlarm) component).validate(recurse);
        }

        if (!CompatibilityHints
                .isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {

            // From "4.8.4.7 Unique Identifier":
            // Conformance: The property MUST be specified in the "VEVENT", "VTODO",
            // "VJOURNAL" or "VFREEBUSY" calendar components.
            PropertyValidator.getInstance().assertOne(Property.UID,
                    getProperties());

            // From "4.8.7.2 Date/Time Stamp":
            // Conformance: This property MUST be included in the "VEVENT", "VTODO",
            // "VJOURNAL" or "VFREEBUSY" calendar components.
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP,
                    getProperties());
        }

        /*
         * ; the following are optional, ; but MUST NOT occur more than once class / completed / created / description /
         * dtstamp / dtstart / geo / last-mod / location / organizer / percent / priority / recurid / seq / status /
         * summary / uid / url /
         */
        PropertyValidator.getInstance().assertOneOrLess(Property.CLASS,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.COMPLETED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CREATED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DTSTAMP,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.GEO,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.ORGANIZER,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(
                Property.PERCENT_COMPLETE, getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.STATUS,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.UID,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.URL,
                getProperties());

        final Status status = (Status) getProperty(Property.STATUS);
        if (status != null && !Status.VTODO_NEEDS_ACTION.getValue().equals(status.getValue())
                && !Status.VTODO_COMPLETED.getValue().equals(status.getValue())
                && !Status.VTODO_IN_PROCESS.getValue().equals(status.getValue())
                && !Status.VTODO_CANCELLED.getValue().equals(status.getValue())) {
            throw new ValidationException("Status property ["
                    + status.toString() + "] may not occur in VTODO");
        }

        /*
         * ; either 'due' or 'duration' may appear in ; a 'todoprop', but 'due' and 'duration' ; MUST NOT occur in the
         * same 'todoprop' due / duration /
         */
        try {
            PropertyValidator.getInstance().assertNone(Property.DUE,
                    getProperties());
        }
        catch (ValidationException ve) {
            PropertyValidator.getInstance().assertNone(Property.DURATION,
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
     * {@inheritDoc}
     */
    protected Validator getValidator(Method method) {
        return (Validator) methodValidators.get(method);
    }

    /**
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD                1       MUST be "ADD"
     * VTODO                 1
     *     DTSTAMP           1
     *     ORGANIZER         1
     *     PRIORITY          1
     *     SEQUENCE          1       MUST be greater than 0
     *     SUMMARY           1       Can be null.
     *     UID               1       MUST match that of the original to-do
     * 
     *     ATTACH            0+
     *     ATTENDEE          0+
     *     CATEGORIES        0 or 1  This property may contain a list of
     *                               values
     *     CLASS             0 or 1
     *     COMMENT           0 or 1
     *     CONTACT           0+
     *     CREATED           0 or 1
     *     DESCRIPTION       0 or 1  Can be null
     *     DTSTART           0 or 1
     *     DUE               0 or 1  If present DURATION MUST NOT be present
     *     DURATION          0 or 1  If present DUE MUST NOT be present
     *     EXDATE            0+
     *     EXRULE            0+
     *     GEO               0 or 1
     *     LAST-MODIFIED     0 or 1
     *     LOCATION          0 or 1
     *     PERCENT-COMPLETE  0 or 1
     *     RDATE             0+
     *     RELATED-TO        0+
     *     RESOURCES         0 or 1  This property may contain a list of
     *                               values
     *     RRULE             0+
     *     STATUS            0 or 1  MAY be one of COMPLETED/NEEDS ACTION/IN-
     *                               PROCESS
     *     URL               0 or 1
     *     X-PROPERTY        0+
     * 
     *     RECURRENCE-ID     0
     *     REQUEST-STATUS    0
     * 
     * VALARM                0+
     * VTIMEZONE             0+      MUST be present if any date/time refers
     *                               to a timezone
     * X-COMPONENT           0+
     * 
     * VEVENT                0
     * VJOURNAL              0
     * VFREEBUSY             0
     * </pre>
     * 
     */
    private class AddValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DUE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PERCENT_COMPLETE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
            
            for (final Iterator i = getAlarms().iterator(); i.hasNext();) {
                final VAlarm alarm = (VAlarm) i.next();
                alarm.validate(Method.ADD);
            }
        }
    }
    
    /**
     * <pre>
     * Component/Property   Presence
     * -------------------  ---------------------------------------------
     * METHOD               1     MUST be "CANCEL"
     * VTODO                1
     *     ATTENDEE         0+    include all "Attendees" being removed from
     *                            the todo. MUST include all "Attendees" if
     *                            the entire todo is cancelled.
     *     UID              1     MUST echo original UID
     *     DTSTAMP          1
     *     ORGANIZER        1
     *     SEQUENCE         1
     * 
     *     ATTACH           0+
     *     CATEGORIES       0 or 1 This property MAY contain a list of values
     *     CLASS            0 or 1
     *     COMMENT          0 or 1
     *     CONTACT          0+
     *     CREATED          0 or 1
     *     DESCRIPTION      0 or 1
     *     DTSTART          0 or 1
     *     DUE              0 or 1  If present DURATION MUST NOT be present
     *     DURATION         0 or 1  If present DUE MUST NOT be present
     *     EXDATE           0+
     *     EXRULE           0+
     *     GEO              0 or 1
     *     LAST-MODIFIED    0 or 1
     *     LOCATION         0 or 1
     *     PERCENT-COMPLETE 0 or 1
     *     RDATE            0+
     *     RECURRENCE-ID    0 or 1 MUST only if referring to one or more
     *                             instances of a recurring calendar
     *                             component. Otherwise it MUST NOT be
     *                             present.
     *     RELATED-TO       0+
     *     RESOURCES        0 or 1 This property MAY contain a list of values
     *     RRULE            0+
     *     PRIORITY         0 or 1
     *     STATUS           0 or 1  If present it MUST be set to "CANCELLED".
     *                              MUST NOT be used if purpose is to remove
     *                              "ATTENDEES" rather than cancel the entire
     *                              VTODO.
     *     URL              0 or 1
     *     X-PROPERTY       0+
     * 
     *     REQUEST-STATUS   0
     * 
     * VTIMEZONE            0 or 1  MUST be present if any date/time refers to
     *                              a timezone
     * X-COMPONENT          0+
     * 
     * VALARM               0
     * VEVENT               0
     * VFREEBUSY            0
     * </pre>
     * 
     */
    private class CancelValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SEQUENCE, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DUE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PERCENT_COMPLETE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
            
            ComponentValidator.assertNone(Component.VALARM, getAlarms());
        }
    }
    
    /**
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD               1      MUST be "COUNTER"
     * VTODO                1
     *     ATTENDEE         1+
     *     DTSTAMP          1
     *     ORGANIZER        1
     *     PRIORITY         1
     *     SUMMARY          1      Can be null
     *     UID              1
     * 
     *     ATTACH           0+
     *     CATEGORIES       0 or 1 This property MAY contain a list of values
     *     CLASS            0 or 1
     *     COMMENT          0 or 1
     *     CONTACT          0+
     *     CREATED          0 or 1
     *     DESCRIPTION      0 or 1 Can be null
     *     DTSTART          0 or 1
     *     DUE              0 or 1  If present DURATION MUST NOT be present
     *     DURATION         0 or 1  If present DUE MUST NOT be present
     *     EXDATE           0+
     *     EXRULE           0+
     *     GEO              0 or 1
     *     LAST-MODIFIED    0 or 1
     *     LOCATION         0 or 1
     *     PERCENT-COMPLETE 0 or 1
     *     RDATE            0+
     *     RECURRENCE-ID    0 or 1 MUST only 3.5if referring to an instance of a
     *                             recurring calendar component.  Otherwise it
     *                             MUST NOT be present.
     *     RELATED-TO       0+
     *     REQUEST-STATUS   0+
     *     RESOURCES        0 or 1 This property MAY contain a list of values
     *     RRULE            0 or 1
     *     SEQUENCE         0 or 1 MUST echo the original SEQUENCE number.
     *                             MUST be present if non-zero. MAY be present
     *                             if zero.
     *     STATUS           0 or 1 MAY be one of COMPLETED/NEEDS ACTION/IN-
     *                             PROCESS/CANCELLED
     *     URL              0 or 1
     *     X-PROPERTY       0+
     * 
     * 
     * VALARM               0+
     * VTIMEZONE            0 or 1  MUST be present if any date/time refers to
     *                              a timezone
     * X-COMPONENT          0+
     * 
     * VEVENT               0
     * VFREEBUSY            0
     * </pre>
     * 
     */
    private class CounterValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, getProperties());
            
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DUE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PERCENT_COMPLETE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RRULE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            for (final Iterator i = getAlarms().iterator(); i.hasNext();) {
                final VAlarm alarm = (VAlarm) i.next();
                alarm.validate(Method.COUNTER);
            }
        }
    }
    
    /**
     * <pre>
     * Component/Property   Presence
     * -------------------  ---------------------------------------------
     * METHOD               1       MUST be "DECLINECOUNTER"
     * 
     * VTODO                1
     *     ATTENDEE         1+      MUST for all attendees
     *     DTSTAMP          1
     *     ORGANIZER        1
     *     SEQUENCE         1       MUST echo the original SEQUENCE number
     *     UID              1       MUST echo original UID
     *     ATTACH           0+
     *     CATEGORIES       0 or 1  This property may contain a list of values
     *     CLASS            0 or 1
     *     COMMENT          0 or 1
     *     CONTACT          0+
     *     CREATED          0 or 1
     *     DESCRIPTION      0 or 1
     *     DTSTART          0 or 1
     *     DUE              0 or 1  If present DURATION MUST NOT be present
     *     DURATION         0 or 1  If present DUE MUST NOT be present
     *     EXDATE           0+
     *     EXRULE           0+
     *     GEO              0 or 1
     *     LAST-MODIFIED    0 or 1
     *     LOCATION         0 or 1
     *     PERCENT-COMPLETE 0 or 1
     *     PRIORITY         0 or 1
     *     RDATE            0+
     *     RECURRENCE-ID    0 or 1  MUST only if referring to an instance of a
     *                              recurring calendar component.  Otherwise
     *                              it MUST NOT be present.
     *     RELATED-TO       0+
     *     REQUEST-STATUS   0+
     *     RESOURCES        0 or 1  This property MAY contain a list of values
     *     RRULE            0+
     *     STATUS           0 or 1  MAY be one of COMPLETED/NEEDS ACTION/IN-
     *                              PROCESS
     *     URL              0 or 1
     *     X-PROPERTY       0+
     * 
     * VTIMEZONE            0+  MUST be present if any date/time refers to
     *                          a timezone
     * X-COMPONENT          0+
     * 
     * VALARM               0
     * VEVENT               0
     * VFREEBUSY            0
     * </pre>
     * 
     */
    private class DeclineCounterValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, getProperties());
            
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DUE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PERCENT_COMPLETE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            ComponentValidator.assertNone(Component.VALARM, getAlarms());
        }
    }
    
    /**
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD               1       MUST be "PUBLISH"
     * VTODO                1+
     *     DTSTAMP          1
     *     DTSTART          1
     *     ORGANIZER        1
     *     PRIORITY         1
     *     SEQUENCE         0 or 1  MUST be present if value is greater than
     *                              0, MAY be present if 0
     *     SUMMARY          1       Can be null.
     *     UID              1
     * 
     *     ATTACH           0+
     *     CATEGORIES       0 or 1  This property may contain a list of values
     *     CLASS            0 or 1
     *     COMMENT          0 or 1
     *     CONTACT          0+
     *     CREATED          0 or 1
     *     DESCRIPTION      0 or 1  Can be null
     *     DUE              0 or 1  If present DURATION MUST NOT be present
     *     DURATION         0 or 1  If present DUE MUST NOT be present
     *     EXDATE           0+
     *     EXRULE           0+
     *     GEO              0 or 1
     *     LAST-MODIFIED    0 or 1
     *     LOCATION         0 or 1
     *     PERCENT-COMPLETE 0 or 1
     *     RDATE            0+
     *     RECURRENCE-ID    0 or 1  MUST only if referring to an instance of a
     *                              recurring calendar component.  Otherwise
     *                              it MUST NOT be present.
     * 
     *     RELATED-TO       0+
     *     RESOURCES        0 or 1  This property may contain a list of values
     *     RRULE            0+
     *     STATUS           0 or 1  MAY be one of COMPLETED/NEEDS ACTION/IN-
     *                              PROCESS/CANCELLED
     *     URL              0 or 1
     *     X-PROPERTY       0+
     * 
     *     ATTENDEE         0
     *     REQUEST-STATUS   0
     * 
     * VTIMEZONE            0+    MUST be present if any date/time refers to
     *                            a timezone
     * VALARM               0+
     * X-COMPONENT          0+
     * 
     * VFREEBUSY            0
     * VEVENT               0
     * VJOURNAL             0
     * </pre>
     * 
     */
    private class PublishValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            
            if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
                PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
                PropertyValidator.getInstance().assertOne(Property.PRIORITY, getProperties());
            }
            
            PropertyValidator.getInstance().assertOne(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            // DTSTART: RFC2446 conflicts with RCF2445..
            PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DUE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PERCENT_COMPLETE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.ATTENDEE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
            
            for (final Iterator i = getAlarms().iterator(); i.hasNext();) {
                final VAlarm alarm = (VAlarm) i.next();
                alarm.validate(Method.PUBLISH);
            }
        }
    }
    
    /**
     * <pre>
     * Component/Property   Presence
     * -------------------  ---------------------------------------------
     * METHOD               1      MUST be "REFRESH"
     * VTODO                1
     *     ATTENDEE         1
     *     DTSTAMP          1
     *     UID              1       MUST echo original UID
     * 
     *     RECURRENCE-ID    0 or 1  MUST only if referring to an instance of a
     *                              Recurring calendar component. Otherwise it
     *                              MUST NOT be present
     *     X-PROPERTY       0+
     * 
     *     ATTACH           0
     *     CATEGORIES       0
     *     CLASS            0
     *     COMMENT          0
     *     CONTACT          0
     *     CREATED          0
     *     DESCRIPTION      0
     *     DTSTART          0
     *     DUE              0
     *     DURATION         0
     *     EXDATE           0
     *     EXRULE           0
     *     GEO              0
     *     LAST-MODIFIED    0
     *     LOCATION         0
     *     ORGANIZER        0
     *     PERCENT-COMPLETE 0
     *     PRIORITY         0
     *     RDATE            0
     *     RELATED-TO       0
     *     REQUEST-STATUS   0
     *     RESOURCES        0
     *     RRULE            0
     *     SEQUENCE         0
     *     STATUS           0
     *     URL              0
     * 
     * X-COMPONENT          0+
     * 
     * VALARM               0
     * VEVENT               0
     * VFREEBUSY            0
     * VTIMEZONE            0
     * </pre>
     * 
     */
    private class RefreshValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOne(Property.ATTENDEE, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.ATTACH, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertNone(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CONTACT, getProperties());
            PropertyValidator.getInstance().assertNone(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DUE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.EXDATE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.EXRULE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertNone(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertNone(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertNone(Property.PERCENT_COMPLETE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RDATE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RELATED_TO, getProperties());
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertNone(Property.RRULE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertNone(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertNone(Property.URL, getProperties());
            
            ComponentValidator.assertNone(Component.VALARM, getAlarms());
        }
    }
    
    /**
     * <pre>
     * Component/Property   Presence
     * -------------------  ---------------------------------------------
     * METHOD               1      MUST be "REPLY"
     * VTODO                1+     All component MUST have the same UID
     *     ATTENDEE         1+
     *     DTSTAMP          1
     *     ORGANIZER        1
     *     UID              1      MUST must be the address of the replying
     *                             attendee
     *     REQUEST-STATUS   0+
     *     ATTACH           0+
     *     CATEGORIES       0 or 1 This property may contain a list of values
     *     CLASS            0 or 1
     *     COMMENT          0 or 1
     *     CONTACT          0+
     *     CREATED          0 or 1
     *     DESCRIPTION      0 or 1
     *     DTSTART          0 or 1
     *     DUE              0 or 1  If present DURATION MUST NOT be present
     *     DURATION         0 or 1  If present DUE MUST NOT be present
     *     EXDATE           0+
     *     EXRULE           0+
     *     GEO              0 or 1
     *     LAST-MODIFIED    0 or 1
     *     LOCATION         0 or 1
     *     PERCENT-COMPLETE 0 or 1
     *     PRIORITY         0 or 1
     *     RDATE            0+
     *     RELATED-TO       0+
     *     RESOURCES        0 or 1  This property may contain a list of values
     *     RRULE            0+
     *     RECURRENCE-ID    0 or 1  MUST only if referring to an instance of a
     *                              Recurring calendar component. Otherwise it
     *                              MUST NOT be present
     *     SEQUENCE         0 or 1  MUST be the sequence number of
     *                              the original REQUEST if greater than 0.
     *                              MAY be present if 0.
     *     STATUS           0 or 1
     *     SUMMARY          0 or 1  Can be null
     *     URL              0 or 1
     *     X-PROPERTY       0+
     * 
     * VTIMEZONE            0 or 1  MUST be present if any date/time refers to
     *                              a timezone
     * X-COMPONENT          0+
     * 
     * VALARM               0
     * VEVENT               0
     * VFREEBUSY            0
     * </pre>
     * 
     */
    private class ReplyValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, getProperties());
            
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());

            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DUE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PERCENT_COMPLETE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            ComponentValidator.assertNone(Component.VALARM, getAlarms());
        }
    }
    
    /**
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * METHOD                1       MUST be "REQUEST"
     * VTODO                 1+      All components must have the same UID
     *     ATTENDEE          1+
     *     DTSTAMP           1
     *     DTSTART           1
     *     ORGANIZER         1
     *     PRIORITY          1
     *     SEQUENCE          0 or 1  MUST be present if value is greater than
     *                               0, MAY be present if 0
     *     SUMMARY           1       Can be null.
     *     UID               1
     * 
     *     ATTACH            0+
     *     CATEGORIES        0 or 1   This property may contain a list of
     *                                values
     *     CLASS             0 or 1
     *     COMMENT           0 or 1
     *     CONTACT           0+
     *     CREATED           0 or 1
     *     DESCRIPTION       0 or 1  Can be null
     *     DUE               0 or 1  If present DURATION MUST NOT be present
     *     DURATION          0 or 1  If present DUE MUST NOT be present
     *     EXDATE            0+
     *     EXRULE            0+
     *     GEO               0 or 1
     *     LAST-MODIFIED     0 or 1
     *     LOCATION          0 or 1
     *     PERCENT-COMPLETE  0 or 1
     *     RDATE             0+
     *     RECURRENCE-ID     0 or 1  present if referring to an instance of a
     *                               recurring calendar component.  Otherwise
     *                               it MUST NOT be present.
     *     RELATED-TO        0+
     *     RESOURCES         0 or 1  This property may contain a list of
     *                               values
     *     RRULE             0+
     *     STATUS            0 or 1  MAY be one of COMPLETED/NEEDS ACTION/IN-
     *                               PROCESS
     *     URL               0 or 1
     *     X-PROPERTY        0+
     * 
     *     REQUEST-STATUS    0
     * 
     * VALARM                0+
     * 
     * VTIMEZONE             0+  MUST be present if any date/time refers
     *                           to a timezone
     * X-COMPONENT           0+
     * 
     * VEVENT                0
     * VFREEBUSY             0
     * VJOURNAL              0
     * </pre>
     * 
     */
    private class RequestValidator implements Validator {
        
		private static final long serialVersionUID = 1L;

        public void validate() throws ValidationException {
            PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, getProperties());
            
            PropertyValidator.getInstance().assertOne(Property.DTSTAMP, getProperties());
            PropertyValidator.getInstance().assertOne(Property.DTSTART, getProperties());
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, getProperties());
            PropertyValidator.getInstance().assertOne(Property.PRIORITY, getProperties());
            PropertyValidator.getInstance().assertOne(Property.SUMMARY, getProperties());
            PropertyValidator.getInstance().assertOne(Property.UID, getProperties());
            
            PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.COMMENT, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DUE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.GEO, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.PERCENT_COMPLETE, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, getProperties());
            PropertyValidator.getInstance().assertOneOrLess(Property.URL, getProperties());
            
            PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, getProperties());
            
            for (final Iterator i = getAlarms().iterator(); i.hasNext();) {
                final VAlarm alarm = (VAlarm) i.next();
                alarm.validate(Method.REQUEST);
            }
        }
    }
    
    /**
     * @return the optional access classification property
     */
    public final Clazz getClassification() {
        return (Clazz) getProperty(Property.CLASS);
    }

    /**
     * @return the optional date completed property
     */
    public final Completed getDateCompleted() {
        return (Completed) getProperty(Property.COMPLETED);
    }

    /**
     * @return the optional creation-time property
     */
    public final Created getCreated() {
        return (Created) getProperty(Property.CREATED);
    }

    /**
     * @return the optional description property
     */
    public final Description getDescription() {
        return (Description) getProperty(Property.DESCRIPTION);
    }

    /**
     * Convenience method to pull the DTSTART out of the property list.
     * @return The DtStart object representation of the start Date
     */
    public final DtStart getStartDate() {
        return (DtStart) getProperty(Property.DTSTART);
    }

    /**
     * @return the optional geographic position property
     */
    public final Geo getGeographicPos() {
        return (Geo) getProperty(Property.GEO);
    }

    /**
     * @return the optional last-modified property
     */
    public final LastModified getLastModified() {
        return (LastModified) getProperty(Property.LAST_MODIFIED);
    }

    /**
     * @return the optional location property
     */
    public final Location getLocation() {
        return (Location) getProperty(Property.LOCATION);
    }

    /**
     * @return the optional organizer property
     */
    public final Organizer getOrganizer() {
        return (Organizer) getProperty(Property.ORGANIZER);
    }

    /**
     * @return the optional percentage complete property
     */
    public final PercentComplete getPercentComplete() {
        return (PercentComplete) getProperty(Property.PERCENT_COMPLETE);
    }

    /**
     * @return the optional priority property
     */
    public final Priority getPriority() {
        return (Priority) getProperty(Property.PRIORITY);
    }

    /**
     * @return the optional date-stamp property
     */
    public final DtStamp getDateStamp() {
        return (DtStamp) getProperty(Property.DTSTAMP);
    }

    /**
     * @return the optional sequence number property
     */
    public final Sequence getSequence() {
        return (Sequence) getProperty(Property.SEQUENCE);
    }

    /**
     * @return the optional status property
     */
    public final Status getStatus() {
        return (Status) getProperty(Property.STATUS);
    }

    /**
     * @return the optional summary property
     */
    public final Summary getSummary() {
        return (Summary) getProperty(Property.SUMMARY);
    }

    /**
     * @return the optional URL property
     */
    public final Url getUrl() {
        return (Url) getProperty(Property.URL);
    }

    /**
     * @return the optional recurrence identifier property
     */
    public final RecurrenceId getRecurrenceId() {
        return (RecurrenceId) getProperty(Property.RECURRENCE_ID);
    }

    /**
     * @return the optional Duration property
     */
    public final Duration getDuration() {
        return (Duration) getProperty(Property.DURATION);
    }

    /**
     * @return the optional due property
     */
    public final Due getDue() {
        return (Due) getProperty(Property.DUE);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return (Uid) getProperty(Property.UID);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object arg0) {
        if (arg0 instanceof VToDo) {
            return super.equals(arg0)
                    && ObjectUtils.equals(alarms, ((VToDo) arg0).getAlarms());
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
     * @throws ParseException where an error occurs parsing data
     * @throws IOException where an error occurs reading data
     * @throws URISyntaxException where an invalid URI is encountered
     * @see net.fortuna.ical4j.model.Component#copy()
     */
    public Component copy() throws ParseException, IOException, URISyntaxException {
        final VToDo copy = (VToDo) super.copy();
        copy.alarms = new ComponentList(alarms);
        return copy;
    }
}
