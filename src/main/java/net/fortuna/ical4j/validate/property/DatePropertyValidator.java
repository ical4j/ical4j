/*
 *  Copyright (c) 2021, Ben Fortuna
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

import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationRule;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.function.Predicate;

import static net.fortuna.ical4j.model.Parameter.TZID;
import static net.fortuna.ical4j.model.Parameter.VALUE;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

public class DatePropertyValidator<T extends Temporal> extends PropertyValidator<DateProperty<T>> {

    public DatePropertyValidator() {
        super(new ValidationRule<>(OneOrLess, VALUE),
                new ValidationRule<>(One, (Predicate<DateProperty<T>> & Serializable) p ->
                        p.getDate() instanceof LocalDate, VALUE),
                new ValidationRule<>(None, (Predicate<DateProperty<T>> & Serializable) DateProperty::isUtc, TZID),
                new ValidationRule<>(OneOrLess, (Predicate<DateProperty<T>> & Serializable) p -> !p.isUtc(), TZID));
    }
}
