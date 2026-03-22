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
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
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

    @Test
    void deserialization_of_ical4j_4_2_4_instance_with_URI_value() throws DecoderException, IOException, ClassNotFoundException {
        var data = Hex.decodeHex("aced0005737200286e65742e666f7274756e612e6963616c346a2e6d6f64656c2e70726f70657274792" +
                "e4174746163683d9dde8dae7d5cdc0200024c000662696e6172797400154c6a6176612f6e696f2f427974654275666665723" +
                "b4c000375726974000e4c6a6176612f6e65742f5552493b787200216e65742e666f7274756e612e6963616c346a2e6d6f646" +
                "56c2e50726f706572747961d2511e8c75386f0200034c00046e616d657400124c6a6176612f6c616e672f537472696e673b4" +
                "c000a706172616d65746572737400284c6e65742f666f7274756e612f6963616c346a2f6d6f64656c2f506172616d6574657" +
                "24c6973743b4c000670726566697871007e0004787200206e65742e666f7274756e612e6963616c346a2e6d6f64656c2e436" +
                "f6e74656e74d7db15711101f14b0200007870740006415454414348737200266e65742e666f7274756e612e6963616c346a2" +
                "e6d6f64656c2e506172616d657465724c6973744ba894acc4d4e22d0200014c000a706172616d65746572737400104c6a617" +
                "6612f7574696c2f4c6973743b7870737200266a6176612e7574696c2e436f6c6c656374696f6e7324556e6d6f64696669616" +
                "26c654c697374fc0f2531b5ec8e100200014c00046c69737471007e000a7872002c6a6176612e7574696c2e436f6c6c65637" +
                "4696f6e7324556e6d6f6469666961626c65436f6c6c656374696f6e19420080cb5ef71e0200014c0001637400164c6a61766" +
                "12f7574696c2f436f6c6c656374696f6e3b78707372001f6a6176612e7574696c2e436f6c6c656374696f6e7324456d70747" +
                "94c6973747ab817b43ca79ede020000787071007e001170707372000c6a6176612e6e65742e555249ac01782e439e49ab030" +
                "0014c0006737472696e6771007e0004787074001f68747470733a2f2f646f6d61696e2e6578616d706c652f66696c652e747" +
                "87478");

        var in = new ObjectInputStream(new ByteArrayInputStream(data));
        Attach deserialized = (Attach) in.readObject();

        assertEquals("https://domain.example/file.txt", deserialized.getValue());
    }
}
