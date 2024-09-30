/*
 *  Copyright (c) 2024-2024, Ben Fortuna
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

package net.fortuna.ical4j.validate.property;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

import static net.fortuna.ical4j.model.Parameter.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

public interface RelationshipPropertyValidators {
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        attendee   = "ATTENDEE" attparam ":" cal-address CRLF
     *
     *        attparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" cutypeparam) / (";" memberparam) /
     *                   (";" roleparam) / (";" partstatparam) /
     *                   (";" rsvpparam) / (";" deltoparam) /
     *                   (";" delfromparam) / (";" sentbyparam) /
     *                   (";" cnparam) / (";" dirparam) /
     *                   (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    Validator<Attendee> ATTENDEE = new PropertyValidator<>(Property.ATTENDEE,
            new ValidationRule<>(OneOrLess, CUTYPE, MEMBER, ROLE, PARTSTAT,
                    RSVP, DELEGATED_TO, DELEGATED_FROM, SENT_BY, CN, DIR, LANGUAGE, SCHEDULE_AGENT, SCHEDULE_STATUS));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        contact    = "CONTACT" contparam ":" text CRLF
     *
     *        contparam  = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" altrepparam) / (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    Validator<Contact> CONTACT = new PropertyValidator<>(Property.CONTACT,
            new ValidationRule<>(ValidationRule.ValidationType.OneOrLess, ALTREP, LANGUAGE));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        organizer  = "ORGANIZER" orgparam ":"
     *                     cal-address CRLF
     *
     *        orgparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" cnparam) / (";" dirparam) / (";" sentbyparam) /
     *                   (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    Validator<Organizer> ORGANIZER = new PropertyValidator<>(Property.ORGANIZER,
            new ValidationRule<>(OneOrLess, CN, DIR, SENT_BY, LANGUAGE, SCHEDULE_STATUS));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        recurid    = "RECURRENCE-ID" ridparam ":" ridval CRLF
     *
     *        ridparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
     *                   (";" tzidparam) / (";" rangeparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        ridval     = date-time / date
     *        ;Value MUST match value type
     * </pre>
     */
    Validator<RecurrenceId<?>> RECURRENCE_ID = new PropertyValidator<>(Property.RECURRENCE_ID,
            new ValidationRule<>(OneOrLess, VALUE, TZID, RANGE),
            PropertyValidator.DATE_OR_DATETIME_VALUE);
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        related    = "RELATED-TO" relparam ":" text CRLF
     *
     *        relparam   = *(
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" reltypeparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    Validator<RelatedTo> RELATED_TO = new PropertyValidator<>(Property.RELATED_TO,
            new ValidationRule<>(OneOrLess, RELTYPE));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        url        = "URL" urlparam ":" uri CRLF
     *
     *        urlparam   = *(";" other-param)
     * </pre>
     */
    Validator<Url> URL = new PropertyValidator<>(Property.URL);
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        uid        = "UID" uidparam ":" text CRLF
     *
     *        uidparam   = *(";" other-param)
     * </pre>
     */
    Validator<Uid> UID = new PropertyValidator<>(Property.UID);
}
