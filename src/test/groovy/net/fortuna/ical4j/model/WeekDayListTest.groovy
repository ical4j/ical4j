package net.fortuna.ical4j.model

import spock.lang.Specification

class WeekDayListTest extends Specification {

    def 'test forms of string parsing'() {
        expect: 'parsed result matches expected'
        new WeekDayList(weekdayListString) == expectedParsedResult

        where:
        weekdayListString   | expectedParsedResult
        "MO"                | new WeekDayList(WeekDay.MO)
        "MO,TU,WE,TH"       | new WeekDayList(WeekDay.MO, WeekDay.TU, WeekDay.WE, WeekDay.TH)
        "MO, TU ,WE,TH"     | new WeekDayList(WeekDay.MO, WeekDay.TU, WeekDay.WE, WeekDay.TH)
        ""                  | new WeekDayList()
    }
}
