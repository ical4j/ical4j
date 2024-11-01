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

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * $Id$
 * <p/>
 * Created on 10/02/2007
 * <p/>
 * Unit tests for {@link Organizer} property.
 *
 * @author Ben Fortuna
 */
public class OrganizerTest extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizerTest.class);

    @Override
    protected void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_PARSING, false);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
    }

    /**
     * Test handling of invalid address values.
     */
    public void testInvalidAddress() throws URISyntaxException {
        String value = "MAILTO:";

        try {
            new Organizer(value);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            LOG.info("Caught exception: " + e.getMessage());
        }

        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_PARSING, true);

        Organizer organizer = new Organizer(value);
        assertEquals(new URI(Uris.INVALID_SCHEME, value, null), organizer.getCalAddress());
        assertEquals(Property.ORGANIZER + ":" + Uris.INVALID_SCHEME + ":" + value + Strings.LINE_SEPARATOR,
                organizer.toString());
    }
}
