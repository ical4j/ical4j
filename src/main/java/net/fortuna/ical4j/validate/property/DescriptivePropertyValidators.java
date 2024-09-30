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

import java.util.Optional;

import static net.fortuna.ical4j.model.Parameter.*;
import static net.fortuna.ical4j.model.parameter.Encoding.BASE64;
import static net.fortuna.ical4j.model.property.immutable.ImmutableClazz.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableStatus.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

public interface DescriptivePropertyValidators {
    /**
     * <pre>
     * Format Definition:  This property is defined by the following
     *       notation:
     *
     *        attach     = "ATTACH" attachparam ( ":" uri ) /
     *                     (
     *                       ";" "ENCODING" "=" "BASE64"
     *                       ";" "VALUE" "=" "BINARY"
     *                       ":" binary
     *                     )
     *                     CRLF
     *
     *        attachparam = *(
     *                    ;
     *                    ; The following is OPTIONAL for a URI value,
     *                    ; RECOMMENDED for a BINARY value,
     *                    ; and MUST NOT occur more than once.
     *                    ;
     *                    (";" fmttypeparam) /
     *                    ;
     *                    ; The following is OPTIONAL,
     *                    ; and MAY occur more than once.
     *                    ;
     *                    (";" other-param)
     *                    ;
     *                    )
     *                    </pre>
     */
    Validator<Attach> ATTACH_URI = new PropertyValidator<>(Property.ATTACH,
            new ValidationRule<>(OneOrLess, FMTTYPE));
    /**
     * @see DescriptivePropertyValidators#ATTACH_URI
     */
    Validator<Attach> ATTACH_BIN = new PropertyValidator<>(Property.ATTACH,
            new ValidationRule<>(OneOrLess, FMTTYPE),
            new ValidationRule<>(One, VALUE, ENCODING),
            new ValidationRule<>(attach -> !Optional.of(BASE64).equals(attach.getParameter(ENCODING)),
                    "ENCODING=BASE64 for binary attachments", ENCODING),
            PropertyValidator.BINARY_VALUE);
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        categories = "CATEGORIES" catparam ":" text *("," text)
     *                     CRLF
     *
     *        catparam   = *(
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" languageparam ) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    Validator<Categories> CATEGORIES = new PropertyValidator<>(Property.CATEGORIES,
            new ValidationRule<>(OneOrLess, LANGUAGE));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        class      = "CLASS" classparam ":" classvalue CRLF
     *
     *        classparam = *(";" other-param)
     *
     *        classvalue = "PUBLIC" / "PRIVATE" / "CONFIDENTIAL" / iana-token
     *                   / x-name
     *        ;Default is PUBLIC
     * </pre>
     */
    Validator<Clazz> CLAZZ = new PropertyValidator<>(Property.CLASS,
            new ValidationRule<>(ValueMatch, "(?i)" + String.join("|",
                    PUBLIC.getValue(), PRIVATE.getValue(), CONFIDENTIAL.getValue())));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        comment    = "COMMENT" commparam ":" text CRLF
     *
     *        commparam  = *(
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
    Validator<Comment> COMMENT = new PropertyValidator<>(Property.COMMENT,
            new ValidationRule<>(OneOrLess, ALTREP, LANGUAGE));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        description = "DESCRIPTION" descparam ":" text CRLF
     *
     *        descparam   = *(
     *                    ;
     *                    ; The following are OPTIONAL,
     *                    ; but MUST NOT occur more than once.
     *                    ;
     *                    (";" altrepparam) / (";" languageparam) /
     *                    ;
     *                    ; The following is OPTIONAL,
     *                    ; and MAY occur more than once.
     *                    ;
     *                    (";" other-param)
     *                    ;
     *                    )
     * </pre>
     */
    Validator<Description> DESCRIPTION = new PropertyValidator<>(Property.DESCRIPTION,
            new ValidationRule<>(OneOrLess, ALTREP, LANGUAGE));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        geo        = "GEO" geoparam ":" geovalue CRLF
     *
     *        geoparam   = *(";" other-param)
     *
     *        geovalue   = float ";" float
     *        ;Latitude and Longitude components
     * </pre>
     */
    Validator<Geo> GEO = new PropertyValidator<>(Property.GEO,
            new ValidationRule<>(ValueMatch, "([0-9]*[.])?[0-9]+;([0-9]*[.])?[0-9]+"));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        location   = "LOCATION"  locparam ":" text CRLF
     *
     *        locparam   = *(
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
    Validator<Location> LOCATION = new PropertyValidator<>(Property.LOCATION,
            new ValidationRule<>(OneOrLess, ALTREP, LANGUAGE));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        percent = "PERCENT-COMPLETE" pctparam ":" integer CRLF
     *
     *        pctparam   = *(";" other-param)
     * </pre>
     */
    Validator<PercentComplete> PERCENT_COMPLETE = new PropertyValidator<>(Property.PERCENT_COMPLETE,
            new ValidationRule<>(ValueMatch, "[0-9]{1,2}|100"));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        priority   = "PRIORITY" prioparam ":" priovalue CRLF
     *        ;Default is zero (i.e., undefined).
     *
     *        prioparam  = *(";" other-param)
     *
     *        priovalue   = integer       ;Must be in the range [0..9]
     *           ; All other values are reserved for future use.
     * </pre>
     */
    Validator<Priority> PRIORITY = new PropertyValidator<>(Property.PRIORITY,
            new ValidationRule<>(ValueMatch, "[0-9]"));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        resources  = "RESOURCES" resrcparam ":" text *("," text) CRLF
     *
     *        resrcparam = *(
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
    Validator<Resources> RESOURCES = new PropertyValidator<>(Property.RESOURCES,
            new ValidationRule<>(OneOrLess, ALTREP, LANGUAGE));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        status          = "STATUS" statparam ":" statvalue CRLF
     *
     *        statparam       = *(";" other-param)
     *
     *        statvalue       = (statvalue-event
     *                        /  statvalue-todo
     *                        /  statvalue-jour)
     *
     *        statvalue-event = "TENTATIVE"    ;Indicates event is tentative.
     *                        / "CONFIRMED"    ;Indicates event is definite.
     *                        / "CANCELLED"    ;Indicates event was cancelled.
     *        ;Status values for a "VEVENT"
     *
     *        statvalue-todo  = "NEEDS-ACTION" ;Indicates to-do needs action.
     *                        / "COMPLETED"    ;Indicates to-do completed.
     *                        / "IN-PROCESS"   ;Indicates to-do in process of.
     *                        / "CANCELLED"    ;Indicates to-do was cancelled.
     *        ;Status values for "VTODO".
     *
     *        statvalue-jour  = "DRAFT"        ;Indicates journal is draft.
     *                        / "FINAL"        ;Indicates journal is final.
     *                        / "CANCELLED"    ;Indicates journal is removed.
     *       ;Status values for "VJOURNAL".
     * </pre>
     */
    Validator<Status> STATUS = new PropertyValidator<>(Property.STATUS,
        new ValidationRule<>(ValueMatch, "(?i)" + String.join("|", VEVENT_TENTATIVE.getValue(),
                VEVENT_CONFIRMED.getValue(), VEVENT_CANCELLED.getValue(),
                VTODO_NEEDS_ACTION.getValue(), VTODO_COMPLETED.getValue(),
                VTODO_IN_PROCESS.getValue(), VTODO_CANCELLED.getValue(),
                VJOURNAL_DRAFT.getValue(), VJOURNAL_FINAL.getValue(), VJOURNAL_CANCELLED.getValue())));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        summary    = "SUMMARY" summparam ":" text CRLF
     *
     *        summparam  = *(
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
    Validator<Summary> SUMMARY = new PropertyValidator<>(Property.SUMMARY,
            new ValidationRule<>(OneOrLess, ALTREP, LANGUAGE));
}
