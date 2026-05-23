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

import junit.framework.AssertionFailedError;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 *
 * Test case for CalendarBuilder.
 *
 * @author benf
 */
public class CalendarEqualsTest {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @BeforeEach
    public final void setUp() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @AfterEach
    public final void tearDown() throws Exception {
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY);
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
    }

    /**
     *
     * @param filename
     * @throws Exception
     */
    @ParameterizedTest
    @MethodSource
    @Disabled
    public void testCalendarEquals(String filename) throws Exception {
        
        FileInputStream fin = new FileInputStream(filename);
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = null;

        boolean errorOccurred = false;
        Exception exception = null;

        try {
            calendar = builder.build(fin);
        } catch (IOException | ParserException e) {
            exception = e;
            errorOccurred = true;
        }

        if (errorOccurred) {
            throw new AssertionFailedError("Calendar file " + filename + " isn't valid:\n" + exception.getMessage());
        }

        if (calendar != null) {
            try {
                calendar.validate();
            } catch (ValidationException e) {
                exception = e;
                errorOccurred = true;
            }

            if (errorOccurred) {
                throw new AssertionFailedError("Calendar file " + filename + " isn't valid:\n" + exception.getMessage());
            }

            fin = new FileInputStream(filename);
            Calendar reparsedCalendar = null;
            builder = new CalendarBuilder();

            try {
                reparsedCalendar = builder.build(fin);
            } catch (IOException | ParserException e) {
                exception = e;
                errorOccurred = true;
            }

            assertEquals(calendar, reparsedCalendar, "Parsed calendar isn't equal to itself!  : " + filename);
        }
    }


    private static List<String> testCalendarEquals() {

        // valid tests..
        return Arrays.stream(Objects.requireNonNull(new File("src/test/resources/samples/valid")
                        .listFiles(f -> !f.isDirectory() && f.getName().endsWith(".ics"))))
                .map(File::getPath).collect(Collectors.toList());
    }
}
