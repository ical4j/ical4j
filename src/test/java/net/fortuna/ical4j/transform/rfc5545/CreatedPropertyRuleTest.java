package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.property.Created;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class CreatedPropertyRuleTest {

    @Test
    public void shouldSetUtcToBrokenCreatedDate() throws ParseException {
        Created created = new Created("20161026T130842");
        RuleManager.applyTo(created);
        assertEquals("20161026T130842Z", created.getValue());
    }
}
