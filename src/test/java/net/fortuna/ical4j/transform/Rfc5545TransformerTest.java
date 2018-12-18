package net.fortuna.ical4j.transform;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.validate.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Rfc5545TransformerTest {

    private Rfc5545Transformer transformer;

    @Before
    public void setUp() {
        transformer = new Rfc5545Transformer();
    }

    @Test
    public void shouldCorrectCalendarBody() throws IOException, ParserException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        String[] calendarNames = { "yahoo1.txt", "yahoo2.txt", "outlook1.txt", "outlook2.txt", "apple.txt" };
        for (String calendarName : calendarNames) {
            Calendar calendar = buildCalendar(calendarName);
            calendar = transformer.transform(calendar);
            try {
                calendar.validate();
            } catch (ValidationException e) {
                e.printStackTrace();
                fail("Validation failed for " + calendarName);
            }
        }
    }

    @Test
    public void shouldCorrectMsSpecificTimeZones() throws IOException, ParserException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        String actuals[] = { "timezones/outlook1.txt", "timezones/outlook2.txt" };
        String expecteds[] = { "timezones/outlook1_expected.txt", "timezones/outlook2_expected.txt" };

        for (int i = 0; i < actuals.length; i++) {
            Calendar actual = buildCalendar(actuals[i]);
            actual = transformer.transform(actual);
            Calendar expected = buildCalendar(expecteds[i]);
            assertEquals("on from " + expecteds[i] + " and " + actuals[i] + " failed.", expected, actual);
        }
    }

    @Test
    public void shouldCorrectDTStampByAddingUTCTimezone() {
        String calendarName = "dtstamp/invalid.txt";
        try {
            Calendar actual = buildCalendar(calendarName);
            actual = transformer.transform(actual);
        } catch (RuntimeException | IOException | ParserException e) {
            e.printStackTrace();
            fail("RFC transformation failed for " + calendarName);
        }
    }

    @Test
    public void shouldSetTimezoneToUtcForNoTZdescription() {
        String actualCalendar = "outlook/TZ-no-description.txt";
        try {
            Calendar actual = buildCalendar(actualCalendar);
            actual = transformer.transform(actual);
            Calendar expected = buildCalendar("outlook/TZ-set-to-utc.txt");
            assertEquals(expected.toString(), actual.toString());
            assertEquals(expected, actual);
        } catch (Exception e) {
            e.printStackTrace();
            fail("RFC transformation failed for " + actualCalendar);
        }
    }

    private Calendar buildCalendar(String file) throws IOException, ParserException {
        InputStream is = getClass().getResourceAsStream(file);
        CalendarBuilder cb = new CalendarBuilder();
        Calendar calendar = cb.build(is);
        is.close();
        return calendar;
    }
}