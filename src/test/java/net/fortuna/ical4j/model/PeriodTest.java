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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * $Id$
 *
 * <p>Period Tester.</p>
 *
 * </p>Tests the behaviour of the Period class to make sure it acts in
 * the expected way.</p>
 * @see net.fortuna.ical4j.model.Period
 */
public class PeriodTest {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodTest.class);

    private TimeZone originalDefault;

    @BeforeEach
    void setUp() {
        originalDefault = java.util.TimeZone.getDefault();
    }

    @AfterEach
    void tearDown() {
        TimeZone.setDefault(originalDefault);
    }

    /**
     *
     */
    @Test
    public void testEquals() {
        Period period = null;
        Period expectedPeriod = null;
        assertEquals(expectedPeriod, period);
    }

    /**
     * Testing of timezone functionality.
     */
    @Test
    public void testTimezone() {
        // change default tz to non-UTC timezone.
        java.util.TimeZone originalTzDefault = java.util.TimeZone.getDefault();
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Australia/Melbourne"));

        ZonedDateTime start = ZonedDateTime.now().plusYears(1);
        ZonedDateTime end = start.withZoneSameInstant(ZoneId.of("UTC"));

        Period<ZonedDateTime> p = new Period<>(start, end);

        LOG.info("Timezone test - period: [" + p + "]");
    }
	
	/**
	 * Regression test for VFREEBUSY.
	 * When start/end are OffSetDateTime with UTC timeStamps, period.toInterval()
	 * must not re-apply systems timezone.
	 */
	@Test
	public void testUTCOffSetDateTimeNotShiftedBySystemTimezone() {
		//Simulated system timezone
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Bangkok"));
		
		//FREEBUSY:20250110T100000Z/20250110T103000Z
		OffsetDateTime start = OffsetDateTime.of(2025, 1, 10, 10, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end   = OffsetDateTime.of(2025, 1, 10, 10, 30, 0, 0, ZoneOffset.UTC);
		
		Period<OffsetDateTime> period = new Period<>(start, end);
		
		org.threeten.extra.Interval interval = period.toInterval(ZoneId.of("Asia/Bangkok"));
		
		assertEquals(start.toInstant(), interval.getStart(),
				"Start instant must not be shifted by system timezone");
		assertEquals(end.toInstant(), interval.getEnd(),
				"End instant must not be shifted by system timezone");
	}
}
