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
package net.fortuna.ical4j.model

import groovy.util.logging.Slf4j
import net.fortuna.ical4j.util.Calendars
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.ZoneOffset

@Slf4j
class TimeZoneSpec extends Specification {

	@Shared TimeZoneRegistry tzRegistry = TimeZoneRegistryFactory.instance.createRegistry()

    @Unroll
	def 'verify date in daylight time for timezone: #date #timezone'() {
		expect: 'specified date is in daylight time'
		def tz = tzRegistry.getTimeZone(timezone)
		tz.inDaylightTime(new DateTime(date)) == inDaylightTime
		
		where:
		date				| timezone					| inDaylightTime
		'20110328T110000'	| 'America/Los_Angeles'		| true
		'20110328T110000'	| 'Australia/Melbourne'		| true
		'20110231T110000'	| 'Europe/London'		    | false
	}
	
	def 'verify string representation'() {
		expect:
		Calendar calendar = Calendars.wrap(tzRegistry.getTimeZone('Europe/Prague').vTimeZone)
		println calendar.toString()
	}
	
	def 'verify custom tz aliases work'() {
		expect:
		tzRegistry.getTimeZone(alias) == tzRegistry.getTimeZone(actual)
		
		where:
		alias	| actual
		'CET'	| 'Europe/Berlin'
		'CEST'	| 'Europe/Berlin'
	}

    @Unroll
    def 'test timezone getoffset issue: #tzid'() {
        setup: 'create date with ical4j timezone'
//        final DateFormat format1 = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        TimeZone tz1 = tzRegistry.getTimeZone(tzid);
//        format1.setTimeZone(tz1);
//        java.util.Date date1 = format1.parse("20140302T080000");
//        java.util.Calendar c1 = java.util.Calendar.getInstance(TimeZones.getUtcTimeZone());
//        c1.setTime(date1);

        and: 'create date with java timezeone'
//        final DateFormat format2 = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        java.util.TimeZone tz2 = java.util.TimeZone.getTimeZone(tzid);
//        format2.setTimeZone(tz2);
//        java.util.Date date2 = format2.parse("20140302T080000");
//        java.util.Calendar c2 = java.util.Calendar.getInstance(TimeZones.getUtcTimeZone());
//        c2.setTime(date2);

        expect:
//        date1 == date2
//        c1 == c2
        tz1.getOffset(era, year, month, day, dayOfWeek, millis) == tz2.getOffset(era, year, month, day, dayOfWeek, millis)
//        c1.get(java.util.Calendar.ZONE_OFFSET) == c2.get(java.util.Calendar.ZONE_OFFSET)
//        c1.get(java.util.Calendar.DST_OFFSET) == c2.get(java.util.Calendar.DST_OFFSET)
//        c1.get(java.util.Calendar.HOUR_OF_DAY) == c2.get(java.util.Calendar.HOUR_OF_DAY)
//        c1.getTimeInMillis() == c2.getTimeInMillis()

        where:
//        era | year  | month | day   | dayOfWeek                 | millis
//        1   | 2014  | 2     | 13    | java.util.Calendar.SUNDAY | 36000000
        [tzid, era, year, month, day, dayOfWeek, millis] << [
                ['Australia/Melbourne','America/Los_Angeles','Europe/London','Europe/Vienna'],
                1, 2014, [0, 6, 11], 31..30, java.util.Calendar.SUNDAY, 36000000].combinations()
    }

    def 'verify valid timezone ids'() {
        expect: 'the specified id translates to a timezone instance'
        def tz = tzRegistry.getTimeZone(tzid)

        and: 'the timezone id matches the specified id'
        tz.ID == expectedId

        where:
		tzid					| expectedId
		'Australia/Lord_Howe'	| 'Australia/Lord_Howe'
		'Asia/Rangoon'			| 'Asia/Yangon'
		'America/Santa_Isabel'	| 'America/Tijuana'
		'Pacific/Johnston'		| 'Pacific/Honolulu'
		'EST'					| 'EST'
    }

	@Unroll('#tzid')
    def 'verify timezone offsets'() {
        expect: 'the specified id has the expected offsets'
        def tz = tzRegistry.getTimeZone(tzid)

        and: 'the timezone id matches the specified id'
        tz.rawOffset == expectedRawOffset

		and: 'daylight offset as expected'
		!tz.inDaylightTime(daylightDate) ||
				tz.getOffset(daylightDate.time) == expectedDaylightOffset

        where:
		tzid					| expectedRawOffset	| expectedDaylightOffset	| daylightDate
		'America/Sao_Paulo'		| -10800000			| -10800000					| Date.from(LocalDate.of(2020, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
		'America/Sao_Paulo'		| -10800000			| -7200000					| Date.from(LocalDate.of(2018, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
		'America/Campo_Grande'	| -14400000         | -14400000					| Date.from(LocalDate.of(2020, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
		'America/Campo_Grande'	| -14400000         | -10800000					| Date.from(LocalDate.of(2018, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
		'America/Cuiaba'		| -14400000         | -14400000					| Date.from(LocalDate.of(2020, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
		'America/Cuiaba'		| -14400000         | -10800000					| Date.from(LocalDate.of(2018, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
    }
}
