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
}
