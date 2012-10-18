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

import java.text.ParseException;

import spock.lang.Ignore;
import spock.lang.Shared;
import spock.lang.Specification

class DateTimeSpec extends Specification {

   @Shared TimeZoneRegistry tzRegistry = TimeZoneRegistryFactory.instance.createRegistry()
   
   def 'test date time initialisation with a standard timezone'() {
	   setup:
	   def originalTimezone = TimeZone.default
	   TimeZone.default = TimeZone.getTimeZone('Europe/London')
	   
	   def timezone = tzRegistry.getTimeZone('Europe/London')
	   
	   expect:
	   assert new DateTime(dateTimeString, timezone) as String == dateTimeString
	   
	   cleanup:
	   TimeZone.default = originalTimezone
	   
	   where:
	   dateTimeString << ['20110327T000000']
   }
   
   @Ignore
   def 'test date time initialisation with a custom timezone'() {
	   setup:
	   def originalTimezone = TimeZone.default
	   TimeZone.default = TimeZone.getTimeZone('Europe/London')
	   
	   def vTimeZone = new ContentBuilder().vtimezone {
		   tzid('Europe/London')
		   standard {
			   tzname('GMT')
			   dtstart('19710101T020000')
			   tzoffsetfrom('+0100')
			   tzoffsetto('+0000')
			   rrule('FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU')
		   }
		   daylight {
			   tzname('BST')
			   dtstart('19710101T010000')
			   tzoffsetfrom('+0000')
			   tzoffsetto('+0100')
			   rrule('FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU')
		   }
	   }
	   println vTimeZone
	   def customTimezone = new TimeZone(vTimeZone)
	   
	   expect:
	   assert new DateTime(dateTimeString, customTimezone) as String == dateTimeString
	   
	   cleanup:
	   TimeZone.default = originalTimezone
	   
	   where:
	   dateTimeString << ['20110327T000000']
   }
   
	  def 'test date time initialisation with a registered custom timezone'() {
		  setup:
		  def originalTimezone = TimeZone.default
		  TimeZone.default = TimeZone.getTimeZone('Europe/London')
		  
		  def vTimeZone = new ContentBuilder().vtimezone {
			  tzid('Europe/London')
			  standard {
				  tzname('GMT')
				  dtstart('19710101T020000')
				  tzoffsetfrom('+0100')
				  tzoffsetto('+0000')
				  rrule('FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU')
			  }
			  daylight {
				  tzname('BST')
				  dtstart('19710101T010000')
				  tzoffsetfrom('+0000')
				  tzoffsetto('+0100')
				  rrule('FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU')
			  }
		  }
		  println vTimeZone
		  def customTimezone = new TimeZone(vTimeZone)
		  tzRegistry.register(customTimezone)
		  
		  when:
		  new DateTime(dateTimeString, customTimezone)
		  
		  then:
		  thrown(ParseException)
   
		  cleanup:
		  TimeZone.default = originalTimezone
		  // remove custom timezone..
		  tzRegistry.clear()
		  
		  where:
		  dateTimeString << ['20110327T000000']
	  }
   
   def 'verify parse failure for invalid dates'() {
	   when:
	   new DateTime(dateTimeString, timezone)
	   
	   then:
	   thrown(ParseException)
	   
	   where:
	   dateTimeString		| timezone
	   '20110327T010000'	| tzRegistry.getTimeZone('Europe/London')
   }
   
   def 'test conversion of UTC date-time to local time'() {
	   setup: 'Override default timezone for test consistency'
	   def originalTimezone = TimeZone.default
	   TimeZone.default = TimeZone.getTimeZone('Australia/Melbourne')
	   
	   and:
	   DateTime dateTime = ['20110327T010000Z']
	   def cal = java.util.Calendar.instance
	   cal.time = dateTime
	   
	   expect:
	   assert !dateTime.is(cal.time)
	   assert cal.time.format("yyyyMMdd'T'hhmmss") == '20110327T120000'
	   
	   cleanup:
	   TimeZone.default = originalTimezone
   }
}
