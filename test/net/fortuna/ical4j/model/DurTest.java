/*
 * $Id$
 *
 * Created on 20/06/2005
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

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class DurTest extends TestCase {
    
    private static Log log = LogFactory.getLog(DurTest.class);

    /*
     * Class under test for void Dur(String)
     */
    public void testDurString() {
        Dur duration = new Dur("PT15M");
        log.info(duration);
        
        Date start = new Date();
        log.info("[" + start + "] -> [" + duration.getTime(start) + "]");
    }

    /*
     * Class under test for void Dur(long)
     */
    public void testDurint() {
        Dur duration = new Dur(33);
        log.info(duration);
    }

    /*
     * Class under test for void Dur(Date, Date)
     */
    public void testDurDateDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2005, 7, 1);
        Date start = cal.getTime();
        
        cal.add(Calendar.YEAR, 1);
        assertEquals("P52W", new Dur(start, cal.getTime()).toString());
        
        cal.setTime(start);
        cal.add(Calendar.WEEK_OF_YEAR, -5);
        assertEquals("-P5W", new Dur(start, cal.getTime()).toString());
        
        cal.setTime(start);
        cal.add(Calendar.DAY_OF_WEEK, 11);
        assertEquals("P11D", new Dur(start, cal.getTime()).toString());
        
        cal.setTime(start);
        cal.add(Calendar.HOUR_OF_DAY, 25);
        assertEquals("P1DT1H", new Dur(start, cal.getTime()).toString());
        
        cal.setTime(start);
        cal.add(Calendar.MINUTE, -23);
        assertEquals("-PT23M", new Dur(start, cal.getTime()).toString());
        
        cal.setTime(start);
        cal.add(Calendar.SECOND, -5);
        assertEquals("-PT5S", new Dur(start, cal.getTime()).toString());
        
        cal.setTime(start);
        cal.add(Calendar.HOUR_OF_DAY, 25);
        cal.add(Calendar.MINUTE, -23);
        cal.add(Calendar.SECOND, -5);
        assertEquals("P1DT36M55S", new Dur(start, cal.getTime()).toString());

        cal.setTime(start);
        cal.add(Calendar.YEAR, -2);
        cal.add(Calendar.WEEK_OF_YEAR, 11);
        assertEquals("-P94W", new Dur(start, cal.getTime()).toString());
    }

}
