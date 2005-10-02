/*
 * $Id$
 *
 * Created on 30/06/2005
 *
 * Copyright (c) 2005, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
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

/**
 * @author Ben Fortuna
 *
 */
public class DateTest extends TestCase {

    /*
     * Class under test for void Date(long)
     */
    public void testDatelong() {
        assertEquals("19700101", new Date(0l).toString());
    }

    /*
     * Class under test for void Date(Date)
     */
    public void testDateDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1984);
        // months are zero-based..
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DAY_OF_MONTH, 17);
        assertEquals("19840417", new Date(cal.getTime()).toString());
    }

    /*
     * Class under test for void Date(String)
     */
    public void testDateString() throws Exception {
        assertEquals("20050630", new Date("20050630").toString());
    }
    
    /**
     * Test equality of Date instances created using different constructors.
     * @throws ParseException
     */
    public void testDateEquals() throws ParseException {
        Date date1 = new Date("20050101");
    
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC"));
        calendar.clear();
        calendar.set(2005, 0, 1);
        calendar.set(Calendar.MILLISECOND, 1);
        Date date2 = new Date(calendar.getTime());
    
        assertEquals(date1.toString(), date2.toString());
        assertEquals(date1, date2);
    }
}
