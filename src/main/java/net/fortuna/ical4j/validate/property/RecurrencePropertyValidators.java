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

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

import java.util.Optional;

import static net.fortuna.ical4j.model.Parameter.VALUE;
import static net.fortuna.ical4j.model.parameter.Value.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.None;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

public interface RecurrencePropertyValidators {
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        exdate     = "EXDATE" exdtparam ":" exdtval *("," exdtval) CRLF
     *
     *        exdtparam  = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
     *                   ;
     *                   (";" tzidparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        exdtval    = date-time / date
     *        ;Value MUST match value type
     * </pre>
     */
    Validator<ExDate<?>> EXDATE = new PropertyValidator<>(Property.EXDATE,
            new ValidationRule<>(OneOrLess, VALUE, Parameter.TZID),
            PropertyValidator.DATE_OR_DATETIME_VALUE);
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        rdate      = "RDATE" rdtparam ":" rdtval *("," rdtval) CRLF
     *
     *        rdtparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE" / "PERIOD")) /
     *                   (";" tzidparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        rdtval     = date-time / date / period
     *        ;Value MUST match value type
     * </pre>
     */
    Validator<RDate<?>> RDATE = new PropertyValidator<>(Property.RDATE,
            new ValidationRule<>(OneOrLess, VALUE, Parameter.TZID),
            new ValidationRule<>(rdate -> {
                Optional<Value> v = rdate.getParameter(VALUE);
                return !(v.isEmpty() || DATE.equals(v.get()) || DATE_TIME.equals(v.get()) || PERIOD.equals(v.get()));
            }, VALUE));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        rrule      = "RRULE" rrulparam ":" recur CRLF
     *
     *        rrulparam  = *(";" other-param)
     * </pre>
     */
    Validator<RRule<?>> RRULE = new PropertyValidator<>(Property.RRULE,
            new ValidationRule<>(None, Parameter.TZID));
}
