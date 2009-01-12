/**
 * Copyright (c) 2009, Ben Fortuna
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

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.util.PropertyValidator;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines an iCalendar VALARM component.
 *
 * <pre>
 *    4.6.6 Alarm Component
 *
 *       Component Name: VALARM
 *
 *       Purpose: Provide a grouping of component properties that define an
 *       alarm.
 *
 *       Formal Definition: A &quot;VALARM&quot; calendar component is defined by the
 *       following notation:
 *
 *              alarmc     = &quot;BEGIN&quot; &quot;:&quot; &quot;VALARM&quot; CRLF
 *                           (audioprop / dispprop / emailprop / procprop)
 *                           &quot;END&quot; &quot;:&quot; &quot;VALARM&quot; CRLF
 *
 *         audioprop  = 2*(
 *
 *                    ; 'action' and 'trigger' are both REQUIRED,
 *                    ; but MUST NOT occur more than once
 *
 *                    action / trigger /
 *
 *                    ; 'duration' and 'repeat' are both optional,
 *                    ; and MUST NOT occur more than once each,
 *                    ; but if one occurs, so MUST the other
 *
 *                    duration / repeat /
 *
 *                    ; the following is optional,
 *                    ; but MUST NOT occur more than once
 *
 *                    attach /
 *
 *                    ; the following is optional,
 *                    ; and MAY occur more than once
 *
 *                    x-prop
 *
 *                    )
 *
 *
 *
 *         dispprop   = 3*(
 *
 *                    ; the following are all REQUIRED,
 *                    ; but MUST NOT occur more than once
 *
 *                    action / description / trigger /
 *
 *                    ; 'duration' and 'repeat' are both optional,
 *                    ; and MUST NOT occur more than once each,
 *                    ; but if one occurs, so MUST the other
 *
 *                    duration / repeat /
 *
 *                    ; the following is optional,
 *                    ; and MAY occur more than once
 *
 *                    *x-prop
 *
 *                    )
 *
 *
 *
 *         emailprop  = 5*(
 *
 *                    ; the following are all REQUIRED,
 *                    ; but MUST NOT occur more than once
 *
 *                    action / description / trigger / summary
 *
 *                    ; the following is REQUIRED,
 *                    ; and MAY occur more than once
 *
 *                    attendee /
 *
 *                    ; 'duration' and 'repeat' are both optional,
 *                    ; and MUST NOT occur more than once each,
 *                    ; but if one occurs, so MUST the other
 *
 *                    duration / repeat /
 *
 *                    ; the following are optional,
 *                    ; and MAY occur more than once
 *
 *                    attach / x-prop
 *
 *                    )
 *
 *
 *
 *         procprop   = 3*(
 *
 *                    ; the following are all REQUIRED,
 *                    ; but MUST NOT occur more than once
 *
 *                    action / attach / trigger /
 *
 *                    ; 'duration' and 'repeat' are both optional,
 *                    ; and MUST NOT occur more than once each,
 *                    ; but if one occurs, so MUST the other
 *
 *                    duration / repeat /
 *
 *                    ; 'description' is optional,
 *                    ; and MUST NOT occur more than once
 *
 *                    description /
 *
 *                    ; the following is optional,
 *                    ; and MAY occur more than once
 *
 *                    x-prop
 *
 *                    )
 * </pre>
 *
 * Example 1 - Creating an alarm to trigger at a specific time:
 *
 * <pre><code>
 * java.util.Calendar cal = java.util.Calendar.getInstance();
 * cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
 * cal.set(java.util.Calendar.DAY_OF_MONTH, 25);
 *
 * VAlarm christmas = new VAlarm(cal.getTime());
 * </code></pre>
 *
 * Example 2 - Creating an alarm to trigger one (1) hour before the scheduled start of the parent event/the parent todo
 * is due:
 *
 * <pre><code>
 * VAlarm reminder = new VAlarm(-1000 * 60 * 60);
 *
 * // repeat reminder four (4) more times every fifteen (15) minutes..
 * reminder.getProperties().add(new Repeat(4));
 * reminder.getProperties().add(new Duration(1000 * 60 * 15));
 *
 * // display a message..
 * reminder.getProperties().add(Action.DISPLAY);
 * reminder.getProperties().add(new Description(&quot;Progress Meeting at 9:30am&quot;));
 * </code></pre>
 *
 * @author Ben Fortuna
 */
public class VAlarm extends CalendarComponent {

    private static final long serialVersionUID = -8193965477414653802L;

