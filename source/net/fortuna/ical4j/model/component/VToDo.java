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
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.PropertyValidator;

/**
 * Defines an iCalendar VTODO component.
 *
 * <pre>
 *   4.6.2 To-do Component
 *   
 *      Component Name: VTODO
 *   
 *      Purpose: Provide a grouping of calendar properties that describe a
 *      to-do.
 *   
 *      Formal Definition: A "VTODO" calendar component is defined by the
 *      following notation:
 *    
 *        todoc      = "BEGIN" ":" "VTODO" CRLF
 *                     todoprop *alarmc
 *                     "END" ":" "VTODO" CRLF
 *    
 *        todoprop   = *(
 *    
 *                   ; the following are optional,
 *                   ; but MUST NOT occur more than once
 *    
 *                   class / completed / created / description / dtstamp /
 *                   dtstart / geo / last-mod / location / organizer /
 *                   percent / priority / recurid / seq / status /
 *                   summary / uid / url /
 *    
 *                   ; either 'due' or 'duration' may appear in
 *                   ; a 'todoprop', but 'due' and 'duration'
 *                   ; MUST NOT occur in the same 'todoprop'
 *    
 *                   due / duration /
 *    
 *                   ; the following are optional,
 *                   ; and MAY occur more than once
 *                   attach / attendee / categories / comment / contact /
 *                   exdate / exrule / rstatus / related / resources /
 *                   rdate / rrule / x-prop
 *    
 *                   )
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class VToDo extends Component {

    private ComponentList alarms = new ComponentList();

    /**
     * Default constructor.
     */
    public VToDo() {
        super(VTODO);
    }

    /**
     * Constructor.
     *
     * @param properties
     *            a list of properties
     */
    public VToDo(final PropertyList properties) {
        super(VTODO, properties);
    }

    public final ComponentList getAlarms() {
        return alarms;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(BEGIN);
        buffer.append(':');
        buffer.append(getName());
        buffer.append("\r\n");
        buffer.append(getProperties());
        buffer.append(getAlarms());
        buffer.append(END);
        buffer.append(':');
        buffer.append(getName());
        buffer.append("\r\n");
        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(boolean recurse) throws ValidationException {

        /*
         * ; the following are optional, ; but MUST NOT occur more than once
         *
         * class / completed / created / description / dtstamp / dtstart / geo /
         * last-mod / location / organizer / percent / priority / recurid / seq /
         * status / summary / uid / url /
         */
        PropertyValidator.getInstance().validateOneOrLess(Property.CLASS,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.COMPLETED,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.CREATED,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.DESCRIPTION,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.DTSTAMP,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.DTSTART,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.GEO,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(
                Property.LAST_MODIFIED, getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.LOCATION,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.ORGANIZER,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(
                Property.PERCENT_COMPLETE, getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.PRIORITY,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(
                Property.RECURRENCE_ID, getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.SEQUENCE,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.STATUS,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.SUMMARY,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.UID,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.URL,
                getProperties());

        /*
         * ; either 'due' or 'duration' may appear in ; a 'todoprop', but 'due'
         * and 'duration' ; MUST NOT occur in the same 'todoprop'
         *
         * due / duration /
         *
         */
        if (getProperties().getProperty(Property.DUE) != null
                && getProperties().getProperty(Property.DURATION) != null) { throw new ValidationException(
                "Properties [" + Property.DUE + "," + Property.DURATION
                        + "] may not occur in the same VEVENT"); }

        /*
         * ; the following are optional, ; and MAY occur more than once attach /
         * attendee / categories / comment / contact / exdate / exrule / rstatus /
         * related / resources / rdate / rrule / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }
}