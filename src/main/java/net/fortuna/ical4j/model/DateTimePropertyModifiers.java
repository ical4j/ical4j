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

package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.property.*;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.function.BiFunction;

/**
 * A collection of functions used to modify date-time properties in a target property container.
 * Used in conjunction with {@link PropertyContainer#with(BiFunction, Object)}
 */
public interface DateTimePropertyModifiers {

    BiFunction<PropertyContainer, Instant, PropertyContainer> COMPLETED = (c, p) -> {
        if (p != null) c.replace(new Completed(p)); return c;
    };

    BiFunction<PropertyContainer, Temporal, PropertyContainer> DTEND = (c, p) -> {
        if (p != null) {
            c.replace(new DtEnd<>(p));
            c.removeAll(Property.DURATION);
        }
        return c;
    };

    BiFunction<PropertyContainer, Temporal, PropertyContainer> DUE = (c, p) -> {
        if (p != null) c.replace(new Due<>(p)); return c;
    };

    BiFunction<PropertyContainer, Temporal, PropertyContainer> DTSTART = (c, p) -> {
        if (p != null) c.replace(new DtStart<>(p)); return c;
    };

    BiFunction<PropertyContainer, TemporalAmount, PropertyContainer> DURATION = (c, p) -> {
        if (p != null) {
            c.replace(new Duration(p));
            c.removeAll(Property.DTEND);
        }
        return c;
    };

    BiFunction<PropertyContainer, Property, PropertyContainer> FREEBUSY = (c, p) -> {
        if (p != null) c.replace(p); return c;
    };

    BiFunction<PropertyContainer, Transp, PropertyContainer> TRANSP = (c, p) -> {
        if (p != null) c.replace(p); return c;
    };
}
