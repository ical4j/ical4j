package net.fortuna.ical4j.model.rfc5545;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import net.fortuna.ical4j.model.property.Created;

public class CreatedPropertyRuleTest {

    @Test
    public void shouldSetUtcToBrokenCreatedDate() throws ParseException {
        Created created = new Created("20161026T130842");
        RuleManager.applyTo(created);
        assertEquals("20161026T130842Z", created.getValue());
    }
}
