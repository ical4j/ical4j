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
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

import java.util.Optional;

import static net.fortuna.ical4j.model.Parameter.RELATED;
import static net.fortuna.ical4j.model.Parameter.VALUE;
import static net.fortuna.ical4j.model.property.immutable.ImmutableAction.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

public interface AlarmPropertyValidators {
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        action      = "ACTION" actionparam ":" actionvalue CRLF
     *
     *        actionparam = *(";" other-param)
     *
     *
     *        actionvalue = "AUDIO" / "DISPLAY" / "EMAIL"
     *                    / iana-token / x-name
     * </pre>
     */
    Validator<Action> ACTION = new PropertyValidator<>(Property.ACTION,
            new ValidationRule<>(ValueMatch, "(?i)" + String.join("|",
                    AUDIO.getValue(), DISPLAY.getValue(), EMAIL.getValue(), "X-[A-Z]+")));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        repeat  = "REPEAT" repparam ":" integer CRLF
     *        ;Default is "0", zero.
     *
     *        repparam   = *(";" other-param)
     * </pre>
     */
    Validator<Repeat> REPEAT = new PropertyValidator<>(Property.REPEAT,
            new ValidationRule<>(ValueMatch, "[0-9]+"));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        trigger    = "TRIGGER" (trigrel / trigabs) CRLF
     *
     *        trigabs    = *(
     *                   ;
     *                   ; The following is REQUIRED,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" "DATE-TIME") /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   ) ":" date-time
     * </pre>
     */
    Validator<Trigger> TRIGGER_ABS = new PropertyValidator<>(TRIGGER,
            new ValidationRule<>(One, VALUE),
            new ValidationRule<>(None, RELATED),
            new ValidationRule<>(trigger -> {
                Optional<Value> v = trigger.getParameter(VALUE);
                return !(v.isEmpty() || Value.DATE_TIME.equals(v.get()));
            }, "MUST be specified as a UTC-formatted DATE-TIME:", VALUE));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        trigger    = "TRIGGER" (trigrel / trigabs) CRLF
     *
     *        trigrel    = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" "DURATION") /
     *                   (";" trigrelparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   ) ":"  dur-value
     * </pre>
     */
    Validator<Trigger> TRIGGER_REL = new PropertyValidator<>(TRIGGER,
            new ValidationRule<>(OneOrLess, VALUE, RELATED),
            new ValidationRule<>(trigger -> {
                Optional<Value> v = trigger.getParameter(VALUE);
                return !(v.isEmpty() || Value.DURATION.equals(v.get()));
            }, "MUST be specified as a DURATION:", VALUE));
}
