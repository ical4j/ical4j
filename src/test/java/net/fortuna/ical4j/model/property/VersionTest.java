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
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created on 16/03/2005
 *
 * $Id$
 *
 * @author Ben
 *
 * Tests related to the property VERSION
 */
public class VersionTest {

    @ParameterizedTest(name = "getValue")
    @MethodSource("getValueData")
    public void testGetValue(Property property, String expectedValue) {
        PropertyTest.assertGetValue(property, expectedValue);
    }

    /**
     * Test that the constant VERSION_2_0 is immutable.
     */
    @ParameterizedTest(name = "immutable")
    @MethodSource("immutableData")
    public void testImmutable(Version version) throws IOException, URISyntaxException {
        PropertyTest.assertImmutable(version);

        try {
            version.setMinVersion("3.0");
            fail("UnsupportedOperationException should be thrown");
        } catch (UnsupportedOperationException uoe) {
        }

        try {
            version.setMaxVersion("5.0");
            fail("UnsupportedOperationException should be thrown");
        } catch (UnsupportedOperationException uoe) {
        }
    }

    @ParameterizedTest(name = "validation")
    @MethodSource("validationData")
    public void testValidation(Property property) throws ValidationException {
        PropertyTest.assertValidation(property);
    }

    @ParameterizedTest(name = "equals")
    @MethodSource("equalsData")
    public void testEquals(Property property) {
        PropertyTest.assertPropertyEquals(property);
    }

    static Stream<Arguments> getValueData() {
        return Stream.of(
                Arguments.of(VERSION_2_0, "2.0")
        );
    }

    static Stream<Arguments> immutableData() {
        return Stream.of(
                Arguments.of(VERSION_2_0)
        );
    }

    static Stream<Arguments> validationData() {
        return Stream.of(
                Arguments.of(VERSION_2_0)
        );
    }

    static Stream<Arguments> equalsData() {
        return Stream.of(
                Arguments.of(VERSION_2_0)
        );
    }
}
