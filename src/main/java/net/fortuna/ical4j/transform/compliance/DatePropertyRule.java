/*
 *  Copyright (c) 2024, Ben Fortuna
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

package net.fortuna.ical4j.transform.compliance;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DateProperty;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * A rule that applies compliance transformations to DateProperty elements.
 * This rule ensures that the timezone parameter is correctly set for date properties
 * according to RFC 5545.
 *
 * @author daniel grigore
 * @author corneliu dobrota
 */
public class DatePropertyRule implements Rfc5545PropertyRule<DateProperty> {

    @Override
    @SuppressWarnings("unchecked")
    public DateProperty apply(DateProperty element) {
        Optional<TzId> originalTzId = element.getParameter(Parameter.TZID);
        TzHelper.correctTzParameterFrom(element);
        // When an unknown TZID is stripped from a zoned date-time, preserve the absolute
        // instant by converting the value to UTC rather than leaving it as floating time.
        if (originalTzId.isPresent()
                && element.getParameter(Parameter.TZID).isEmpty()
                && element.getDate() instanceof ZonedDateTime
                && !element.isUtc()) {
            element.setDate(((ZonedDateTime) element.getDate()).toInstant());
            return element;
        }
        if (!element.isUtc() || element.getParameter(Parameter.TZID).isEmpty()) {
            return element;
        }
        element.getParameters().removeIf(p -> p.getName().equalsIgnoreCase(Parameter.TZID));
        return element;
    }

    @Override
    public Class<DateProperty> getSupportedType() {
        return DateProperty.class;
    }

}
