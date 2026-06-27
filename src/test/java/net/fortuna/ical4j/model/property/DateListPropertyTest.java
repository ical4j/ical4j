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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.TzId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * $Id$
 *
 * Created on 01/03/2011
 *
 * Unit tests specific to {@link DateListProperty} and its subclasses.
 * @author arnaudq
 */
public class DateListPropertyTest {

    @ParameterizedTest(name = "copy")
    @MethodSource("copyData")
    public void testCopy(DateListProperty<?> property) throws URISyntaxException {
        Property copy = property.copy();
        assertEquals(property, copy);
    }

    static Stream<Arguments> copyData() throws ParseException {
        ExDate<Instant> exZulu = new ExDate<>();
        exZulu.setValue("20111212T000000Z");

        @SuppressWarnings("unchecked")
        ExDate<ZonedDateTime> exMelbourne = (ExDate<ZonedDateTime>) new ExDate<>()
                .withParameter(new TzId("Australia/Melbourne")).getFluentTarget();
        exMelbourne.setValue("20111212T000000");

        return Stream.of(
                Arguments.of(exZulu),
                Arguments.of(exMelbourne)
        );
    }
}