    /**
     * Default constructor.
     */
    public VAlarm() {
        super(VALARM);
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VAlarm(final PropertyList properties) {
        super(VALARM, properties);
    }

    /**
     * Constructs a new VALARM instance that will trigger at the specified time.
     * @param trigger the time the alarm will trigger
     */
    public VAlarm(final DateTime trigger) {
        this();
        getProperties().add(new Trigger(trigger));
    }

    /**
     * Constructs a new VALARM instance that will trigger at the specified time relative to the event/todo component.
     * @param trigger a duration of time relative to the parent component that the alarm will trigger at
     */
    public VAlarm(final Dur trigger) {
        this();
        getProperties().add(new Trigger(trigger));
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(final boolean recurse)
            throws ValidationException {

        /*
         * ; 'action' and 'trigger' are both REQUIRED, ; but MUST NOT occur more than once action / trigger /
         */
        PropertyValidator.getInstance().assertOne(Property.ACTION, getProperties());
        PropertyValidator.getInstance().assertOne(Property.TRIGGER, getProperties());

        /*
         * ; 'duration' and 'repeat' are both optional, ; and MUST NOT occur more than once each, ; but if one occurs,
         * so MUST the other duration / repeat /
         */
        PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.REPEAT, getProperties());

        try {
            PropertyValidator.getInstance().assertNone(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertNone(Property.REPEAT, getProperties());
        }
        catch (ValidationException ve) {
            PropertyValidator.getInstance().assertOne(Property.DURATION, getProperties());
            PropertyValidator.getInstance().assertOne(Property.REPEAT, getProperties());
        }
        
        /*
         * ; the following is optional, ; and MAY occur more than once x-prop
         */
        
        final Action action = getAction();
        if (Action.AUDIO.equals(action)) {
            validateAudio();
        }
        else if (Action.DISPLAY.equals(action)) {
            validateDisplay();
        }
        else if (Action.EMAIL.equals(action)) {
            validateEmail();
        }
        else if (Action.PROCEDURE.equals(action)) {
            validateProcedure();
        }

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * @throws ValidationException
     */
    private void validateAudio() throws ValidationException {
        /*
         * ; the following is optional, ; but MUST NOT occur more than once attach /
         */
        PropertyValidator.getInstance().assertOneOrLess(Property.ATTACH, getProperties());
    }

    /**
     * @throws ValidationException
     */
    private void validateDisplay() throws ValidationException {
        /*
         * ; the following are all REQUIRED, ; but MUST NOT occur more than once action / description / trigger /
         */
        PropertyValidator.getInstance().assertOne(Property.DESCRIPTION, getProperties());
    }

    /**
     * @throws ValidationException
     */
    private void validateEmail() throws ValidationException {
        /*
         * ; the following are all REQUIRED, 
         * ; but MUST NOT occur more than once action / description / trigger / summary 
         * ; the following is REQUIRED, 
         * ; and MAY occur more than once attendee / 
         * ; 'duration' and 'repeat' are both optional, 
         * ; and MUST NOT occur more than once each, 
         * ; but if one occurs, so MUST the other duration / repeat / 
         * ; the following are optional, 
         * ; and MAY occur more than once attach / x-prop
         */
        PropertyValidator.getInstance().assertOne(Property.DESCRIPTION, getProperties());
        PropertyValidator.getInstance().assertOne(Property.SUMMARY, getProperties());

        PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, getProperties());
    }

    /**
     * @throws ValidationException
     */
    private void validateProcedure() throws ValidationException {
        /*
         * ; the following are all REQUIRED, 
         * ; but MUST NOT occur more than once action / attach / trigger / 
         * ; 'duration' and 'repeat' are both optional, 
         * ; and MUST NOT occur more than once each, 
         * ; but if one occurs, so MUST the other duration / repeat / 
         * ; 'description' is optional, 
         * ; and MUST NOT occur more than once description / 
         * ; the following is optional, ; and MAY occur more than once x-prop
         */
        PropertyValidator.getInstance().assertOne(Property.ATTACH, getProperties());

        PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.component.CalendarComponent#validatePublish()
     */
    public void validatePublish() throws ValidationException {
        validateITIP();
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.component.CalendarComponent#validateAdd()
     */
    public void validateAdd() throws ValidationException {
        validateITIP();
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.component.CalendarComponent#validateCancel()
     */
    public void validateCancel() throws ValidationException {
        validateITIP();
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.component.CalendarComponent#validateCounter()
     */
    public void validateCounter() throws ValidationException {
        validateITIP();
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.component.CalendarComponent#validateDeclineCounter()
     */
    public void validateDeclineCounter() throws ValidationException {
        validateITIP();
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.component.CalendarComponent#validateRefresh()
     */
    public void validateRefresh() throws ValidationException {
        validateITIP();
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.component.CalendarComponent#validateReply()
     */
    public void validateReply() throws ValidationException {
        validateITIP();
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.component.CalendarComponent#validateRequest()
     */
    public void validateRequest() throws ValidationException {
        validateITIP();
    }

    /**
     * Common validation for all iTIP methods.
     * @throws ValidationException
     * 
     * <pre>
     * Component/Property  Presence
     * ------------------- ----------------------------------------------
     * VALARM              0+
     *     ACTION          1
     *     ATTACH          0+
     *     DESCRIPTION     0 or 1
     *     DURATION        0 or 1  if present REPEAT MUST be present
     *     REPEAT          0 or 1  if present DURATION MUST be present
     *     SUMMARY         0 or 1
     *     TRIGGER         1
     *     X-PROPERTY      0+
     * </pre>
     */
    private void validateITIP() throws ValidationException {
        PropertyValidator.getInstance().assertOne(Property.ACTION, getProperties());
        PropertyValidator.getInstance().assertOne(Property.TRIGGER, getProperties());
        
        PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.REPEAT, getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY, getProperties());
    }
    
    /**
     * Returns the mandatory action property.
     * @return
     */
    public final Action getAction() {
        return (Action) getProperty(Property.ACTION);
    }

    /**
     * Returns the mandatory trigger property.
     * @return
     */
    public final Trigger getTrigger() {
        return (Trigger) getProperty(Property.TRIGGER);
    }

    /**
     * Returns the optional duration property.
     * @return
     */
    public final Duration getDuration() {
        return (Duration) getProperty(Property.DURATION);
    }

    /**
     * Returns the optional repeat property.
     * @return
     */
    public final Repeat getRepeat() {
        return (Repeat) getProperty(Property.REPEAT);
    }

    /**
     * Returns the optional attachment property.
     * @return
     */
    public final Attach getAttachment() {
        return (Attach) getProperty(Property.ATTACH);
    }

    /**
     * Returns the optional description property.
     * @return
     */
    public final Description getDescription() {
        return (Description) getProperty(Property.DESCRIPTION);
    }

    /**
     * Returns the optional summary property.
     * @return
     */
    public final Summary getSummary() {
        return (Summary) getProperty(Property.SUMMARY);
    }
}
