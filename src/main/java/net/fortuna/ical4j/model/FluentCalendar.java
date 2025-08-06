/*
 *  Copyright (c) 2022, Ben Fortuna
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

import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.ProdId;

import static net.fortuna.ical4j.model.property.immutable.ImmutableCalScale.GREGORIAN;
import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0;

/**
 * An interface for a fluent API to build iCalendar {@link Calendar} objects.
 * Provides methods to add default properties, product ID, custom properties, and components to the calendar.
 * <p>
 * Example usage:
 * <pre>
 * FluentCalendar calendar = new FluentCalendarImpl();
 * calendar.withDefaults()
 *         .withProdId("-//My Company//My Product//EN")
 *         .withProperty(new CustomProperty("X-CUSTOM", "value"))
 *         .withComponent(new EventComponent("My Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
 * </pre>
 * This interface allows for a more readable and maintainable way to construct calendar objects by chaining method calls.
 * Each method returns the same instance of the calendar, allowing for a fluent style of programming.
 * <p>
 * Implementations of this interface should provide the actual calendar object and handle the addition of properties and components.
 * The methods defined here are intended to be used in a fluent manner, allowing for easy chaining of method calls to build up a calendar object step by step.
 * <p>
 * Note: This interface does not define how the calendar is serialized or encoded; it focuses solely on the fluent construction of the calendar object.
 * Implementations should ensure that the final calendar object can be serialized to the iCalendar format as per the iCalendar specification.
 * <p>
 * This interface is part of the iCal4j library, which provides a comprehensive set of tools for working with iCalendar data.
 * It is designed to be used in Java applications that need to create, manipulate, or read iCalendar files.
 * The fluent API style enhances readability and usability, making it easier for developers to work with calendar data without dealing with the complexities of the underlying iCalendar model directly.
 * <p>
 * Implementations of this interface should ensure that the calendar object is properly initialized and that all methods
 * return the same instance of the calendar to maintain the fluent API style.
 * <p>
 */
public interface FluentCalendar {

    Calendar getFluentTarget();

    default FluentCalendar withDefaults() {
        return getFluentTarget().add(GREGORIAN).add(VERSION_2_0);
    }

    default FluentCalendar withProdId(String prodId) {
        return getFluentTarget().add(new ProdId(prodId));
    }

    default FluentCalendar withProperty(Property property) {
        return getFluentTarget().add(property);
    }

    default FluentCalendar withComponent(CalendarComponent component) {
        return getFluentTarget().add(component);
    }
}
