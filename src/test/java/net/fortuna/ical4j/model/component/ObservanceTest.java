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
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.fortuna.ical4j.model.component.Observance.MAX_RECURRENCE_ONSETS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * $Id$
 *
 * Created on 18/11/2007
 *
 * @author fortuna
 *
 */
public class ObservanceTest {

    private Observance observance;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @BeforeEach
    void setUp() throws Exception {
        observance = (Observance) new Standard().withProperty(new DtStart<>("16010101T030000"))
                .withProperty(new TzOffsetFrom("+0200"))
                .withProperty(new TzOffsetTo("+0100"))
                .withProperty(new RRule<>("FREQ=YEARLY;WKST=MO;INTERVAL=1;BYMONTH=10;BYDAY=-1SU"))
                .getFluentTarget();
    }

    /**
     * Test method for {@link net.fortuna.ical4j.model.component.Observance#getLatestOnset(Temporal)}.
     */
    @Test
    public void testGetLatestOnset() {
        for (int i = 10; i > 0; i--) {
            OffsetDateTime onset = observance.getLatestOnset(
                    ZonedDateTime.now().withYear(2000 + 1).withMonth(i).withDayOfMonth(i));
            assertNotNull(onset);
        }
    }

    /**
     * A sub-yearly frequency with an early DTSTART would otherwise expand to every interval since the
     * epoch; getLatestOnset must bound the expansion and still return an onset.
     */
    @Test
    public void getLatestOnsetBoundsSubYearlyFrequency() {
        Observance crafted = (Observance) new Standard().withProperty(new DtStart<>("19700101T000000"))
                .withProperty(new TzOffsetFrom("+0000"))
                .withProperty(new TzOffsetTo("+0100"))
                .withProperty(new RRule<>("FREQ=MINUTELY"))
                .getFluentTarget();

        OffsetDateTime onset = assertTimeoutPreemptively(Duration.ofSeconds(10),
                () -> crafted.getLatestOnset(Instant.now()));
        assertNotNull(onset);
    }

    /**
     * A rule whose single-period BY* cross-product is itself unbounded cannot be capped by the onset
     * limit alone, because the recurrence engine materialises a whole period before that limit applies.
     * getLatestOnset must skip such a rule and fall back to the initial (DTSTART) onset.
     */
    @Test
    public void getLatestOnsetSkipsExcessivePerPeriodExpansion() {
        String rrule = "FREQ=YEARLY;BYYEARDAY=" + range(1, 366) + ";BYHOUR=" + range(0, 23)
                + ";BYMINUTE=" + range(0, 59) + ";BYSECOND=" + range(0, 59);
        RRule<OffsetDateTime> recurrence = new RRule<>(rrule);
        // a single year would yield 366 * 24 * 60 * 60 candidates before any onset cap could apply
        assertEquals(31_622_400L, recurrence.getRecur().getMaxCandidatesPerPeriod());
        assertTrue(recurrence.getRecur().getMaxCandidatesPerPeriod() > MAX_RECURRENCE_ONSETS);

        Observance crafted = (Observance) new Standard().withProperty(new DtStart<>("19700101T000000"))
                .withProperty(new TzOffsetFrom("+0000"))
                .withProperty(new TzOffsetTo("+0100"))
                .withProperty(recurrence)
                .getFluentTarget();

        OffsetDateTime onset = assertTimeoutPreemptively(Duration.ofSeconds(10),
                () -> crafted.getLatestOnset(Instant.parse("2000-01-01T00:00:00Z")));
        assertEquals(OffsetDateTime.parse("1970-01-01T00:00+00:00"), onset);
    }

    private static String range(int from, int to) {
        return IntStream.rangeClosed(from, to).mapToObj(Integer::toString).collect(Collectors.joining(","));
    }
}
