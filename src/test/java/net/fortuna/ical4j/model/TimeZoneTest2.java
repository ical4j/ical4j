package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.Observance;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeZoneTest2 {

    /* ical4j 3.0.21, but seems to also happen with 4.0.0-alpha8 (couldn't test everything) */

    @Test
    public void testTzKarachi() throws Exception {
        TimeZoneRegistry tzReg = TimeZoneRegistryFactory.getInstance().createRegistry();

        TimeZone origKarachi = tzReg.getTimeZone("Asia/Karachi");
        TimeZone karachi = origKarachi;

        long ts1 = 1609945200000L;       // 20210106T150000Z; 20210106T200000 Asia/Karachi
        DateTime dt1 = new DateTime(ts1);
        dt1.setUtc(true);

        System.err.println("Applicable observance: " + karachi.getVTimeZone().getApplicableObservance(dt1));
        /* prints:

           BEGIN:DAYLIGHT
           TZNAME:PKST
           TZOFFSETFROM:+0500
           TZOFFSETTO:+0600
           DTSTART:20020407T000000
           RDATE:20080601T000000
           RDATE:20090415T000000
           END:DAYLIGHT

           which is wrong. Should be this one:

           BEGIN:STANDARD
           TZNAME:PKT
           TZOFFSETFROM:+0600
           TZOFFSETTO:+0500
           DTSTART:20081101T000000
           RRULE:FREQ=YEARLY;UNTIL=20091031T180000Z
           END:STANDARD
        */

        for (Observance obs : karachi.getVTimeZone().getObservances()) {
            System.err.print("Found observance: " + obs.toString());
            System.err.println("Latest onset: " + obs.getLatestOnset(dt1));
            System.err.println();
        }
        /* prints:

        Found observance: BEGIN:STANDARD
        TZNAME:+0530
        TZOFFSETFROM:+042812
        TZOFFSETTO:+0530
        DTSTART:19070101T000000
        END:STANDARD
        Latest onset: 19061231T193148Z

        Found observance: BEGIN:DAYLIGHT
        TZNAME:+0630
        TZOFFSETFROM:+0530
        TZOFFSETTO:+0630
        DTSTART:19420901T000000
        END:DAYLIGHT
        Latest onset: 19420831T183000Z

        Found observance: BEGIN:STANDARD
        TZNAME:+0530
        TZOFFSETFROM:+0630
        TZOFFSETTO:+0530
        DTSTART:19451015T000000
        END:STANDARD
        Latest onset: 19451014T173000Z

        Found observance: BEGIN:STANDARD
        TZNAME:+05
        TZOFFSETFROM:+0530
        TZOFFSETTO:+0500
        DTSTART:19510930T000000
        END:STANDARD
        Latest onset: 19510929T183000Z

        Found observance: BEGIN:STANDARD
        TZNAME:PKT
        TZOFFSETFROM:+0500
        TZOFFSETTO:+0500
        DTSTART:19710326T000000
        END:STANDARD
        Latest onset: 19710325T190000Z

        Found observance: BEGIN:DAYLIGHT
        TZNAME:PKST
        TZOFFSETFROM:+0500
        TZOFFSETTO:+0600               // <- this is what is used for 2021
        DTSTART:20020407T000000
        RDATE:20080601T000000
        RDATE:20090415T000000        // <-- newer than 20081031T180000Z so this one is used
        END:DAYLIGHT
        Latest onset: 20090414T190000Z

        Found observance: BEGIN:STANDARD
        TZNAME:PKT
        TZOFFSETFROM:+0600
        TZOFFSETTO:+0500
        DTSTART:20021006T000000
        END:STANDARD
        Latest onset: 20021005T180000Z

        Found observance: BEGIN:STANDARD
        TZNAME:PKT
        TZOFFSETFROM:+0600
        TZOFFSETTO:+0500                         // <- this is what should be used for 2021
        DTSTART:20081101T000000
        RRULE:FREQ=YEARLY;UNTIL=20091031T180000Z
        END:STANDARD
        Latest onset: 20081031T180000Z                // <- this should be 20091031T180000Z
         */

        assertEquals(5, karachi.getOffset(ts1)/3600000);  // fails!

        DateTime dt2 = new DateTime("20210106T200000", karachi);
        assertEquals(1609945200000L, dt2.getTime());    // fails!
    }

}