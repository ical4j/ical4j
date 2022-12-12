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

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyContainer;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Test for a property matching the supplied specification.
 */
public class PropertyExistsRule<T extends PropertyContainer> implements Predicate<T> {

    private final Property specification;

    public PropertyExistsRule(Property specification) {
        this.specification = specification;
    }

    @Override
    public boolean test(T t) {
        return new PropertyEqualToRule<>(new PropertyExists(specification)).test(t);
    }

    /**
     * Ignore the property value and just compare on the property name and parameters.
     */
    public static class PropertyExists implements Comparable<Property> {

        private final Property specification;

        public PropertyExists(Property specification) {
            this.specification = specification;
        }

        @Override
        public int compareTo(Property o) {
            return Comparator.comparing(Property::getName)
                    .thenComparing((Function<Property, ParameterList>) Property::getParameters)
                    .compare(specification, o);
        }
    }
}
