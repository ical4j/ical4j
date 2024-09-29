/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;

import java.util.List;
import java.util.Optional;

import static net.fortuna.ical4j.model.Parameter.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

/**
 * $Id$ [15-May-2004]
 *
 * Defines methods for validating properties and property lists.
 *
 * @author Ben Fortuna
 */
public final class PropertyValidator<T extends Property> extends AbstractValidator<T> {

    public static final ValidationRule<Property> DATE_OR_DATETIME_VALUE = new ValidationRule<>(prop -> {
        Optional<Value> v = prop.getParameter(VALUE);
        return !(v.isEmpty() || Value.DATE.equals(v.get()) || Value.DATE_TIME.equals(v.get()));
    }, "MUST be specified as a DATE or DATE-TIME:", VALUE);

    public static final ValidationRule<Property> BINARY_VALUE = new ValidationRule<>(prop -> {
        Optional<Value> v = prop.getParameter(VALUE);
        return !(v.isEmpty() || Value.BINARY.equals(v.get()));
    }, "MUST be specified as a BINARY:", VALUE);

    /**
     * <pre>
     *           FORM #3: DATE WITH LOCAL TIME AND TIME ZONE REFERENCE
     *
     *       The date and local time with reference to time zone information is
     *       identified by the use the "TZID" property parameter to reference
     *       the appropriate time zone definition.  "TZID" is discussed in
     *       detail in Section 3.2.19.  For example, the following represents
     *       2:00 A.M. in New York on January 19, 1998:
     *
     *        TZID=America/New_York:19980119T020000
     * </pre>
     */
    public static final PropertyRuleSet<DateProperty<?>> DATE_PROP_RULE_SET = new PropertyRuleSet<>(
            new ValidationRule<>(OneOrLess, VALUE, Parameter.TZID), DATE_OR_DATETIME_VALUE);

    /**
     * <pre>
     *           FORM #2: DATE WITH UTC TIME
     *
     *       The date with UTC time, or absolute time, is identified by a LATIN
     *       CAPITAL LETTER Z suffix character, the UTC designator, appended to
     *       the time value.  For example, the following represents January 19,
     *       1998, at 0700 UTC:
     *
     *        19980119T070000Z
     *
     *       The "TZID" property parameter MUST NOT be applied to DATE-TIME
     *       properties whose time values are specified in UTC.
     * </pre>
     */
    public static final PropertyRuleSet<Property> UTC_PROP_RULE_SET = new PropertyRuleSet<>(
            new ValidationRule<>(None, Parameter.TZID),
            new ValidationRule<>(ValueMatch, ".+Z$"));


    public static final Validator<BusyType> BUSY_TYPE = new PropertyValidator<>(Property.BUSYTYPE,
            new ValidationRule<>(ValueMatch, "(?i)" + String.join("|", BusyType.VALUE_BUSY,
                    BusyType.VALUE_BUSY_TENTATIVE, BusyType.VALUE_BUSY_UNAVAILABLE)));

    public static final Validator<Country> COUNTRY = new PropertyValidator<>(Property.COUNTRY,
            new ValidationRule<>(OneOrLess, ABBREV));

    public static final Validator<LocationType> LOCATION_TYPE = new PropertyValidator<>(Property.LOCATION_TYPE,
            new ValidationRule<>(OneOrLess, LANGUAGE));

    public static final Validator<Region> REGION = new PropertyValidator<>(Property.REGION,
            new ValidationRule<>(OneOrLess, ABBREV));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        rstatus    = "REQUEST-STATUS" rstatparam ":"
     *                     statcode ";" statdesc [";" extdata]
     *
     *        rstatparam = *(
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
     *
     *        statcode   = 1*DIGIT 1*2("." 1*DIGIT)
     *        ;Hierarchical, numeric return status code
     *
     *        statdesc   = text
     *        ;Textual status description
     *
     *        extdata    = text
     *        ;Textual exception data.  For example, the offending property
     *        ;name and value or complete property line.
     * </pre>
     */
    public static final Validator<RequestStatus> REQUEST_STATUS = new PropertyValidator<>(Property.REQUEST_STATUS,
            new ValidationRule<>(OneOrLess, LANGUAGE));

    public static final Validator<StructuredData> STRUCTURED_DATA = new PropertyValidator<>(Property.STRUCTURED_DATA,
            new ValidationRule<>(OneOrLess, FMTTYPE, SCHEMA));

    public static final Validator<StyledDescription> STYLED_DESCRIPTION = new PropertyValidator<>(Property.STYLED_DESCRIPTION,
            new ValidationRule<>(OneOrLess, ALTREP, FMTTYPE, LANGUAGE));

    public static final Validator<Xml> XML = new PropertyValidator<>(Property.XML,
            new ValidationRule<>(None, ENCODING, VALUE));

    public static final Validator<Xml> XML_BIN = new PropertyValidator<>(Property.XML,
            new ValidationRule<>(One, VALUE, ENCODING),
            new ValidationRule<>(xml -> !Optional.of(Encoding.BASE64).equals(xml.getParameter(ENCODING)),
                    "ENCODING=BASE64 for binary attachments", ENCODING),
            BINARY_VALUE);

    public static final Validator<Tel> TEL = new PropertyValidator<>(Property.TEL,
            new ValidationRule<>(OneOrLess, TYPE));

    @SafeVarargs
    public PropertyValidator(String context, ValidationRule<? super T>... rules) {
        super(context, new PropertyRuleSet<>(rules));
    }

    public PropertyValidator(String context, List<PropertyRuleSet<? super T>> rulesets) {
        super(context, rulesets.toArray(PropertyRuleSet[]::new));
    }
}
