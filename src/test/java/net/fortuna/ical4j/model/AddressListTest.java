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

import net.fortuna.ical4j.util.CompatibilityHints;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * $Id$
 *
 * Created on 16/11/2005
 *
 */
public class AddressListTest {

    private static final Logger LOG = LoggerFactory.getLogger(AddressListTest.class);

    /**
     * Assert three addresses parsed from value.
     * @throws URISyntaxException
     */
    @ParameterizedTest(name = "size [{0}]")
    @MethodSource("sizeData")
    public void testSize(String value, int expectedSize, String[] compatibilityHints) throws URISyntaxException {
        try {
            for (int i = 0; i < compatibilityHints.length; i++) {
                CompatibilityHints.setHintEnabled(compatibilityHints[i], true);
            }
            AddressList addresses = new AddressList(value);
            assertEquals(expectedSize, addresses.getAddresses().size());
        } finally {
            for (int i = 0; i < compatibilityHints.length; i++) {
                CompatibilityHints.clearHintEnabled(compatibilityHints[i]);
            }
        }
    }

    static Stream<Arguments> sizeData() {
        String value1 = "\"address1@example.com\",\"address2@example.com\",\"address3@example.com\"";
        String value2 = "address1@example.com,<address2@example.com>,address3@example.com";
        return Stream.of(
                Arguments.of(value1, 3, new String[] {}),
                Arguments.of(value2, 2, new String[] {CompatibilityHints.KEY_RELAXED_PARSING})
        );
    }

    /**
     * Assert toString() produces identical address list string value.
     * @throws URISyntaxException
     */
    @ParameterizedTest(name = "toString [{0}]")
    @MethodSource("toStringData")
    public void testToString(String value, String[] compatibilityHints) throws URISyntaxException {
        try {
            for (int i = 0; i < compatibilityHints.length; i++) {
                CompatibilityHints.setHintEnabled(compatibilityHints[i], true);
            }
            AddressList addresses = new AddressList(value);
            assertEquals(value, addresses.toString());
        } finally {
            for (int i = 0; i < compatibilityHints.length; i++) {
                CompatibilityHints.clearHintEnabled(compatibilityHints[i]);
            }
        }
    }

    static Stream<Arguments> toStringData() {
        String value = "\"address1@example.com\",\"address2@example.com\",\"address3@example.com\"";
        return Stream.of(
                Arguments.of(value, new String[] {})
        );
    }

    /**
     * Test invalid addresses are correctly handled.
     */
    @ParameterizedTest(name = "invalidAddressList [{0}]")
    @MethodSource("invalidAddressListData")
    public void testInvalidAddressList(String value, String[] compatibilityHints) throws URISyntaxException {
        try {
            for (int i = 0; i < compatibilityHints.length; i++) {
                CompatibilityHints.setHintEnabled(compatibilityHints[i], true);
            }
            try {
                new AddressList(value);
                fail("Should throw URISyntaxException");
            } catch (URISyntaxException use) {
                LOG.info("Caught exception: " + use.getMessage());
            }
        } finally {
            for (int i = 0; i < compatibilityHints.length; i++) {
                CompatibilityHints.clearHintEnabled(compatibilityHints[i]);
            }
        }
    }

    static Stream<Arguments> invalidAddressListData() {
        String value = "address1@example.com,<address2@example.com>,address3@example.com";
        return Stream.of(
                Arguments.of(value, new String[] {})
        );
    }
}
