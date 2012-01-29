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

import java.text.ParseException;
import java.util.Calendar;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fortuna.ical4j.util.TimeZones;

/**
 * $Id$
 *
 * Created on 30/06/2005
 *
 * @author Ben Fortuna
 *
 */
public class DateTest extends TestCase {

    private Date date;

    private java.util.Date date2;
    
    private String expectedString;
    
    /**
     * @param date
     * @param expectedString
     */
    public DateTest(Date date, String expectedString) {
        super("testToString");
        this.date = date;
        this.expectedString = expectedString;
    }
    
    /**
     * @param date
     * @param date2
     */
    public DateTest(Date date, java.util.Date date2) {
        super("testEquals");
        this.date = date;
        this.date2 = date2;
    }
    
    /**
     * 
     */
    public void testToString() {
        assertEquals(expectedString, date.toString());
    }
    
    /**
     * 
     */
    public void testEquals() {
        assertEquals(date2, date);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#getName()
     */
    public String getName() {
        return super.getName() + " [" + date.toString() + "]";
    }
    
    /**
     * @return
     * @throws ParseException 
     */
    public static TestSuite suite() throws ParseException {
        TestSuite suite = new TestSuite();
        suite.addTest(new DateTest(new Date(0l), "19700101"));

        Calendar cal = Calendar.getInstance(TimeZones.getDateTimeZone());
        cal.clear();
        cal.set(Calendar.YEAR, 1984);
        // months are zero-based..
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        suite.addTest(new DateTest(new Date(cal.getTime()), "19840417"));

        suite.addTest(new DateTest(new Date("20050630"), "20050630"));

        // Test equality of Date instances created using different constructors..
        Calendar calendar = Calendar.getInstance(TimeZones.getDateTimeZone());
        calendar.clear();
        calendar.set(2005, 0, 1);
        calendar.set(Calendar.MILLISECOND, 1);
        suite.addTest(new DateTest(new Date(calendar.getTime()), new Date("20050101").toString()));
        suite.addTest(new DateTest(new Date(calendar.getTime()), new Date("20050101")));
        
        calendar = Calendar.getInstance(TimeZones.getDateTimeZone());
        calendar.clear();
        calendar.set(2005, 0, 1);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        suite.addTest(new DateTest(new Date("20050101"), calendar.getTime()));
        return suite;
    }
}
