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

import static net.fortuna.ical4j.model.Parameter.LANGUAGE;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

public interface TimeZonePropertyValidators {
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        tzid       = "TZID" tzidpropparam ":" [tzidprefix] text CRLF
     *
     *        tzidpropparam      = *(";" other-param)
     *
     *        ;tzidprefix        = "/"
     *        ; Defined previously. Just listed here for reader convenience.
     * </pre>
     */
    Validator<TzId> TZID = new PropertyValidator<>(Property.TZID);
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        tzname     = "TZNAME" tznparam ":" text CRLF
     *
     *        tznparam   = *(
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
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
    Validator<TzName> TZNAME = new PropertyValidator<>(Property.TZNAME,
            new ValidationRule<>(OneOrLess, LANGUAGE));
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        tzoffsetfrom       = "TZOFFSETFROM" frmparam ":" utc-offset
     *                             CRLF
     *
     *        frmparam   = *(";" other-param)
     * </pre>
     */
    Validator<TzOffsetFrom> TZOFFSETFROM = new PropertyValidator<>(Property.TZOFFSETFROM);
    /**
     * <pre>
     *     Format Definition:  This property is defined by the following
     *       notation:
     *
     *        tzoffsetto = "TZOFFSETTO" toparam ":" utc-offset CRLF
     *
     *        toparam    = *(";" other-param)
     * </pre>
     */
    Validator<TzOffsetTo> TZOFFSETTO = new PropertyValidator<>(Property.TZOFFSETTO);
    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        tzurl      = "TZURL" tzurlparam ":" uri CRLF
     *
     *        tzurlparam = *(";" other-param)
     * </pre>
     */
    Validator<TzUrl> TZURL = new PropertyValidator<>(Property.TZURL);
}
