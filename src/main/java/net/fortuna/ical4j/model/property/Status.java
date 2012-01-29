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

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.ValidationException;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a STATUS iCalendar component property.
 * 
 * <pre>
 *     4.8.1.11 Status
 *     
 *        Property Name: STATUS
 *     
 *        Purpose: This property defines the overall status or confirmation for
 *        the calendar component.
 *     
 *        Value Type: TEXT
 *     
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *     
 *        Conformance: This property can be specified in &quot;VEVENT&quot;, &quot;VTODO&quot; or
 *        &quot;VJOURNAL&quot; calendar components.
 *     
 *        Description: In a group scheduled calendar component, the property is
 *        used by the &quot;Organizer&quot; to provide a confirmation of the event to the
 *        &quot;Attendees&quot;. For example in a &quot;VEVENT&quot; calendar component, the
 *        &quot;Organizer&quot; can indicate that a meeting is tentative, confirmed or
 *        cancelled. In a &quot;VTODO&quot; calendar component, the &quot;Organizer&quot; can
 *        indicate that an action item needs action, is completed, is in
 *        process or being worked on, or has been cancelled. In a &quot;VJOURNAL&quot;
 *        calendar component, the &quot;Organizer&quot; can indicate that a journal entry
 *        is draft, final or has been cancelled or removed.
 *     
 *        Format Definition: The property is defined by the following notation:
 *     
 *          status     = &quot;STATUS&quot; statparam] &quot;:&quot; statvalue CRLF
 *     
 *          statparam  = *(&quot;;&quot; xparam)
 *     
 *          statvalue  = &quot;TENTATIVE&quot;           ;Indicates event is
 *                                             ;tentative.
 *                     / &quot;CONFIRMED&quot;           ;Indicates event is
 *                                             ;definite.
 *                     / &quot;CANCELLED&quot;           ;Indicates event was
 *                                             ;cancelled.
 *             ;Status values for a &quot;VEVENT&quot;
 *     
 *          statvalue  =/ &quot;NEEDS-ACTION&quot;       ;Indicates to-do needs action.
 *                     / &quot;COMPLETED&quot;           ;Indicates to-do completed.
 *                     / &quot;IN-PROCESS&quot;          ;Indicates to-do in process of
 *                     / &quot;CANCELLED&quot;           ;Indicates to-do was cancelled.
 *             ;Status values for &quot;VTODO&quot;.
 *     
 *          statvalue  =/ &quot;DRAFT&quot;              ;Indicates journal is draft.
 *                     / &quot;FINAL&quot;               ;Indicates journal is final.
 *                     / &quot;CANCELLED&quot;           ;Indicates journal is removed.
 *             ;Status values for &quot;VJOURNAL&quot;.
 *     
 *        Example: The following is an example of this property for a &quot;VEVENT&quot;
 *        calendar component:
 *     
 *          STATUS:TENTATIVE
 *     
 *        The following is an example of this property for a &quot;VTODO&quot; calendar
 *        component:
 *     
 *          STATUS:NEEDS-ACTION
 *     
 *        The following is an example of this property for a &quot;VJOURNAL&quot;
 *        calendar component:
 *     
 *          STATUS:DRAFT
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Status extends Property {

    private static final long serialVersionUID = 7401102230299289898L;

    // Status values for a "VEVENT"
    /**
     * Tentative VEVENT status.
     */
    public static final Status VEVENT_TENTATIVE = new ImmutableStatus(
            "TENTATIVE");

    /**
     * Confirmed VEVENT status.
     */
    public static final Status VEVENT_CONFIRMED = new ImmutableStatus(
            "CONFIRMED");

    /**
     * Cancelled VEVENT status.
     */
    public static final Status VEVENT_CANCELLED = new ImmutableStatus(
            "CANCELLED");

    // Status values for "VTODO"
    /**
     * Tentative VTODO status.
     */
    public static final Status VTODO_NEEDS_ACTION = new ImmutableStatus(
            "NEEDS-ACTION");

    /**
     * Completed VTODO status.
     */
    public static final Status VTODO_COMPLETED = new ImmutableStatus(
            "COMPLETED");

    /**
     * In-process VTODO status.
     */
    public static final Status VTODO_IN_PROCESS = new ImmutableStatus(
            "IN-PROCESS");

    /**
     * Cancelled VTODO status.
     */
    public static final Status VTODO_CANCELLED = new ImmutableStatus(
            "CANCELLED");

    // Status values for "VJOURNAL"
    /**
     * Draft VJOURNAL status.
     */
    public static final Status VJOURNAL_DRAFT = new ImmutableStatus("DRAFT");

    /**
     * Final VJOURNAL status.
     */
    public static final Status VJOURNAL_FINAL = new ImmutableStatus("FINAL");

    /**
     * Cancelled VJOURNAL status.
     */
    public static final Status VJOURNAL_CANCELLED = new ImmutableStatus(
            "CANCELLED");

    /**
     * @author Ben Fortuna An immutable instance of Status.
     */
    private static final class ImmutableStatus extends Status {

        private static final long serialVersionUID = 7771868877237685612L;

        private ImmutableStatus(final String value) {
            super(new ParameterList(true), value);
        }

        public void setValue(final String aValue) {
            throw new UnsupportedOperationException(
                    "Cannot modify constant instances");
        }
    }

    private String value;

    /**
     * Default constructor.
     */
    public Status() {
        super(STATUS, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aValue a value string for this component
     */
    public Status(final String aValue) {
        super(STATUS, PropertyFactoryImpl.getInstance());
        this.value = aValue;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Status(final ParameterList aList, final String aValue) {
        super(STATUS, aList, PropertyFactoryImpl.getInstance());
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(final String aValue) {
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
