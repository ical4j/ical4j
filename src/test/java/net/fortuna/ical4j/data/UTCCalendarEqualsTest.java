package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import junit.framework.TestCase;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.transform.recurrence.Frequency;
import net.fortuna.ical4j.util.RandomUidGenerator;

public class UTCCalendarEqualsTest extends TestCase {

    public void testGetValidCalendarUTC() throws Exception {
        Calendar calendar = new Calendar();
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        VTimeZone tz = registry.getTimeZone("UTC").getVTimeZone();
        calendar.add(tz);
        calendar.add(new ProdId("Calendar " + OffsetDateTime.now()));
        calendar.add(ImmutableVersion.VERSION_2_0);
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime eventStart = ZonedDateTime.now(zoneId);
        ZonedDateTime eventEnd = eventStart.plusMinutes(5);
        VEvent event = new VEvent(eventStart, eventEnd, "TEST")
            .add(new RandomUidGenerator().generateUid())
            .add(new Transp(Transp.VALUE_OPAQUE));
        Recur recurence = new Recur.Builder()
            .frequency(Frequency.MINUTELY)
            .interval(1)
            .until(eventEnd.plusDays(1))
            .build();
        event.add(new RRule<>(recurence));
        calendar.add(event);
        String s = convertToDatabaseColumn(calendar);
        Calendar c2 = convertToEntityAttribute(s);
        //when
        boolean result = calendar.equals(c2);
        //then no exceptions
        assertTrue(result);
    }
    
    public void testGetValidCalendarEtcUTC() throws Exception {
        Calendar calendar = new Calendar();
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        VTimeZone tz = registry.getTimeZone("Etc/UTC").getVTimeZone();
        calendar.add(tz);
        calendar.add(new ProdId("Calendar " + OffsetDateTime.now()));
        calendar.add(ImmutableVersion.VERSION_2_0);
        ZoneId zoneId = ZoneId.of("Etc/UTC");
        ZonedDateTime eventStart = ZonedDateTime.now(zoneId);
        ZonedDateTime eventEnd = eventStart.plusMinutes(5);
        VEvent event = new VEvent(eventStart, eventEnd, "TEST")
            .add(new RandomUidGenerator().generateUid())
            .add(new Transp(Transp.VALUE_OPAQUE));
        Recur recurence = new Recur.Builder()
            .frequency(Frequency.MINUTELY)
            .interval(1)
            .until(eventEnd.plusDays(1))
            .build();
        event.add(new RRule<>(recurence));
        calendar.add(event);
        String s = convertToDatabaseColumn(calendar);
        Calendar c2 = convertToEntityAttribute(s);
        //when
        boolean result = calendar.equals(c2);
        //then no exceptions
        assertTrue(result);
    }

    public String convertToDatabaseColumn(Calendar aCalendar) throws IOException {
        try (Writer out = new StringWriter()) {
            new CalendarOutputter(false, Integer.MAX_VALUE).output(aCalendar, out);
            return out.toString();
        } catch (IOException ex) {
            throw ex;
        }
    }

    public Calendar convertToEntityAttribute(String aDbData) throws IOException, ParserException {
        try (StringReader reader = new StringReader(aDbData)) {
            return new CalendarBuilder().build(reader);
        } catch (IOException | ParserException ex) {
            throw ex;
        }
    }
}
