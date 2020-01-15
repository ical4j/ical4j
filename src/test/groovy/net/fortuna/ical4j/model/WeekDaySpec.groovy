package net.fortuna.ical4j.model

import net.fortuna.ical4j.util.Dates
import spock.lang.Specification
import spock.lang.Unroll

class WeekDaySpec extends Specification {

    @Unroll
    def 'assert result of getNegativeMonthlyOffset'() {

        expect: 'returned result matches expected'
        WeekDay.getNegativeMonthlyOffset(cal) as String == expected

        where:
        cal     | expected
        Dates.getCalendarInstance(new Date('20180228')) | '-3WE'
        Dates.getCalendarInstance(new Date('20191130')) | '-3WE'
    }
}
