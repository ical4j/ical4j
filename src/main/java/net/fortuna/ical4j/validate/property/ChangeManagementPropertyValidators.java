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
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

import java.util.Collections;

import static net.fortuna.ical4j.validate.PropertyValidator.UTC_PROP_RULE_SET;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.ValueMatch;

public interface ChangeManagementPropertyValidators {
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        created    = "CREATED" creaparam ":" date-time CRLF
     *
     *        creaparam  = *(";" other-param)
     * </pre>
     */
    Validator<Created> CREATED = new PropertyValidator<>(Property.CREATED,
            Collections.singletonList(UTC_PROP_RULE_SET));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        dtstamp    = "DTSTAMP" stmparam ":" date-time CRLF
     *
     *        stmparam   = *(";" other-param)
     * </pre>
     */
    Validator<DtStamp> DTSTAMP = new PropertyValidator<>(Property.DTSTAMP,
            Collections.singletonList(UTC_PROP_RULE_SET));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        last-mod   = "LAST-MODIFIED" lstparam ":" date-time CRLF
     *
     *        lstparam   = *(";" other-param)
     * </pre>
     */
    Validator<LastModified> LAST_MODIFIED = new PropertyValidator<LastModified>(Property.LAST_MODIFIED,
            Collections.singletonList(UTC_PROP_RULE_SET));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        seq = "SEQUENCE" seqparam ":" integer CRLF
     *        ; Default is "0"
     *
     *        seqparam   = *(";" other-param)
     * </pre>
     */
    Validator<Sequence> SEQUENCE = new PropertyValidator<>(Property.SEQUENCE,
            new ValidationRule<>(ValueMatch, "[0-9]+"));
}
