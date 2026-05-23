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

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * $Id$
 * <p/>
 * Created on 10/11/2006
 * <p/>
 * Unit tests for {@link Calendars}.
 *
 * @author Ben Fortuna
 */
public class CalendarsTest {

    private static final Logger LOG = LoggerFactory.getLogger(CalendarsTest.class);

    @ParameterizedTest(name = "load [{0}]")
    @MethodSource("loadData")
    public void testLoad(String path) throws IOException, ParserException {
        URL resource = getClass().getResource(path);
        assertNotNull(Calendars.load(resource));
    }

    static Stream<Arguments> loadData() {
        return Stream.of(
                Arguments.of("/samples/valid/Australian32Holidays.ics"),
                Arguments.of("/samples/valid/google_aus_holidays.ics")
        );
    }

    @ParameterizedTest(name = "loadFileNotFoundException [{0}]")
    @MethodSource("loadFileNotFoundExceptionData")
    public void testLoadFileNotFoundException(String path) throws IOException, ParserException {
        try {
            Calendars.load(path);
            fail("Should throw FileNotFoundException");
        } catch (NoSuchFileException fnfe) {
            LOG.info("Caught exception: " + fnfe.getMessage());
        }
    }

    static Stream<Arguments> loadFileNotFoundExceptionData() {
        return Stream.of(
                Arguments.of("/samples/valid/doesnt-exist.ics")
        );
    }

    @ParameterizedTest(name = "merge")
    @MethodSource("mergeData")
    public void testMerge(Calendar[] calendars) throws IOException, ParserException {
        Calendar result = calendars[0];
        for (int i = 1; i < calendars.length; i++) {
            result = Calendars.merge(result, calendars[i]);
        }

        for (Calendar value : calendars) {
            for (Property p : value.getProperties()) {
                assertTrue(result.getProperties().contains(p),
                        "Property [" + p + "] not found in merged calendar");
            }
            for (CalendarComponent c : value.getComponents()) {
                assertTrue(result.getComponents().contains(c),
                        "Component [" + c + "] not found in merged calendar");
            }
        }
    }

    static Stream<Arguments> mergeData() throws IOException, ParserException {
        List<Calendar> calendars = new ArrayList<>();
        calendars.add(Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/valid/Australian32Holidays.ics"))));
        calendars.add(Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/invalid/OZMovies.ics"))));
        return Stream.of(
                Arguments.of((Object) calendars.toArray(Calendar[]::new))
        );
    }

    @ParameterizedTest(name = "split [expectedCount={1}]")
    @MethodSource("splitData")
    public void testSplit(Calendar calendar, int expectedCount) throws IOException, ParserException {
        Calendar[] split = Calendars.split(calendar);
        assertEquals(expectedCount, split.length);
    }

    static Stream<Arguments> splitData() throws IOException, ParserException {
        Calendar calendar = Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/valid/Australian32Holidays.ics")));
        return Stream.of(
                Arguments.of(calendar, 10)
        );
    }

    @ParameterizedTest(name = "getContentType [charset={1}]")
    @MethodSource("getContentTypeData")
    public void testGetContentType(Calendar calendar, Charset charset, String expectedContentType) {
        assertEquals(expectedContentType, Calendars.getContentType(calendar, charset));
    }

    static Stream<Arguments> getContentTypeData() throws IOException, ParserException {
        Calendar aus = Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/valid/Australian32Holidays.ics")));
        Calendar oz = Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/invalid/OZMovies.ics")));
        return Stream.of(
                Arguments.of(aus, null, "text/calendar; method=PUBLISH"),
                Arguments.of(oz, null, "text/calendar; method=PUBLISH"),
                Arguments.of(oz, StandardCharsets.US_ASCII, "text/calendar; method=PUBLISH; charset=US-ASCII")
        );
    }

    /**
     * Ensures that after de-serialization a calendar can still be copied.
     */
    @ParameterizedTest(name = "serializeDeserialize")
    @MethodSource("shouldSerializeDeserializeCorrectlyData")
    public void testShouldSerializeDeserializeCorrectly(Calendar calendar)
            throws IOException, ClassNotFoundException, ParseException, URISyntaxException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(calendar);
        oos.flush();

        assertNotNull(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Calendar copy = (Calendar) ois.readObject();
        assertNotNull(copy);
        assertEquals(copy, calendar);
        Calendar newCopy = new Calendar(copy);
        assertEquals(newCopy, copy);
    }

    static Stream<Arguments> shouldSerializeDeserializeCorrectlyData() throws IOException, ParserException {
        Calendar calendar = Calendars.load(Objects.requireNonNull(CalendarsTest.class.getResource("/samples/valid/Australian32Holidays.ics")));
        return Stream.of(
                Arguments.of(calendar)
        );
    }
}
