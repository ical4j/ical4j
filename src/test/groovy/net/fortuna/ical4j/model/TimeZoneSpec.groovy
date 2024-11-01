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
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.util.Calendars
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.stream.Collectors

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
		'20231214T170000'	| 'America/Los_Angeles'		| false
		'20110328T110000'	| 'Australia/Melbourne'		| true
		'20110231T110000'	| 'Europe/London'		    | false
		'20231115T083000'	| 'America/Sao_Paulo'		| false
	}

	def 'test temporal adapter parsing uses correct timezone'() {
		when: 'parsing a string using a global zone id'
		def instance = TemporalAdapter.parse('20231214T170000',
				ZoneId.of('America/Los_Angeles'))

		then: 'correct timezone in resulting instance'
		instance.getTemporal().getZone() == ZoneId.of('America/Los_Angeles')
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
        TimeZone tz1 = tzRegistry.getTimeZone(tzid)
//        format1.setTimeZone(tz1);
//        java.util.Date date1 = format1.parse("20140302T080000");
//        java.util.Calendar c1 = java.util.Calendar.getInstance(TimeZones.getUtcTimeZone());
//        c1.setTime(date1);

        and: 'create date with java timezeone'
//        final DateFormat format2 = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        java.util.TimeZone tz2 = java.util.TimeZone.getTimeZone(tzid)
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

	@Unroll("#tzid")
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

	def testTzDublin_external() {
		given: 'negative dst support is enabled'
		CompatibilityHints.setHintEnabled('net.fortuna.ical4j.timezone.offset.negative_dst_supported',
				true)

		and: 'a negative dst dublin tz definition'
		def vtzFromGoogle = "BEGIN:VCALENDAR\n" +
				"CALSCALE:GREGORIAN\n" +
				"VERSION:2.0\n" +
				"PRODID:-//Google Inc//Google Calendar 70.9054//EN\n" +
				"BEGIN:VTIMEZONE\n" +
				"TZID:Europe/Dublin\n" +
				"BEGIN:STANDARD\n" +
				"TZOFFSETFROM:+0000\n" +
				"TZOFFSETTO:+0100\n" +
				"TZNAME:IST\n" +
				"DTSTART:19700329T010000\n" +
				"RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\n" +
				"END:STANDARD\n" +
				"BEGIN:DAYLIGHT\n" +
				"TZOFFSETFROM:+0100\n" +
				"TZOFFSETTO:+0000\n" +
				"TZNAME:GMT\n" +
				"DTSTART:19701025T020000\n" +
				"RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU\n" +
				"END:DAYLIGHT\n" +
				"END:VTIMEZONE\n" +
				"END:VCALENDAR"

		and: 'a timezone constructed from the definition'
		def iCalFromGoogle = new CalendarBuilder().build(new StringReader(vtzFromGoogle))
		Optional<VTimeZone> dublinFromGoogle = iCalFromGoogle.getComponent(Component.VTIMEZONE)

		when: 'a date-time is calculated with the timezone'
		def dt = new DateTime("20210108T151500", new TimeZone(dublinFromGoogle.get()))

		then: 'result is as expected'
		"20210108T151500" == dt.toString()

		cleanup:
		CompatibilityHints.clearHintEnabled('net.fortuna.ical4j.timezone.offset.negative_dst_supported')
	}

	def 'test tz aliases resolve correctly'() {
		setup:
		System.setProperty('net.fortuna.ical4j.timezone.update.enabled', 'false')

		expect: 'tz alias resolves to a timezone'
		TimeZoneRegistry.getGlobalZoneId(alias)

		cleanup:
		System.clearProperty('net.fortuna.ical4j.timezone.update.enabled')

		where:
		alias << getClass().getResourceAsStream('/net/fortuna/ical4j/model/tz.alias').readLines().stream()
				.filter(line -> !line.empty && !line.contains('#')).map {
			line -> line.split('\\s*=\\s*')[0]
		}.collect(Collectors.toList())
	}
}
