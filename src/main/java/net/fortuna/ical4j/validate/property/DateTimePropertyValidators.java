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

import java.util.Collections;

import static net.fortuna.ical4j.model.Parameter.FBTYPE;
import static net.fortuna.ical4j.model.property.immutable.ImmutableTransp.OPAQUE;
import static net.fortuna.ical4j.model.property.immutable.ImmutableTransp.TRANSPARENT;
import static net.fortuna.ical4j.validate.PropertyValidator.UTC_PROP_RULE_SET;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.ValueMatch;

public interface DateTimePropertyValidators {
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        completed  = "COMPLETED" compparam ":" date-time CRLF
     *
     *        compparam  = *(";" other-param)
     * </pre>
     */
    Validator<Completed> COMPLETED = new PropertyValidator<>(Property.COMPLETED,
            Collections.singletonList(UTC_PROP_RULE_SET));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        dtend      = "DTEND" dtendparam ":" dtendval CRLF
     *
     *        dtendparam = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
     *                   (";" tzidparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        dtendval   = date-time / date
     *        ;Value MUST match value type
     * </pre>
     */
    Validator<DtEnd<?>> DTEND = new PropertyValidator<>(Property.DTEND,
            Collections.singletonList(PropertyValidator.DATE_PROP_RULE_SET));
    /**
     * <pre>
     *       Format Definition:  This property is defined by the following
     *       notation:
     *
     *        due        = "DUE" dueparam ":" dueval CRLF
     *
     *        dueparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
     *                   (";" tzidparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        dueval     = date-time / date
     *        ;Value MUST match value type
     * </pre>
     */
    Validator<Due<?>> DUE = new PropertyValidator<>(Property.DUE,
            Collections.singletonList(PropertyValidator.DATE_PROP_RULE_SET));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        dtstart    = "DTSTART" dtstparam ":" dtstval CRLF
     *
     *        dtstparam  = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
     *                   (";" tzidparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        dtstval    = date-time / date
     *        ;Value MUST match value type
     * </pre>
     */
    Validator<DtStart<?>> DTSTART = new PropertyValidator<>(Property.DTSTART,
            Collections.singletonList(PropertyValidator.DATE_PROP_RULE_SET));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        duration   = "DURATION" durparam ":" dur-value CRLF
     *                     ;consisting of a positive duration of time.
     *
     *        durparam   = *(";" other-param)
     * </pre>
     */
    Validator<Duration> DURATION = new PropertyValidator<>(Property.DURATION);
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        freebusy   = "FREEBUSY" fbparam ":" fbvalue CRLF
     *
     *        fbparam    = *(
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" fbtypeparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        fbvalue    = period *("," period)
     *        ;Time value MUST be in the UTC time format.
     * </pre>
     */
    Validator<FreeBusy> FREEBUSY = new PropertyValidator<>(Property.FREEBUSY,
            new ValidationRule<>(OneOrLess, FBTYPE));
    /**
     * <pre>
     *       Format Definition:  This property is defined by the following
     *       notation:
     *
     *        transp     = "TRANSP" transparam ":" transvalue CRLF
     *
     *        transparam = *(";" other-param)
     *
     *        transvalue = "OPAQUE"
     *                    ;Blocks or opaque on busy time searches.
     *                    / "TRANSPARENT"
     *                    ;Transparent on busy time searches.
     *        ;Default value is OPAQUE
     * </pre>
     */
    Validator<Transp> TRANSP = new PropertyValidator<>(Property.TRANSP,
            new ValidationRule<>(ValueMatch, "(?i)" + String.join("|",
                    OPAQUE.getValue(), TRANSPARENT.getValue())));
}
