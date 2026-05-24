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
package net.fortuna.ical4j.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * $Id$ [17-Jan-2005]
 *
 * Unit test for StringUtils.
 * @author Chris Borrill
 */
public class UrisTest {

    @BeforeEach
    void setUp() {
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_PARSING, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
    }

    @AfterEach
    void tearDown() {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);
    }

    @ParameterizedTest(name = "createUri [{0}]")
    @MethodSource("createUriData")
    public void testCreateUri(String testString, String expectedValue) throws Exception {
        assertEquals(expectedValue, Uris.create(testString).toString(), "create failed");
    }

    static Stream<Arguments> createUriData() {
        return Stream.of(
                Arguments.of("mailto:joe@example.com", "mailto:joe@example.com"),
                Arguments.of("mailto:gaëlle@example.com", "mailto:gaëlle@example.com"),
                Arguments.of("mailto:joe+titi@example.com", "mailto:joe+titi@example.com"),
                Arguments.of("mailto:joe%titi@example.com", "mailto:joe%25titi@example.com"),
                Arguments.of("mailto:jack jill@example.com", "mailto:jack%20jill@example.com"),
                Arguments.of("sms:caluser2@example.com,%20mailto:caluser2@example.com", "sms:caluser2@example.com,%20mailto:caluser2@example.com"),
                Arguments.of("toto", "toto"),
                Arguments.of(":toto", Uris.INVALID_SCHEME + ":" + ":toto"),
                Arguments.of("toto:", Uris.INVALID_SCHEME + ":" + "toto:")
        );
    }
}
