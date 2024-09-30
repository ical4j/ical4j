/*
 *  Copyright (c) 2024, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.transform.itip;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ChangeManagementPropertyModifiers;
import net.fortuna.ical4j.model.ComponentGroup;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.transform.Transformer;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static net.fortuna.ical4j.model.CalendarPropertyModifiers.METHOD;
import static net.fortuna.ical4j.model.ChangeManagementPropertyModifiers.DTSTAMP;
import static net.fortuna.ical4j.model.RelationshipPropertyModifiers.UIDGEN;

/**
 * Subclasses provide implementations to support ITiP methods as specified in
 * <a href="https://www.rfc-editor.org/rfc/rfc5546">RFC5546</a>.
 *
 * <pre>
 *     1.4.  Methods
 *
 *    The iTIP methods are listed below and their usage and semantics are
 *    defined in Section 3 of this document.
 *
 *    +----------------+--------------------------------------------------+
 *    | Method         | Description                                      |
 *    +----------------+--------------------------------------------------+
 *    | PUBLISH        | Used to publish an iCalendar object to one or    |
 *    |                | more "Calendar Users".  There is no              |
 *    |                | interactivity between the publisher and any      |
 *    |                | other "Calendar User".  An example might include |
 *    |                | a baseball team publishing its schedule to the   |
 *    |                | public.                                          |
 *    |                |                                                  |
 *    | REQUEST        | Used to schedule an iCalendar object with other  |
 *    |                | "Calendar Users".  Requests are interactive in   |
 *    |                | that they require the receiver to respond using  |
 *    |                | the reply methods.  Meeting requests, busy-time  |
 *    |                | requests, and the assignment of tasks to other   |
 *    |                | "Calendar Users" are all examples.  Requests are |
 *    |                | also used by the Organizer to update the status  |
 *    |                | of an iCalendar object.                          |
 *    |                |                                                  |
 *    | REPLY          | A reply is used in response to a request to      |
 *    |                | convey Attendee status to the Organizer.         |
 *    |                | Replies are commonly used to respond to meeting  |
 *    |                | and task requests.                               |
 *    |                |                                                  |
 *    | ADD            | Add one or more new instances to an existing     |
 *    |                | recurring iCalendar object.                      |
 *    |                |                                                  |
 *    | CANCEL         | Cancel one or more instances of an existing      |
 *    |                | iCalendar object.                                |
 *    |                |                                                  |
 *    | REFRESH        | Used by an Attendee to request the latest        |
 *    |                | version of an iCalendar object.                  |
 *    |                |                                                  |
 *    | COUNTER        | Used by an Attendee to negotiate a change in an  |
 *    |                | iCalendar object.  Examples include the request  |
 *    |                | to change a proposed event time or change the    |
 *    |                | due date for a task.                             |
 *    |                |                                                  |
 *    | DECLINECOUNTER | Used by the Organizer to decline the proposed    |
 *    |                | counter proposal.                                |
 *    +----------------+--------------------------------------------------+
 *    
 * </pre>
 */
public abstract class AbstractMethodTransformer implements Transformer<Calendar> {

    private final Method method;

    private final Supplier<Uid> uidGenerator;

    private final boolean incrementSequence;

    private final boolean sameUid;

    private final List<String> requiredProps;

    AbstractMethodTransformer(Method method, Supplier<Uid> uidGenerator, boolean sameUid, boolean incrementSequence,
                              String...requiredProps) {
        this.method = method;
        this.uidGenerator = uidGenerator;
        this.incrementSequence = incrementSequence;
        this.sameUid = sameUid;
        this.requiredProps = Arrays.asList(requiredProps);
    }

    @Override
    public Calendar apply(Calendar object) {
        // ensure method property is set..
        object.with(METHOD, method);

        Uid alignedUid = null;
        for (var component : object.getComponents()) {
            // ensure uid property is set for all components..
            component.with(UIDGEN, uidGenerator);
            component.with(DTSTAMP, Instant.now());

            // check if calendar contains different object instances
            Uid uid = component.getRequiredProperty(Property.UID);
            if (alignedUid == null) {
                alignedUid = uid;
            } else if (sameUid && !uid.equals(alignedUid)) {
                throw new IllegalArgumentException("All components must share the same non-null UID");
            }

            // check if calendar contains different object types..
            //xxx: implement

            ComponentGroup<CalendarComponent> componentGroup = new ComponentGroup<>(
                    object.getComponents(), uid);

            // if a calendar component has already been published previously
            // update the sequence number..
            if (incrementSequence) {
                ChangeManagementPropertyModifiers.SEQUENCE_INCREMENT.apply(componentGroup.getLatestRevision());
            }
        }
        return object;
    }
}
