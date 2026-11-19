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
import net.fortuna.ical4j.filter.ComponentFilter;
import net.fortuna.ical4j.filter.FilterExpression;
import net.fortuna.ical4j.filter.FilterTarget;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.validate.ValidationResult;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void registeredEncodablePropertyUsesItsValueType() {
        var builder = new PropertyBuilder(List.of(new CustomProperty.Factory())).name("X-CUSTOM");
        var raw = builder.parameter(Value.URI).value("a\\nb\\,c").build();
        assertEquals(CustomProperty.class, raw.getClass());
        assertEquals("a\\nb\\,c", raw.getValue());
        assertEquals("X-CUSTOM;VALUE=URI:a\\nb\\,c\r\n", raw.toString());

        var text = new PropertyBuilder(List.of(new CustomProperty.Factory())).name("X-CUSTOM")
                .parameter(Value.TEXT).value("a\\nb\\,c").build();
        assertEquals("a\nb,c", text.getValue());
        assertEquals("X-CUSTOM;VALUE=TEXT:a\\nb\\,c\r\n", text.toString());
    }

    @Test
    void nonTextFilterOperandsPreserveLiteralBackslashes() {
        var filter = new ComponentFilter<VEvent>();
        var type = List.of(new FilterTarget.Attribute("VALUE", "URI"));
        var property = new XProperty("X-FOO", new ParameterList(List.of(Value.URI)), "a\\,b");
        var event = new VEvent();
        event.add(property);

        assertTrue(filter.predicate(FilterExpression.equalTo("X-FOO", type, "a\\,b")).test(event));
        assertFalse(filter.predicate(FilterExpression.equalTo("X-FOO", type, "a,b")).test(event));
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
    void nonTextLineBreaksReparseAsLiteralBackslashN() throws Exception {
        var parameters = new ParameterList(List.of(Value.URI));
        String literal = "before\\nAFTER:value";
        String contentLine = "X-FOO;VALUE=URI:" + literal;
        for (String lineBreak : List.of("\n", "\r", "\r\n")) {
            var property = new XProperty("X-FOO", parameters, "before" + lineBreak + "AFTER:value");
            assertEquals(contentLine + "\r\n", property.toString());
            var reparsed = parse(property.toString().stripTrailing()).getRequiredProperty("X-FOO");
            assertEquals(literal, reparsed.getValue());
        }

        var property = new XProperty("X-FOO", parameters, literal);
        assertEquals(contentLine + "\r\n", property.toString());
        assertEquals(literal, parse(contentLine).getRequiredProperty("X-FOO").getValue());
    }

    private static List<String> nonTextValues() {
        return List.of("a\\nb", "a\\\\nb", "a\\,b", "a\\\\,b", "a\\;b", "a,b;c");
    }

    private static Calendar parse(String property) throws Exception {
        return new CalendarBuilder().build(new StringReader("BEGIN:VCALENDAR\r\nVERSION:2.0\r\n"
                + "PRODID:-//X-Property decoding test//EN" + "\r\n" + property + "\r\nEND:VCALENDAR\r\n"));
    }

    private static final class CustomProperty extends Property implements Encodable {

        private String value;

        private CustomProperty(ParameterList parameters, String value) {
            super("X-CUSTOM", parameters);
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public ValidationResult validate() {
            return ValidationResult.EMPTY;
        }

        @Override
        protected PropertyFactory<?> newFactory() {
            return new Factory();
        }

        private static final class Factory implements PropertyFactory<CustomProperty> {

            @Override
            public CustomProperty createProperty() {
                return createProperty(new ParameterList(), "");
            }

            @Override
            public CustomProperty createProperty(ParameterList parameters, String value) {
                return new CustomProperty(parameters, value);
            }

            @Override
            public boolean supports(String name) {
                return "X-CUSTOM".equals(name);
            }
        }
    }

}
