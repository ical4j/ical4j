package net.fortuna.ical4j.transform.recurrence

import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.CalScale
import net.fortuna.ical4j.model.property.ExRule
import net.fortuna.ical4j.model.property.RRule
import net.fortuna.ical4j.model.property.Version
import net.fortuna.ical4j.util.RandomUidGenerator
import net.fortuna.ical4j.util.UidGenerator
import spock.lang.Specification

import java.time.DayOfWeek
import java.time.Instant
import java.util.stream.IntStream

import static net.fortuna.ical4j.model.WeekDay.*
import static net.fortuna.ical4j.transform.recurrence.Frequency.WEEKLY

class ByDayRuleTest extends Specification {

    def 'verify transformations by day'() {
        given: 'a BYDAY rule'
        ByDayRule rule = [new WeekDayList(rulePart), frequency, DayOfWeek.SUNDAY]

        and: 'a list of dates'
        def dates = []
        dateStrings.each {
            dates << TemporalAdapter.parse(it).temporal
        }

        def expected = []
        expectedResult.each {
            expected << TemporalAdapter.parse(it).temporal
        }

        expect: 'the rule transforms the dates correctly'
        rule.transform(dates) == expected

        where:
        rulePart | frequency | dateStrings  | expectedResult
        FR | WEEKLY | ['20150103'] | ['20150102']
        [SU, MO] as WeekDay[] | WEEKLY    | ['20110306'] | ['20110306', '20110307']
    }

    def 'test limit with FREQ=MINUTELY'() {
        given: 'a calendar definition'
        Calendar calendar = new Calendar();
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        TemporalAdapter<Instant> dateTime = TemporalAdapter.parse("20210104T130000Z");
        VEvent e1 = new VEvent(dateTime.temporal, "even");
        UidGenerator ug = new RandomUidGenerator();
        e1.getProperties().add(ug.generateUid());

        // recurency
        final NumberList hourList = new NumberList();
        hourList.add(13);
        hourList.add(14);
        hourList.add(15);
        hourList.add(16);

        Recur recur = new Recur.Builder()
                .frequency(Frequency.MINUTELY)
                .interval(15)
                .hourList(hourList)
                .dayList(new WeekDayList(WE))
                .build();
        e1.getProperties().add(new RRule(recur));

        //exrules
        final NumberList hourExList = new NumberList();
        hourExList.add(16);
        Recur recurEx = new Recur.Builder()
                .frequency(Frequency.MINUTELY)
                .interval(15)
                .hourList(hourExList)
                .minuteList(numberList(30, 60))
                .build();
        Recur recurEx2 = new Recur.Builder()
                .frequency(Frequency.MINUTELY)
                .interval(15)
                .hourList(numberList(13, 14))
                .minuteList(numberList(0, 30))
                .build();

        e1.getProperties().add(new ExRule(recurEx));
        e1.getProperties().add(new ExRule(recurEx2));

        calendar.getComponents().add(e1);
        System.out.println(calendar);

        expect: 'dates are calculated successfully'
        System.out.println("--------------------------------------------------");
        TemporalAdapter<Instant> from = TemporalAdapter.parse("20200101T070000Z");
        TemporalAdapter<Instant> to = TemporalAdapter.parse("20210107T070000Z")

        Period period = new Period(from.temporal, to.temporal);
        for (Component c : calendar.getComponents(Component.VEVENT)) {
            PeriodList list = c.calculateRecurrenceSet(period);
            for (Object po : list) {
                System.out.println((Period) po);
            }
        }
    }

    private static NumberList numberList(int startInclusive, int endExclusive) {
        final NumberList integers = new NumberList();
        IntStream.range(startInclusive, endExclusive).forEach(integers::add);
        return integers;
    }
}
