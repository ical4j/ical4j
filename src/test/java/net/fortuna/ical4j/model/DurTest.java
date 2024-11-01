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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

/**
 * $Id$
 *
 * Created on 20/06/2005
 *
 * @author Ben
 *
 */
public class DurTest extends TestCase {

    private final Dur duration;
    
    private Dur duration2;
    
    private String expectedString;
    
    private Date startTime;
    
    private Date expectedTime;
    
    private TimeZone originalDefault;
    
    /**
     * @param duration
     * @param expectedString
     */
    public DurTest(Dur duration, String expectedString) {
        super("testToString");
        this.duration = duration;
        this.expectedString = expectedString;
    }

    /**
     * @param duration
     * @param startTime
     * @param expectedTime
     */
    public DurTest(Dur duration, Date startTime, Date expectedTime) {
        super("testGetTime");
        this.duration = duration;
        this.startTime = startTime;
        this.expectedTime = expectedTime;
    }
    
    /**
     * @param duration
     * @param duration2
     */
    public DurTest(String testMethod, Dur duration, Dur duration2) {
        super(testMethod);
        this.duration = duration;
        this.duration2 = duration2;
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        originalDefault = TimeZone.getDefault();
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        TimeZone.setDefault(originalDefault);
    }
    
    /**
     * 
     */
    public void testToString() {
        assertEquals(expectedString, duration.toString());
    }
    
    /**
     * 
     */
    public void testGetTime() {
        assertEquals(expectedTime, duration.getTime(startTime));
    }
    
    /**
     * 
     */
    public void testEquals() {
        assertEquals(duration2, duration);
    }
    
