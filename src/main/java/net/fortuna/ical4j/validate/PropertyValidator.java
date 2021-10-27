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

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.util.Arrays;
import java.util.List;

import static net.fortuna.ical4j.model.Parameter.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.None;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

/**
 * $Id$ [15-May-2004]
 *
 * Defines methods for validating properties and property lists.
 *
 * @author Ben Fortuna
 */
public final class PropertyValidator<T extends Property> implements Validator<T> {

    public static final Validator<Attach> ATTACH = new PropertyValidator<>(
            new ValidationRule(OneOrLess, FMTTYPE));

    public static final Validator<Attendee> ATTENDEE = new PropertyValidator<>(
            new ValidationRule(OneOrLess, CUTYPE, MEMBER, ROLE, PARTSTAT,
                    RSVP, DELEGATED_TO, DELEGATED_FROM, SENT_BY, CN, DIR, LANGUAGE, SCHEDULE_AGENT, SCHEDULE_STATUS));

    public static final Validator<Categories> CATEGORIES = new PropertyValidator<>(
            new ValidationRule(OneOrLess, LANGUAGE));

    public static final Validator<Comment> COMMENT = new PropertyValidator<>(
            new ValidationRule(OneOrLess, ALTREP, LANGUAGE));

    public static final Validator<Contact> CONTACT = new PropertyValidator<>(
            new ValidationRule(ValidationRule.ValidationType.OneOrLess, ALTREP, LANGUAGE));

    public static final Validator<Country> COUNTRY = new PropertyValidator<>(
            new ValidationRule(OneOrLess, ABBREV));

    public static final Validator<Description> DESCRIPTION = new PropertyValidator<>(
            new ValidationRule(OneOrLess, ALTREP, LANGUAGE));

    public static final Validator<ExDate> EXDATE = new PropertyValidator<>(
            new ValidationRule(OneOrLess, VALUE, TZID));

    public static final Validator<FreeBusy> FREEBUSY = new PropertyValidator<>(
            new ValidationRule(OneOrLess, FBTYPE));

    public static final Validator<Location> LOCATION = new PropertyValidator<>(
            new ValidationRule(OneOrLess, ALTREP, LANGUAGE, VVENUE));

    public static final Validator<LocationType> LOCATION_TYPE = new PropertyValidator<>(
            new ValidationRule(OneOrLess, LANGUAGE));

    public static final Validator<Organizer> ORGANIZER = new PropertyValidator<>(
            new ValidationRule(OneOrLess, CN, DIR, SENT_BY, LANGUAGE, SCHEDULE_STATUS));

    public static final Validator<RDate> RDATE = new PropertyValidator<>(
            new ValidationRule(OneOrLess, VALUE, TZID));

    public static final Validator<RecurrenceId> RECURRENCE_ID = new PropertyValidator<>(
            new ValidationRule(OneOrLess, RANGE));

    public static final Validator<Region> REGION = new PropertyValidator<>(
            new ValidationRule(OneOrLess, ABBREV));

    public static final Validator<RelatedTo> RELATED_TO = new PropertyValidator<>(
            new ValidationRule(OneOrLess, RELTYPE));

    public static final Validator<RequestStatus> REQUEST_STATUS = new PropertyValidator<>(
            new ValidationRule(OneOrLess, LANGUAGE));

    public static final Validator<Resources> RESOURCES = new PropertyValidator<>(
            new ValidationRule(OneOrLess, ALTREP, LANGUAGE));

    public static final Validator<RRule> RRULE = new PropertyValidator<>(
            new ValidationRule(None, TZID));

    public static final Validator<StructuredData> STRUCTURED_DATA = new PropertyValidator<>(
            new ValidationRule(OneOrLess, FMTTYPE, SCHEMA));

    public static final Validator<StyledDescription> STYLED_DESCRIPTION = new PropertyValidator<>(
            new ValidationRule(OneOrLess, ALTREP, FMTTYPE, LANGUAGE));

    public static final Validator<Summary> SUMMARY = new PropertyValidator<>(
            new ValidationRule(OneOrLess, ALTREP, LANGUAGE));

    public static final Validator<Tel> TEL = new PropertyValidator<>(
            new ValidationRule(OneOrLess, TYPE));

    public static final Validator<Trigger> TRIGGER = new PropertyValidator<>(
            new ValidationRule(OneOrLess, RELATED));

    public static final Validator<TzName> TZ_NAME = new PropertyValidator<>(
            new ValidationRule(OneOrLess, LANGUAGE));

    private final List<ValidationRule> rules;

    public PropertyValidator(ValidationRule... rules) {
        this(Arrays.asList(rules));
    }
    
    public PropertyValidator(List<ValidationRule> rules) {
        this.rules = rules;
    }

    @Override
    public void validate(Property target) throws ValidationException {
        ValidationResult result = new ValidationResult();
        for (ValidationRule rule : rules) {
            boolean warnOnly = CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
                    && rule.isRelaxedModeSupported();

            if (warnOnly) {
                result.getWarnings().addAll(apply(rule, target));
            } else {
                result.getErrors().addAll(apply(rule, target));
            }
        }
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
    }
}
