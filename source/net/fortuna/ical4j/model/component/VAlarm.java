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

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.util.PropertyValidator;

/**
 * Defines an iCalendar VALARM component.
 *
 * <pre>
 *   4.6.6 Alarm Component
 *   
 *      Component Name: VALARM
 *   
 *      Purpose: Provide a grouping of component properties that define an
 *      alarm.
 *   
 *      Formal Definition: A "VALARM" calendar component is defined by the
 *      following notation:
 *   
 *             alarmc     = "BEGIN" ":" "VALARM" CRLF
 *                          (audioprop / dispprop / emailprop / procprop)
 *                          "END" ":" "VALARM" CRLF
 *   
 *        audioprop  = 2*(
 *   
 *                   ; 'action' and 'trigger' are both REQUIRED,
 *                   ; but MUST NOT occur more than once
 *   
 *                   action / trigger /
 *   
 *                   ; 'duration' and 'repeat' are both optional,
 *                   ; and MUST NOT occur more than once each,
 *                   ; but if one occurs, so MUST the other
 *   
 *                   duration / repeat /
 *   
 *                   ; the following is optional,
 *                   ; but MUST NOT occur more than once
 *   
 *                   attach /
 *   
 *                   ; the following is optional,
 *                   ; and MAY occur more than once
 *   
 *                   x-prop
 *   
 *                   )
 *   
 *   
 *   
 *        dispprop   = 3*(
 *   
 *                   ; the following are all REQUIRED,
 *                   ; but MUST NOT occur more than once
 *   
 *                   action / description / trigger /
 *   
 *                   ; 'duration' and 'repeat' are both optional,
 *                   ; and MUST NOT occur more than once each,
 *                   ; but if one occurs, so MUST the other
 *   
 *                   duration / repeat /
 *   
 *                   ; the following is optional,
 *                   ; and MAY occur more than once
 *   
 *                   *x-prop
 *   
 *                   )
 *   
 *   
 *   
 *        emailprop  = 5*(
 *   
 *                   ; the following are all REQUIRED,
 *                   ; but MUST NOT occur more than once
 *   
 *                   action / description / trigger / summary
 *   
 *                   ; the following is REQUIRED,
 *                   ; and MAY occur more than once
 *   
 *                   attendee /
 *   
 *                   ; 'duration' and 'repeat' are both optional,
 *                   ; and MUST NOT occur more than once each,
 *                   ; but if one occurs, so MUST the other
 *   
 *                   duration / repeat /
 *   
 *                   ; the following are optional,
 *                   ; and MAY occur more than once
 *   
 *                   attach / x-prop
 *   
 *                   )
 *   
 *   
 *   
 *        procprop   = 3*(
 *   
 *                   ; the following are all REQUIRED,
 *                   ; but MUST NOT occur more than once
 *   
 *                   action / attach / trigger /
 *   
 *                   ; 'duration' and 'repeat' are both optional,
 *                   ; and MUST NOT occur more than once each,
 *                   ; but if one occurs, so MUST the other
 *   
 *                   duration / repeat /
 *   
 *                   ; 'description' is optional,
 *                   ; and MUST NOT occur more than once
 *   
 *                   description /
 *   
 *                   ; the following is optional,
 *                   ; and MAY occur more than once
 *   
 *                   x-prop
 *   
 *                   )
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class VAlarm extends Component {

    /**
     * Default constructor.
     */
    public VAlarm() {
        super(VALARM);
    }

    /**
     * Constructor.
     *
     * @param properties
     *            a list of properties
     */
    public VAlarm(final PropertyList properties) {
        super(VALARM, properties);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(boolean recurse) throws ValidationException {

        /*
         * ; 'action' and 'trigger' are both REQUIRED, ; but MUST NOT occur more
         * than once
         *
         * action / trigger /
         */
        Property action = getProperties().getProperty(Property.ACTION);

        if (action == null) { throw new ValidationException("Property ["
                + Property.ACTION + "] must be specified once"); }

        if (getProperties().getProperty(Property.TRIGGER) == null) { throw new ValidationException(
                "Property [" + Property.TRIGGER + "] must be specified once"); }

        /*
         * ; 'duration' and 'repeat' are both optional, ; and MUST NOT occur
         * more than once each, ; but if one occurs, so MUST the other
         *
         * duration / repeat /
         */
        PropertyValidator.getInstance().validateOneOrLess(Property.DURATION,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.REPEAT,
                getProperties());

        if ((getProperties().getProperty(Property.DURATION) == null && getProperties()
                .getProperty(Property.REPEAT) != null)
                || (getProperties().getProperty(Property.REPEAT) == null && getProperties()
                        .getProperty(Property.DURATION) != null)) { throw new ValidationException(
                "Properties [" + Property.DURATION + "," + Property.REPEAT
                        + "] must both be specified or not at all"); }

        /*
         * ; the following is optional, ; and MAY occur more than once
         *
         * x-prop
         */

        if (Action.AUDIO.equals(action.getValue())) {
            validateAudio();
        }
        else if (Action.DISPLAY.equals(action.getValue())) {
            validateDisplay();
        }
        else if (Action.EMAIL.equals(action.getValue())) {
            validateEmail();
        }
        else if (Action.PROCEDURE.equals(action.getValue())) {
            validateProcedure();
        }

        if (recurse) {
            validateProperties();
        }
    }

    private void validateAudio() throws ValidationException {
        /*
         * ; the following is optional, ; but MUST NOT occur more than once
         *
         * attach /
         */
        PropertyValidator.getInstance().validateOneOrLess(Property.ATTACH,
                getProperties());
    }

    private void validateDisplay() throws ValidationException {
        /*
         * ; the following are all REQUIRED, ; but MUST NOT occur more than once
         *
         * action / description / trigger /
         */
        PropertyValidator.getInstance().validateOne(Property.DESCRIPTION,
                getProperties());
    }

    private void validateEmail() throws ValidationException {
        /*
         * ; the following are all REQUIRED, ; but MUST NOT occur more than once
         *
         * action / description / trigger / summary
         *  ; the following is REQUIRED, ; and MAY occur more than once
         *
         * attendee /
         *  ; 'duration' and 'repeat' are both optional, ; and MUST NOT occur
         * more than once each, ; but if one occurs, so MUST the other
         *
         * duration / repeat /
         *  ; the following are optional, ; and MAY occur more than once
         *
         * attach / x-prop
         */
        PropertyValidator.getInstance().validateOne(Property.DESCRIPTION,
                getProperties());
        PropertyValidator.getInstance().validateOne(Property.SUMMARY,
                getProperties());

        PropertyValidator.getInstance().validateOneOrMore(Property.ATTENDEE,
                getProperties());
    }

    private void validateProcedure() throws ValidationException {
        /*
         * ; the following are all REQUIRED, ; but MUST NOT occur more than once
         *
         * action / attach / trigger /
         *  ; 'duration' and 'repeat' are both optional, ; and MUST NOT occur
         * more than once each, ; but if one occurs, so MUST the other
         *
         * duration / repeat /
         *  ; 'description' is optional, ; and MUST NOT occur more than once
         *
         * description /
         *  ; the following is optional, ; and MAY occur more than once
         *
         * x-prop
         */
        PropertyValidator.getInstance().validateOne(Property.ATTACH,
                getProperties());

        PropertyValidator.getInstance().validateOneOrLess(Property.DESCRIPTION,
                getProperties());
    }
}