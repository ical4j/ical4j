/*
 *  Copyright (c) 2012-2024, Ben Fortuna
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
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Uid;

import java.util.function.Supplier;

import static net.fortuna.ical4j.model.RelationshipPropertyModifiers.ORGANIZER;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.PUBLISH;

/**
 * $Id$
 *
 * Created: 26/09/2004
 *
 * Transforms a calendar for publishing.
 * @author benfortuna
 *
 * <pre>
 *     3.2.1.  PUBLISH
 *
 *    The "PUBLISH" method in a "VEVENT" calendar component is an
 *    unsolicited posting of an iCalendar object.  Any CU may add published
 *    components to their calendar.  The "Organizer" MUST be present in a
 *    published iCalendar component.  "Attendees" MUST NOT be present.  Its
 *    expected usage is for encapsulating an arbitrary event as an
 *    iCalendar object.  The "Organizer" may subsequently update (with
 *    another "PUBLISH" method), add instances to (with an "ADD" method),
 *    or cancel (with a "CANCEL" method) a previously published "VEVENT"
 *    calendar component.
 *
 *    This method type is an iCalendar object that conforms to the
 *    following property constraints:
 *
 *
 *
 * Daboo                       Standards Track                    [Page 18]
 *
 *
 * RFC 5546                          iTIP                     December 2009
 *
 *
 *              +----------------------------------------------+
 *              | Constraints for a METHOD:PUBLISH of a VEVENT |
 *              +----------------------------------------------+
 *
 *    +--------------------+----------+-----------------------------------+
 *    | Component/Property | Presence | Comment                           |
 *    +--------------------+----------+-----------------------------------+
 *    | METHOD             | 1        | MUST equal PUBLISH.               |
 *    |                    |          |                                   |
 *    | VEVENT             | 1+       |                                   |
 *    |   DTSTAMP          | 1        |                                   |
 *    |   DTSTART          | 1        |                                   |
 *    |   ORGANIZER        | 1        |                                   |
 *    |   SUMMARY          | 1        | Can be null.                      |
 *    |   UID              | 1        |                                   |
 *    |   RECURRENCE-ID    | 0 or 1   | Only if referring to an instance  |
 *    |                    |          | of a recurring calendar           |
 *    |                    |          | component.  Otherwise, it MUST    |
 *    |                    |          | NOT be present.                   |
 *    |   SEQUENCE         | 0 or 1   | MUST be present if value is       |
 *    |                    |          | greater than 0; MAY be present if |
 *    |                    |          | 0.                                |
 *    |   ATTACH           | 0+       |                                   |
 *    |   CATEGORIES       | 0+       |                                   |
 *    |   CLASS            | 0 or 1   |                                   |
 *    |   COMMENT          | 0+       |                                   |
 *    |   CONTACT          | 0 or 1   |                                   |
 *    |   CREATED          | 0 or 1   |                                   |
 *    |   DESCRIPTION      | 0 or 1   | Can be null.                      |
 *    |   DTEND            | 0 or 1   | If present, DURATION MUST NOT be  |
 *    |                    |          | present.                          |
 *    |   DURATION         | 0 or 1   | If present, DTEND MUST NOT be     |
 *    |                    |          | present.                          |
 *    |   EXDATE           | 0+       |                                   |
 *    |   GEO              | 0 or 1   |                                   |
 *    |   LAST-MODIFIED    | 0 or 1   |                                   |
 *    |   LOCATION         | 0 or 1   |                                   |
 *    |   PRIORITY         | 0 or 1   |                                   |
 *    |   RDATE            | 0+       |                                   |
 *    |   RELATED-TO       | 0+       |                                   |
 *    |   RESOURCES        | 0+       |                                   |
 *    |   RRULE            | 0 or 1   |                                   |
 *    |   STATUS           | 0 or 1   | MAY be one of                     |
 *    |                    |          | TENTATIVE/CONFIRMED/CANCELLED.    |
 *    |   TRANSP           | 0 or 1   |                                   |
 *    |   URL              | 0 or 1   |                                   |
 *    |   IANA-PROPERTY    | 0+       |                                   |
 *    |   X-PROPERTY       | 0+       |                                   |
 *    |   ATTENDEE         | 0        |                                   |
 *    |   REQUEST-STATUS   | 0        |                                   |
 *    |                    |          |                                   |
 *    |   VALARM           | 0+       |                                   |
 *    |                    |          |                                   |
 *    | VFREEBUSY          | 0        |                                   |
 *    |                    |          |                                   |
 *    | VJOURNAL           | 0        |                                   |
 *    |                    |          |                                   |
 *    | VTODO              | 0        |                                   |
 *    |                    |          |                                   |
 *    | VTIMEZONE          | 0+       | MUST be present if any date/time  |
 *    |                    |          | refers to a timezone.             |
 *    |                    |          |                                   |
 *    | IANA-COMPONENT     | 0+       |                                   |
 *    | X-COMPONENT        | 0+       |                                   |
 *    +--------------------+----------+-----------------------------------+
 * </pre>
 */
public class PublishTransformer extends AbstractMethodTransformer {

    private final Organizer defaultOrganizer;

    public PublishTransformer(Organizer defaultOrganizer, Supplier<Uid> uidGenerator, boolean incrementSequence) {
        super(PUBLISH, uidGenerator, false, incrementSequence);
        this.defaultOrganizer = defaultOrganizer;
    }

    @Override
    public Calendar apply(Calendar object) {
        for (var component : object.getComponents()) {
            component.with(ORGANIZER, defaultOrganizer);
            component.removeAll(Property.ATTENDEE, Property.REQUEST_STATUS);
        }
        return super.apply(object);
    }
}
