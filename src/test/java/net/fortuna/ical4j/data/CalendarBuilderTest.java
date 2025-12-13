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
package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.DateTimeException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 * <p/>
 * Test case for iCalendarBuilder.
 *
 * @author benf
 */
public class CalendarBuilderTest {

    private static final Logger log = LoggerFactory.getLogger(CalendarBuilderTest.class);

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public final void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
        CompatibilityHints.setHintEnabled(
                CompatibilityHints.KEY_RELAXED_VALIDATION, true);

        // uncomment for testing invalid calendars in relaxed parsing mode..
//        CompatibilityHints.setHintEnabled(
//                CompatibilityHints.KEY_RELAXED_PARSING, true);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public final void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }

    /**
     * @throws IOException
     * @throws ParserException
     * @throws ValidationException
     */
    @ParameterizedTest
    @MethodSource
    @Disabled
    public void testBuildValid(final String filename) throws IOException, ParserException, ValidationException {
        final FileInputStream fin = new FileInputStream(filename);
        Calendar calendar = new CalendarBuilder().build(fin);
        calendar.validate();
    }

    /**
     * @throws IOException
     * @throws ParserException
     */
    @ParameterizedTest
    @MethodSource
    public void testBuildInvalid(final String filename) throws IOException {
        final FileInputStream fin = new FileInputStream(filename);
        try {
            Calendar calendar = new CalendarBuilder().build(fin);
            ValidationResult result = calendar.validate();
            Assert.assertTrue(result.hasErrors());
        } catch (DateTimeException | ValidationException | ParserException e) {
            log.trace("Caught exception: [" + filename + "," + e.getMessage() + "]");
        }
    }

    private static List<String> testBuildValid() {

        // valid tests..
        return Arrays.stream(Objects.requireNonNull(new File("src/test/resources/samples/valid")
                        .listFiles(f -> !f.isDirectory() && f.getName().endsWith(".ics"))))
                .map(File::getPath).collect(Collectors.toList());
    }

    private static List<String> testBuildInvalid() {
        // invalid tests..
        return Arrays.stream(Objects.requireNonNull(new File("src/test/resources/samples/invalid")
                        .listFiles(f -> !f.isDirectory() && f.getName().endsWith(".ics"))))
                .map(File::getPath).collect(Collectors.toList());
    }
}
