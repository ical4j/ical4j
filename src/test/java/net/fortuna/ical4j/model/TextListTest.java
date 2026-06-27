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
package net.fortuna.ical4j.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * $Id$
 *
 * Created on 16/11/2005
 *
 */
public class TextListTest {

    private static final String VALUE_RESOURCE_LIST = "projector,laptop,pulpit";

    /**
     * Assert three resourcees parsed from value.
     */
    @ParameterizedTest(name = "size [{0}]")
    @MethodSource("sizeData")
    public void testSize(TextList resources, int expectedSize) {
        assertEquals(expectedSize, resources.getTexts().size());
    }

    static Stream<Arguments> sizeData() {
        TextList resources = new TextList(VALUE_RESOURCE_LIST);
        return Stream.of(
                Arguments.of(resources, 3)
        );
    }

    /**
     * Assert toString() produces identical resource list string value.
     */
    @ParameterizedTest(name = "toString [{0}]")
    @MethodSource("toStringData")
    public void testToString(TextList resources, String expectedToString) {
        assertEquals(expectedToString, resources.toString());
    }

    static Stream<Arguments> toStringData() {
        TextList resources = new TextList(VALUE_RESOURCE_LIST);
        return Stream.of(
                Arguments.of(resources, VALUE_RESOURCE_LIST)
        );
    }
}