    /**
     * 
     */
    public void testCompareToGreater() {
        assertTrue(duration.compareTo(duration2) > 0);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#getName()
     */
    @Override
    public String getName() {
        return super.getName() + " [" + duration.toString() + "]";
    }
    
    /**
     * @return
     * @throws ParseException 
     */
    public static TestSuite suite() throws ParseException {
        TestSuite suite = new TestSuite();
        TimeZoneRegistry tzreg = new DefaultTimeZoneRegistryFactory().createRegistry();
        suite.addTest(new DurTest(new Dur("PT15M"), "PT15M"));
        
        Calendar cal = Calendar.getInstance();
        Date startTime = cal.getTime();
        cal.add(Calendar.MINUTE, 15);
        suite.addTest(new DurTest(new Dur("PT15M"), startTime, cal.getTime()));

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2011);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 26);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startTime2 = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 27);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        suite.addTest(new DurTest(new Dur("P1D"), startTime2, cal.getTime()));

        // tests around various Daylight Saving Times
        // EST change on 20110327T020000
        suite.addTest(new DurTest(new Dur("P1D"),
                new DateTime("20110326T200000", tzreg.getTimeZone("Europe/Paris")),
                new DateTime("20110327T200000", tzreg.getTimeZone("Europe/Paris"))));
        suite.addTest(new DurTest(new Dur("P1D"),
                new DateTime("20110326T110000", tzreg.getTimeZone("America/Los_Angeles")),
                new DateTime("20110327T110000", tzreg.getTimeZone("America/Los_Angeles"))));
        // PST change on 20110313T020000
        suite.addTest(new DurTest(new Dur("P1D"),
                new DateTime("20110312T200000", tzreg.getTimeZone("America/Los_Angeles")),
                new DateTime("20110313T200000", tzreg.getTimeZone("America/Los_Angeles"))));
        suite.addTest(new DurTest(new Dur("P1D"),
                new DateTime("20110312T200000", tzreg.getTimeZone("Europe/Paris")),
                new DateTime("20110313T200000", tzreg.getTimeZone("Europe/Paris"))));

        suite.addTest(new DurTest(new Dur(33), "P33W"));
        
        cal = Calendar.getInstance();
        cal.set(2005, 7, 1);
        Date start = cal.getTime();
        
        cal.add(Calendar.YEAR, 1);
        suite.addTest(new DurTest(new Dur(start, cal.getTime()), "P365D"));
        
        cal.setTime(start);
        cal.add(Calendar.WEEK_OF_YEAR, -5);
        suite.addTest(new DurTest(new Dur(start, cal.getTime()), "-P5W"));
        
        cal.setTime(start);
        cal.add(Calendar.DAY_OF_WEEK, 11);
        suite.addTest(new DurTest(new Dur(start, cal.getTime()), "P11D"));
        
        cal.setTime(start);
        cal.add(Calendar.HOUR_OF_DAY, 25);
        suite.addTest(new DurTest(new Dur(start, cal.getTime()), "P1DT1H"));
        
        cal.setTime(start);
        cal.add(Calendar.MINUTE, -23);
        suite.addTest(new DurTest(new Dur(start, cal.getTime()), "-PT23M"));
        
        cal.setTime(start);
        cal.add(Calendar.SECOND, -5);
        suite.addTest(new DurTest(new Dur(start, cal.getTime()), "-PT5S"));
        
        cal.setTime(start);
        cal.add(Calendar.HOUR_OF_DAY, 25);
        cal.add(Calendar.MINUTE, -23);
        cal.add(Calendar.SECOND, -5);
        suite.addTest(new DurTest(new Dur(start, cal.getTime()), "P1DT36M55S"));

        cal.setTime(start);
        cal.add(Calendar.YEAR, -2);
        cal.add(Calendar.WEEK_OF_YEAR, 11);
        suite.addTest(new DurTest(new Dur(start, cal.getTime()), "-P654D"));

        // test adjacent weeks..
        ZonedDateTime newstart = ZonedDateTime.now(TimeZoneRegistry.getGlobalZoneId("America/Los_Angeles"))
                .withYear(2005).withMonth(1).withDayOfMonth(1).withHour(12).withMinute(0);
        ParameterList tzParams = new ParameterList(Collections.singletonList(new TzId(newstart.getZone().getId())));
        DtStart<ZonedDateTime> dtStart = new DtStart<>(tzParams, newstart);
        DtEnd<ZonedDateTime> dtEnd = new DtEnd<>(newstart.withDayOfMonth(2).withHour(11).withMinute(59));
        suite.addTest(new DurTest(
                new Dur(new DateTime(Date.from(dtStart.getDate().toInstant())), Date.from(dtEnd.getDate().toInstant())),
                "PT23H59M"));

        // test accross Europe/Paris DST boundary should not matter
        start = new net.fortuna.ical4j.model.DateTime("20110326T110000", tzreg.getTimeZone("America/Los_Angeles"));
        DateTime end = new net.fortuna.ical4j.model.DateTime("20110327T110000", tzreg.getTimeZone("America/Los_Angeles"));
        suite.addTest(new DurTest(new Dur(start, end), "P1D"));

        // test cross-year..
        Dur duration = new Dur(new net.fortuna.ical4j.model.Date("20061231"),
                new net.fortuna.ical4j.model.Date("20070101"));
        suite.addTest(new DurTest(duration, "P1D"));

        // test negative duration..
        suite.addTest(new DurTest(new Dur(-1), "-P1W"));
        suite.addTest(new DurTest(new Dur(-1, 0, 0, 0), "-P1D"));
        suite.addTest(new DurTest(new Dur(0, -1, 0, 0), "-PT1H"));
        suite.addTest(new DurTest(new Dur(0, 0, -1, 0), "-PT1M"));
        suite.addTest(new DurTest(new Dur(0, 0, 0, -1), "-PT1S"));
        suite.addTest(new DurTest(new Dur(-1, 0, 0, -1), "-P1DT1S"));
//        suite.addTest(new DurTest(new Dur(-1, 0, 0, -1), "PT23H59M59S"));

        // Test adding durations..
        Dur oneWeek = new Dur("P1W");
        Dur twoWeeks = new Dur("P2W");
        Dur oneDay = new Dur("P1D");
        Dur twoDays = new Dur("P2D");
        Dur oneHour = new Dur("PT1H");
        Dur twoHours = new Dur("PT2H");
        Dur oneMinute = new Dur("P1M");
        Dur twoMinutes = new Dur("P2M");
        Dur oneSecond = new Dur("PT1S");
        Dur twoSeconds = new Dur("PT2S");


        suite.addTest(new DurTest("testCompareToGreater", new Dur(1), new Dur(-1)));
        suite.addTest(new DurTest("testCompareToGreater", new Dur(0, 0, 0, 3), new Dur(0, 0, 0, -5)));
        suite.addTest(new DurTest("testCompareToGreater", new Dur(0, 0, 0, 5), new Dur(0, 0, 0, 3)));
        suite.addTest(new DurTest("testCompareToGreater", new Dur(0, 0, 0, -3), new Dur(0, 0, 0, -5)));
        
        return suite;
    }
}
