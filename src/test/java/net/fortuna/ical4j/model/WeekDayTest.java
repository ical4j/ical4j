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

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 * Created on 13/02/2005
 *
 * $Id$
 *
 * @author Ben Fortuna
 */
public class WeekDayTest extends TestCase {
    
    private static Log log = LogFactory.getLog(WeekDayTest.class);

    public void testGetWeekDay() {
        Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        cal.set(2010, 4, 26, 12, 0);
        assertEquals(WeekDay.WE, WeekDay.getWeekDay(cal));
    }

    public void testGetMonthlyOffset() {
        Calendar cal = Calendar.getInstance();
        log.info("Monthly offset: " + WeekDay.getMonthlyOffset(cal));
        
        cal.add(Calendar.DAY_OF_MONTH, 15);
        log.info("Monthly offset: " + WeekDay.getMonthlyOffset(cal));
    }

    public void testGetNegativeMonthlyOffset() {
        Calendar cal = Calendar.getInstance();
        log.info("Negative monthly offset: " + WeekDay.getNegativeMonthlyOffset(cal));
        
        cal.add(Calendar.DAY_OF_MONTH, 15);
        log.info("Negative monthly offset: " + WeekDay.getNegativeMonthlyOffset(cal));
    }
    
    /**
     * Tests the parsing of various offset values.
     */
    public void testOffsetParsing() {
        log.info(new WeekDay("-1SU"));
        log.info(new WeekDay("+2SU"));
    }
}
