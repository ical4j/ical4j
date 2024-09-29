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
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

import static net.fortuna.ical4j.model.property.Method.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableCalScale.GREGORIAN;
import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.ValueMatch;

public interface CalendarPropertyValidators {
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        method     = "METHOD" metparam ":" metvalue CRLF
     *
     *        metparam   = *(";" other-param)
     *
     *        metvalue   = iana-token
     * </pre>
     */
    Validator<Method> METHOD = new PropertyValidator<>(Property.METHOD,
            new ValidationRule<>(ValueMatch, "(?i)" + String.join("|", VALUE_PUBLISH,
                    VALUE_REQUEST, VALUE_REPLY, VALUE_ADD, VALUE_CANCEL,
                    VALUE_COUNTER, VALUE_DECLINECOUNTER, VALUE_REFRESH)));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        calscale   = "CALSCALE" calparam ":" calvalue CRLF
     *
     *        calparam   = *(";" other-param)
     *
     *        calvalue   = "GREGORIAN"
     * </pre>
     */
    Validator<CalScale> CALSCALE = new PropertyValidator<>(Property.CALSCALE,
            new ValidationRule<>(ValueMatch, "(?i)" + GREGORIAN.getValue()));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        version    = "VERSION" verparam ":" vervalue CRLF
     *
     *        verparam   = *(";" other-param)
     *
     *        vervalue   = "2.0"         ;This memo
     *                   / maxver
     *                   / (minver ";" maxver)
     *
     *        minver     = &lt;A IANA-registered iCalendar version identifier&gt;
     *        ;Minimum iCalendar version needed to parse the iCalendar object.
     *
     *        maxver     = &lt;A IANA-registered iCalendar version identifier&gt;
     *        ;Maximum iCalendar version needed to parse the iCalendar object.
     * </pre>
     */
    Validator<Version> VERSION = new PropertyValidator<>(Property.VERSION,
            new ValidationRule<>(ValueMatch, "(?i)" + VERSION_2_0.getValue()));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        prodid     = "PRODID" pidparam ":" pidvalue CRLF
     *
     *        pidparam   = *(";" other-param)
     *
     *        pidvalue   = text
     *        ;Any text that describes the product and version
     *        ;and that is generally assured of being unique.
     * </pre>
     */
    Validator<ProdId> PROD_ID = new PropertyValidator<>(Property.PRODID,
            new ValidationRule<>(ValueMatch, "\\w+"));
}
