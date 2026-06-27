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
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.ComponentTest;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URISyntaxException;
import java.util.stream.Stream;

/**
 * $Id$
 *
 * Created on: 25/11/2008
 *
 * @author fortuna
 */
public class VToDoTest {

    @AfterEach
    void tearDown() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }

    @ParameterizedTest(name = "isCalendarComponent")
    @MethodSource("isCalendarComponentData")
    public void testIsCalendarComponent(VToDo component) {
        ComponentTest.assertIsCalendarComponent(component);
    }

    @ParameterizedTest(name = "validationException")
    @MethodSource("validationExceptionData")
    public void testValidationException(VToDo component) {
        ComponentTest.assertValidationException(component);
    }

    @ParameterizedTest(name = "validation")
    @MethodSource("validationData")
    public void testValidation(VToDo component) throws ValidationException {
        ComponentTest.assertValidation(component);
    }

    @ParameterizedTest(name = "replyValidationException")
    @MethodSource("replyValidationExceptionData")
    public void testReplyValidationException(VToDo component) {
        CalendarComponentTest.assertReplyValidationException(component);
    }

    @ParameterizedTest(name = "replyValidation")
    @MethodSource("replyValidationData")
    public void testReplyValidation(VToDo component) throws ValidationException {
        CalendarComponentTest.assertReplyValidation(component);
    }

    static Stream<Arguments> isCalendarComponentData() {
        return Stream.of(Arguments.of(new VToDo()));
    }

    static Stream<Arguments> validationExceptionData() {
        return Stream.of(Arguments.of(new VToDo()));
    }

    static Stream<Arguments> validationData() throws URISyntaxException {
        var validTd = (VToDo) new VToDo().withProperty(new Uid("12")).getFluentTarget();
        return Stream.of(Arguments.of(validTd));
    }

    static Stream<Arguments> replyValidationExceptionData() {
        return Stream.of(Arguments.of(new VToDo()));
    }

    static Stream<Arguments> replyValidationData() throws URISyntaxException {
        var replyTd = (VToDo) new VToDo().withProperty(new Attendee("mailto:jane@example.com"))
                .withProperty(new Organizer("mailto:joe@example.com"))
                .withProperty(new Uid("12")).getFluentTarget();
        return Stream.of(Arguments.of(replyTd));
    }
}
