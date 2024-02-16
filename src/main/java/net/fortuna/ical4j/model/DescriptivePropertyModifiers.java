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

import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Summary;

import java.util.function.BiFunction;

/**
 * A collection of functions used to modify date-time properties in a target property container.
 * Used in conjunction with {@link PropertyContainer#with(BiFunction, Object)}
 */
public interface DescriptivePropertyModifiers {

    BiFunction<PropertyContainer, byte[], PropertyContainer> ATTACHMENT = (c, p) -> {
        if (p != null) c.add(new Attach(p)); return c;
    };

    BiFunction<PropertyContainer, String[], PropertyContainer> CATEGORIES = (c, p) -> {
        if (p != null) c.add(new Categories(new TextList(p))); return c;
    };

    BiFunction<PropertyContainer, String, PropertyContainer> SUMMARY = (c, p) -> {
        if (p != null) c.replace(new Summary(p)); return c;
    };

    BiFunction<PropertyContainer, String, PropertyContainer> DESCRIPTION = (c, p) -> {
        if (p != null) c.replace(new Description(p)); return c;
    };

    static <T extends PropertyContainer> BiFunction<T, String, T> summary() {
        return (BiFunction<T, String, T>) SUMMARY;
    }
}
