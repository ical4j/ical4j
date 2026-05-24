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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * $Id$
 * <p/>
 * Created on 6/08/2005
 *
 * @author Ben
 */
public class NumberListTest {

    private static final Logger LOG = LoggerFactory.getLogger(NumberListTest.class);

    @ParameterizedTest(name = "size [{0}]")
    @MethodSource("sizeData")
    public void testSize(NumberList numberList, int expectedSize) {
        assertEquals(expectedSize, numberList.size());
    }

    static Stream<Arguments> sizeData() {
        return Stream.of(
                Arguments.of(new NumberList("1,1,2,4,5"), 5),
                Arguments.of(new NumberList("-9,-2,-3,3,5,6"), 6),
                Arguments.of(new NumberList("0,2,5,-2,-4,-5,+3"), 7)
        );
    }

    @ParameterizedTest(name = "toString [{0}]")
    @MethodSource("toStringData")
    public void testToString(NumberList numberList, String expectedString) {
        assertEquals(expectedString, numberList.toString());
    }

    static Stream<Arguments> toStringData() {
        return Stream.of(
                Arguments.of(new NumberList("1,1,2,4,5"), "1,1,2,4,5"),
                Arguments.of(new NumberList("-9,-2,-3,3,5,6"), "-9,-2,-3,3,5,6"),
                Arguments.of(new NumberList("0,2,5,-2,-4,-5,+3"), "0,2,5,-2,-4,-5,3"),
                Arguments.of(new NumberList("0,2,5,-2,-4,-5,+3", 0, 5, true), "0,2,5,-2,-4,-5,3")
        );
    }

    @ParameterizedTest(name = "bounds [{0}]")
    @MethodSource("boundsData")
    public void testBounds(NumberList numberList, Integer validNumber, Integer invalidNumber) {
        numberList.add(validNumber);
        try {
            numberList.add(invalidNumber);
        } catch (IllegalArgumentException e) {
            LOG.debug("Caught exception: " + e);
        }
    }

    static Stream<Arguments> boundsData() {
        return Stream.of(
                Arguments.of(new NumberList(0, 1, false), 0, -1),
                Arguments.of(new NumberList("1", 0, 1, true), 0, 2)
        );
    }
}
