/*
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
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;

import static net.fortuna.ical4j.model.Property.*;

/**
 * $Id$ [May 1 2017]
 *
 * Defines an iCalendar PARTICIPANT component.
 *
 * <pre>
 Component name:  PARTICIPANT

 Purpose:  This component provides information about a participant in
 an event or task.

 Conformance:  This component can be specified multiple times in a
 "VEVENT", "VTODO", "VJOURNAL" or "VFREEBUSY" calendar component.

 Description:  This component provides information about a participant
 in a calendar component.  A participant may be an attendee in a
 scheduling sense and the ATTENDEE property may be specified in
 addition.  Participants can be individuals or organizations, for
 example a soccer team, the spectators or the musicians.

 STRUCTURED-DATA properties if present may refer to definitions of
 the participant - such as a vCard.

 The CALENDAR-ADDRESS property if present will provide a cal-
 address.  If an ATTENDEE property has the same value the
 participant is considered schedulable.  The PARTICIPANT component
 can be used to contain additional meta-data related to the
 attendee.

 Format Definition:

 This property is defined by the following notation:


 participantc = "BEGIN" ":" "PARTICIPANT" CRLF
                *( partprop / locationc / resourcec )
                "END" ":" "PARTICIPANT" CRLF

 partprop     = ; the elements herein may appear in any order,
                ; and the order is not significant.

                uid
                participanttype

                (calendaraddress)
                (created)
                (description)
                (dtstamp)
                (geo)
                (last-mod)
                (priority)
                (seq)
                (status)
                (summary)
                (url)

                *attach
                *categories
                *comment
                *contact
                *location
                *rstatus
                *related
                *resources
                *strucloc
                *strucres
                *styleddescription
                *sdataprop
                *iana-prop

 Note:  When the PRIORITY is supplied it defines the ordering of
 PARTICIPANT components with the same value for the TYPE parameter.
 * </pre>
 *
 * @author Mike Douglass
 */
public class Participant extends Component implements ComponentContainer<Component> {
    private static final long serialVersionUID = -8193965477414653802L;

    /**
     * Default constructor.
     */
    public Participant() {
        super(PARTICIPANT);
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public Participant(final PropertyList<Property> properties) {
        super(PARTICIPANT, properties);
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public Participant(final PropertyList<Property> properties, final ComponentList<Component> components) {
        super(PARTICIPANT, properties, components);
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse) throws ValidationException {
        ComponentValidator.PARTICIPANT.validate(this);

        if (recurse) {
            validateProperties();
        }
    }

    @Override
    public ComponentList<Component> getComponents() {
        return (ComponentList<Component>) components;
    }

    /**
     * Returns the optional calendar address property.
     * @return the CALENDAR_ADDRESS property or null if not specified
     */
    public final CalendarAddress getCalendarAddress() {
        return getProperty(CALENDAR_ADDRESS);
    }

    /**
     * @return the optional creation-time property for an event
     */
    public final Created getCreated() {
        return getProperty(CREATED);
    }

    /**
     * @return the optional date-stamp property
     */
    public final DtStamp getDateStamp() {
        return getProperty(DTSTAMP);
    }

    /**
     * Returns the optional description property.
     * @return the DESCRIPTION property or null if not specified
     */
    public final Description getDescription() {
        return getProperty(DESCRIPTION);
    }

    /**
     * @return the optional last-modified property for an event
     */
    public final LastModified getLastModified() {
        return getProperty(LAST_MODIFIED);
    }

    /**
     * Returns the mandatory PARTICIPANT-TYPE property.
     * @return the PARTICIPANT-TYPE property or null if not specified
     */
    public ParticipantType getParticipantType() {
        return getProperty(PARTICIPANT_TYPE);
    }

    /**
     * @return the optional priority property for an event
     */
    public final Priority getPriority() {
        return getProperty(PRIORITY);
    }

    /**
     * @return the optional sequence number property for an event
     */
    public final Sequence getSequence() {
        return getProperty(SEQUENCE);
    }

    /**
     * @return the optional status property for an event
     */
    public final Status getStatus() {
        return getProperty(STATUS);
    }

    /**
     * Returns the optional summary property.
     * @return the SUMMARY property or null if not specified
     */
    public final Summary getSummary() {
        return getProperty(SUMMARY);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return getProperty(UID);
    }

    /**
     * @return the optional URL property for an event
     */
    public final Url getUrl() {
        return getProperty(URL);
    }

    public static class Factory extends Content.Factory implements ComponentFactory<Participant> {

        public Factory() {
            super(PARTICIPANT);
        }

        @Override
        public Participant createComponent() {
            return new Participant();
        }

        @Override
        public Participant createComponent(PropertyList<Property> properties) {
            return new Participant(properties);
        }

        @Override
        public Participant createComponent(final PropertyList<Property> properties,
                                           final ComponentList<Component> subComponents) {
            return new Participant(properties, subComponents);
        }
    }
}
