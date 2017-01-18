package net.fortuna.ical4j.model.rfc5545;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import org.junit.Test;

import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.property.Attendee;

public class AttendeePropertyRuleTest {

    private AttendeePropertyRule rule = new AttendeePropertyRule();

    @Test
    public void shouldCorrectlyRemoveApostrophes() throws URISyntaxException {
        Attendee attendee = new Attendee("mailto:'mobile-media-applications@1und1.de'");
        attendee.getParameters().add(new Cn("Mobile Media"));
        this.rule.applyTo(attendee);
        assertEquals("mailto:mobile-media-applications@1und1.de", attendee.getValue());
    }

    @Test
    public void shouldLeaveAttendeeAsItIs() throws URISyntaxException {
        Attendee attendee = new Attendee("mailto:mobile-media-applications@1und1.de");
        attendee.getParameters().add(new Cn("Mobile Media"));
        this.rule.applyTo(attendee);
        assertEquals("mailto:mobile-media-applications@1und1.de", attendee.getValue());
    }

    @Test
    public void shouldNotThrowExceptionIfAttendeeIsNull() throws URISyntaxException {
        this.rule.applyTo(null);
    }

    @Test
    public void shouldNotThrowExceptionIfAttendeeIsEmpty() throws URISyntaxException {
        this.rule.applyTo(new Attendee());
    }

    @Test
    public void shouldNotThrowExceptionIfOneApostrophe() throws URISyntaxException {
        this.rule.applyTo(new Attendee("mailto:'"));
    }

    @Test
    public void shouldNotThrowExceptionIfTwoApostrophes() throws URISyntaxException {
        this.rule.applyTo(new Attendee("mailto:''"));
    }

    @Test
    public void shouldNotDoAnythingIfAnotherScheem() throws URISyntaxException {
        String value = "http://something";
        Attendee attende = new Attendee(value);
        this.rule.applyTo(attende);
        assertEquals(value, attende.getValue());
    }

}
