package net.fortuna.ical4j.model

import net.fortuna.ical4j.util.Dates
import spock.lang.Specification
import spock.lang.Unroll

class WeekDaySpec extends Specification {

    @Unroll
    def 'assert result of getNegativeMonthlyOffset'() {

        expect: 'returned result matches expected'
        java.util.Calendar cal = Dates.getCalendarInstance(date)
        cal.setTime(date)
        WeekDay.getNegativeMonthlyOffset(cal) as String == expected

        where:
        date                    | expected
        new Date('20180228')    | '-1WE'
        new Date('20180221')    | '-2WE'
        new Date('20191130')    | '-1SA'
    }

    def 'asset parsing weekday value'() {
        expect: 'parsed value matches expected'
        new WeekDay(weekdayString).toString().equalsIgnoreCase(expected)

        where:
        weekdayString   | expected
        'mo'            | 'mo'
        'Tu'            | 'Tu'
        'WE'            | 'WE'
        '-1Th'            | '-1Th'
        '+2fr'            | '2fr'
        '-3SA'            | '-3SA'
        'Su'            | 'Su'
    }
}
