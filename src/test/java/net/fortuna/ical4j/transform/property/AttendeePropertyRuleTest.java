package net.fortuna.ical4j.transform.property;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.transform.RuleManager;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class AttendeePropertyRuleTest {

    private ParameterList params;


    @Before
    public void setup() {
        params = new ParameterList(Collections.singletonList(new Cn("Mobile Media")));
    }

    @Test
    public void shouldCorrectlyRemoveApostrophes() throws URISyntaxException {
        Attendee attendee = new Attendee(params, "mailto:'mobile-media-applications@1und1.de'");
        RuleManager.applyTo(attendee);
        assertEquals("mailto:mobile-media-applications@1und1.de", attendee.getValue());
    }

    @Test
    public void shouldLeaveAttendeeAsItIs() throws URISyntaxException {
        Attendee attendee = new Attendee(params, "mailto:mobile-media-applications@1und1.de");
        RuleManager.applyTo(attendee);
        assertEquals("mailto:mobile-media-applications@1und1.de", attendee.getValue());
    }

    @Test
    public void shouldNotThrowExceptionIfAttendeeIsEmpty() throws URISyntaxException {
        RuleManager.applyTo(new Attendee());
    }

    @Test
    public void shouldNotThrowExceptionIfOneApostrophe() throws URISyntaxException {
        RuleManager.applyTo(new Attendee("mailto:'"));
    }

    @Test
    public void shouldNotThrowExceptionIfTwoApostrophes() throws URISyntaxException {
        RuleManager.applyTo(new Attendee("mailto:''"));
    }

    @Test
    public void shouldNotDoAnythingIfAnotherScheem() throws URISyntaxException {
        String value = "http://something";
        Attendee attende = new Attendee(value);
        RuleManager.applyTo(attende);
        assertEquals(value, attende.getValue());
    }

}
