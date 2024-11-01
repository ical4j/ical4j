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

package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.util.RegEx;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FilterTarget {

    private final String name;

    private final Optional<String> value;

    private final List<Attribute> attributes;

    public FilterTarget(String spec) {
        this(spec, Collections.emptyList());
    }

    public FilterTarget(String spec, List<Attribute> attributes) {
        this.name = spec.split(RegEx.COLON_DELIMITED)[0].replace("_", "-");

        var split = spec.split(RegEx.COLON_DELIMITED);
        this.value = Optional.ofNullable(split.length > 1 ? split[1] : null);
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getValue() {
        return value;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (FilterTarget) o;
        return name.equals(that.name) && Objects.equals(value, that.value) && Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, attributes);
    }

    @Override
    public String toString() {
        return "FilterTarget{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", attributes=" + attributes +
                '}';
    }

    public static class Attribute {

        private final String name;

        private String value;

        public Attribute(String name) {
            this.name = name;
        }

        public Attribute(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var attribute = (Attribute) o;
            return name.equals(attribute.name) && Objects.equals(value, attribute.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

        public static Attribute parse(String string) {
            var name = string.split(RegEx.COLON_DELIMITED)[0];
            var value = string.contains(":") ? string.split(RegEx.COLON_DELIMITED)[1] : null;
            return new Attribute(name, value);
        }

        @Override
        public String toString() {
            return "Attribute{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
