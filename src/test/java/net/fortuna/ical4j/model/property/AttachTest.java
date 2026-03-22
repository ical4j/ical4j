/*
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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for Attach property.
 */
public class AttachTest {

    private Attach attach;

    @BeforeEach
    void setUp() throws Exception {
        var data = Files.readAllBytes(Paths.get("etc/artwork/logo.png"));
        attach = new Attach(new ParameterList(List.of(Encoding.BASE64, Value.BINARY)), ByteBuffer.wrap(data));
    }

    @Test
    void testAttachParameterListString() throws IOException, ValidationException, ParserException, ConstraintViolationException {
        var start = new DtStart<>(LocalDate.now().withMonth(12).withDayOfMonth(25));
        var summary = new Summary("Christmas Day; \n this is a, test\\");

        var christmas = (VEvent) new VEvent()
                .withProperty(start)
                .withProperty(summary)
                .withProperty(attach)
                .withProperty(new Uid("000001@modularity.net.au"))
                .getFluentTarget();

        var calendar = new Calendar()
                .withDefaults()
                .withProdId("-//Ben Fortuna//iCal4j 1.0//EN")
                .withComponent(christmas)
                .getFluentTarget();

        var stringWriter = new StringWriter();
        new CalendarOutputter().output(calendar, stringWriter);
        var calendarString = stringWriter.toString();

        var calendarBuilder = new CalendarBuilder();
        var parsedCalendar = calendarBuilder.build(new StringReader(calendarString));
        var parsedEvent = parsedCalendar.<VEvent>getComponent(Component.VEVENT).orElseThrow();
        Attach parsedAttach = parsedEvent.getRequiredProperty(Property.ATTACH);

        assertNotNull(parsedAttach);
        assertEquals(attach, parsedAttach);
    }

    @Test
    void testSerialization() throws IOException, ClassNotFoundException {
        var bout = new ByteArrayOutputStream();
        var out = new ObjectOutputStream(bout);

        out.writeObject(attach);

        var in = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
        Attach deserialized = (Attach) in.readObject();

        assertNotNull(deserialized);
        assertEquals(attach, deserialized);
    }
}
