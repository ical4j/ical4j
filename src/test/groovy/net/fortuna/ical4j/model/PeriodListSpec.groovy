package net.fortuna.ical4j.model

import spock.lang.Specification

import java.time.LocalDate

class PeriodListSpec extends Specification {

    static Period<LocalDate> monthJanuary, monthFebruary, monthMarch, monthApril,
            monthMay, monthJune, monthJuly, monthAugust, monthSeptember, monthOctober,
            monthNovember, monthDecember, head1994, tail1994

    def setupSpec() {
        // create ranges that are intervals
        LocalDate begin1994 = LocalDate.now().withYear(1994).withMonth(1).withDayOfMonth(1)
        LocalDate end1994 = begin1994.withMonth(12).withDayOfMonth(31)
        LocalDate jan1994 = end1994.withMonth(1).withDayOfMonth(22)
        LocalDate feb1994 = jan1994.withMonth(2).withDayOfMonth(15)
        LocalDate mar1994 = feb1994.withMonth(3).withDayOfMonth(4)
        LocalDate apr1994 = mar1994.withMonth(4).withDayOfMonth(12)
        LocalDate may1994 = apr1994.withMonth(5).withDayOfMonth(19)
        LocalDate jun1994 = may1994.withMonth(6).withDayOfMonth(21)
        LocalDate jul1994 = jun1994.withMonth(7).withDayOfMonth(28)
        LocalDate aug1994 = jul1994.withMonth(8).withDayOfMonth(20)
        LocalDate sep1994 = aug1994.withMonth(9).withDayOfMonth(17)
        LocalDate oct1994 = sep1994.withMonth(10).withDayOfMonth(29)
        LocalDate nov1994 = oct1994.withMonth(11).withDayOfMonth(11)
        LocalDate dec1994 = nov1994.withMonth(12).withDayOfMonth(2)

        monthJanuary = new Period<>(jan1994, feb1994)
        monthFebruary = new Period<>(feb1994, mar1994)
        monthMarch = new Period<>(mar1994, apr1994)
        monthApril = new Period<>(apr1994, may1994)
        monthMay = new Period<>(may1994, jun1994)
        monthJune = new Period<>(jun1994, jul1994)
        monthJuly = new Period<>(jul1994, aug1994)
        monthAugust = new Period<>(aug1994, sep1994)
        monthSeptember = new Period<>(sep1994, oct1994)
        monthOctober = new Period<>(oct1994, nov1994)
        monthNovember = new Period<>(nov1994, dec1994)
        monthDecember = new Period<>(dec1994, end1994)
        head1994 = new Period<>(begin1994, jan1994)
        tail1994 = new Period<>(dec1994, end1994)

        // create sets that contain the ranges
        List<Period<LocalDate>> oddMonths = new ArrayList<>()
        oddMonths.add(monthJanuary)
        oddMonths.add(monthMarch)
        oddMonths.add(monthMay)
        oddMonths.add(monthJuly)
        oddMonths.add(monthSeptember)
        oddMonths.add(monthNovember)
        List<Period<LocalDate>> tailSet = new ArrayList<>()
        tailSet.add(tail1994)

        /*
         * assertNull("Removing null from a null set should return null", empty1.subtract(null)); assertNull("Removing
         * from a null set should return null", normalizer.subtractDateRanges(null, headSet));
         */
        PeriodList<LocalDate> evenMonths = new PeriodList<>(CalendarDateFormat.DATE_FORMAT)
            .add(monthFebruary)
            .add(monthApril)
            .add(monthJune)
            .add(monthAugust)
            .add(monthOctober)
            .add(monthDecember)

        PeriodList<LocalDate> headSet = new PeriodList<>(CalendarDateFormat.DATE_FORMAT)
            .add(head1994)

        PeriodList<LocalDate> empty1 = new PeriodList<>(CalendarDateFormat.DATE_FORMAT)
        PeriodList<LocalDate> empty2 = new PeriodList<>(CalendarDateFormat.DATE_FORMAT)

    }

    def 'test hashcode equality'() {
        given: 'a period list'
        PeriodList list1 = PeriodList.parse('20140803T120100/P1D')

        and: 'a second identical list'
        PeriodList list2 = PeriodList.parse('20140803T120100/P1D')

        expect: 'object equality'
        list1 == list2

        and: 'hashcode equality'
        list1.hashCode() == list2.hashCode()
    }

    def 'test addition'() {
        given: 'two period lists added'
        def sum = new PeriodList(periodList1).add(new PeriodList(periodList2))

        expect: 'result is as expected'
        sum == new PeriodList(expectedSum)

        where:
        periodList1                     | periodList2                   | expectedSum
        [monthNovember, monthDecember]  | [monthNovember, monthJuly]    | [monthJuly, monthNovember, monthDecember]
        [monthOctober, monthNovember, monthDecember]  | [monthNovember]    | [monthOctober, monthNovember, monthDecember]
        [monthNovember, monthDecember]  | [monthOctober, monthNovember]    | [monthOctober, monthNovember, monthDecember]
    }

    def 'test subtraction'() {
        given: 'a period list subtracted from another'
        def sum = new PeriodList(periodList1).subtract(new PeriodList(periodList2))

        expect: 'result is as expected'
        sum == new PeriodList(expectedSum)

        where:
        periodList1                     | periodList2                   | expectedSum
        [monthNovember, monthDecember]  | [monthNovember]    | [monthDecember]
        [monthOctober, monthNovember, monthDecember]  | [monthNovember]    | [monthOctober, monthDecember]
        [monthNovember, monthDecember]  | [monthOctober, monthNovember]    | [monthDecember]
        [monthSeptember, monthOctober, monthNovember, monthDecember]  | [monthOctober, monthNovember]    | [monthSeptember, monthDecember]
        [monthSeptember, monthOctober, monthNovember, monthDecember]  | [monthApril, monthMay]    | [monthSeptember, monthOctober, monthNovember, monthDecember]
    }
}
