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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.XProperty;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TEXT decoding must not alter X-properties with an explicit non-TEXT value type.
 */
class XPropertyValueDecodingTest {

    @Test
    void explicitUriBuilderPreservesBackslashN() {
        String value = "file:C:\\new\\dir";
        Property property = new PropertyBuilder().name("X-FOO").parameter(Value.URI).value(value).build();
        assertEquals(value, property.getValue());
    }

    @Test
    void nonTextCalendarInputPreservesBackslashEscapes() throws Exception {
        for (String value : nonTextValues()) {
            Calendar calendar = parse("X-FOO;VALUE=URI:" + value);
            assertEquals(value, calendar.getRequiredProperty("X-FOO").getValue(), value);
        }
    }

    @Test
    void nonTextCalendarRoundTripsRemainStable() throws Exception {
        for (String value : nonTextValues()) {
            Calendar calendar = parse("X-FOO;VALUE=URI:" + value);
            for (int round = 0; round < 3; round++) {
                calendar = new CalendarBuilder().build(new StringReader(calendar.toString()));
                assertEquals(value, calendar.getRequiredProperty("X-FOO").getValue(), value);
            }
        }
    }

    @Test
    void customValueTypePreservesBackslashes() throws Exception {
        String value = "a\\nb\\,c";
        Calendar calendar = parse("X-FOO;VALUE=X-RAW:" + value);
        assertEquals(value, calendar.getRequiredProperty("X-FOO").getValue());
        assertEquals("X-RAW", calendar.getRequiredProperty("X-FOO")
                .getRequiredParameter(Parameter.VALUE).getValue());
    }

    @Test
    void explicitTextStillDecodes() throws Exception {
        assertEquals("a\nb,c;d\\e", parse("X-FOO;VALUE=TEXT:" + "a\\nb\\,c\\;d\\\\e")
                .getRequiredProperty("X-FOO").getValue());
    }

    @Test
    void defaultTextStillDecodes() throws Exception {
        assertEquals("a\nb,c;d\\e", parse("X-FOO:" + "a\\nb\\,c\\;d\\\\e")
                .getRequiredProperty("X-FOO").getValue());
    }

    @Test
    void standardTextPropertyStillDecodes() throws Exception {
        assertEquals("a\nb,c;d\\e", parse("SUMMARY:" + "a\\nb\\,c\\;d\\\\e")
                .getRequiredProperty(Property.SUMMARY).getValue());
    }

    @Test
    void nonTextLineBreakOutputGuardIsPreserved() {
        XProperty property = new XProperty("X-FOO", new ParameterList(List.of(Value.URI)), "before\nAFTER:value");
        assertEquals("X-FOO;VALUE=URI:before\\nAFTER:value\r\n", property.toString());
    }

    private static List<String> nonTextValues() {
        return List.of("a\\nb", "a\\\\nb", "a\\,b", "a\\\\,b", "a\\;b", "a,b;c");
    }

    private static Calendar parse(String property) throws Exception {
        return new CalendarBuilder().build(new StringReader("BEGIN:VCALENDAR\r\nVERSION:2.0\r\n"
                + "PRODID:-//X-Property decoding test//EN" + "\r\n" + property + "\r\nEND:VCALENDAR\r\n"));
    }
}
