/**
 * Copyright (c) 2004-2021, Ben Fortuna
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
package net.fortuna.ical4j.filter.predicate;

import net.fortuna.ical4j.model.PropertyContainer;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Sequence;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;

/**
 * Test for a property that is by comparison less than the provided value.
 *
 * @param <T>
 */
public class PropertyLessThanRule<T extends PropertyContainer> implements Predicate<T> {

    private final String propertyName;

    private final Object value;

    private final boolean inclusive;

    public PropertyLessThanRule(String propertyName, Object value) {
        this(propertyName, value, false);
    }

    public PropertyLessThanRule(String propertyName, Object value, boolean inclusive) {
        this.propertyName = propertyName;
        this.value = value;
        this.inclusive = inclusive;
    }

    @Override
    public boolean test(T t) {
        if ("sequence".equalsIgnoreCase(propertyName)) {
            Sequence sequence = t.getProperty(propertyName);
            if (sequence != null) {
                return inclusive ? sequence.getSequenceNo() <= Integer.parseInt(value.toString())
                        : sequence.getSequenceNo() < Integer.parseInt(value.toString());
            }
        } else if (Arrays.asList("due").contains(propertyName)) {
            DateProperty dateProperty = t.getProperty(propertyName);
            return inclusive ? dateProperty.getDate().compareTo(Date.from(Instant.from((Temporal) value))) < 0
                    : dateProperty.getDate().compareTo(Date.from(Instant.from((Temporal) value))) <= 0;
        }
        return false;
    }
}
