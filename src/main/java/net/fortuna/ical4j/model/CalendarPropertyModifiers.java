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

import net.fortuna.ical4j.model.property.Method;

import java.util.function.BiFunction;

/**
 * A collection of functions used to modify calendar properties in a target property container.
 * Used in conjunction with {@link PropertyContainer#with(BiFunction, Object)}
 * <p> This interface provides a method to replace the Method property in a PropertyContainer.
 * It is designed to be used with a lambda expression or method reference that takes a PropertyContainer and a Method,
 * and returns a modified PropertyContainer.
 * <p>
 * Example usage:
 * <pre>
 * PropertyContainer container = new PropertyContainer();
 * Method method = new Method("PUBLISH");
 * PropertyContainer modifiedContainer = container.with(CalendarPropertyModifiers.METHOD, method);
 * </pre>
 * This will replace the existing Method property in the container with the new Method instance.
 * If the Method property does not exist, it will be added.
 * </p>
 * <p> Note: This interface is intended for internal use within the iCal4j library and may not
 * be suitable for general use outside the library's context.
 * It is primarily used to provide a consistent way to modify calendar properties in a PropertyContainer.
 * </p>
 */
public interface CalendarPropertyModifiers {

    BiFunction<PropertyContainer, Method, PropertyContainer> METHOD = (c, p) -> {
        if (p != null) c.replace(p); return c;
    };
}
