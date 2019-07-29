package net.fortuna.ical4j.model


import net.fortuna.ical4j.model.component.VEvent
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized)
class VEventRecurrenceParametrizedTest extends GroovyTestCase {
	String start
	String startParam
	String end
	String endParam
	String rule
	Period period
	String exDates
	PeriodList expected

	VEventRecurrenceParametrizedTest(start, startParam, end, endParam, rule, period, exDates, expected) {
		this.start = start
		this.startParam = startParam
		this.end = end
		this.endParam = endParam
		this.rule = rule
		this.period = new Period(period)
		this.exDates = exDates
		this.expected = new PeriodList(true)
		expected.each { this.expected.add(new Period(it)) }
	}

	@Parameterized.Parameters
	static List data() {
		[
				['20101113', 'DATE',
				 '20101114', 'DATE',
				 'FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA',
				 '20101101T000000/20110101T000000',
				 null,
				 ['20101113T000000Z/P1D', '20101129T000000Z/P1D', '20101130T000000Z/P1D', '20101204T000000Z/P1D',
				  '20101220T000000Z/P1D', '20101221T000000Z/P1D', '20101225T000000Z/P1D']],

				['20101112', 'DATE',
				 '20101113', 'DATE',
				 'FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA',
				 '20101101T000000/20110101T000000',
				 null,
				 ['20101113T000000Z/P1D', '20101129T000000Z/P1D', '20101130T000000Z/P1D', '20101204T000000Z/P1D',
				  '20101220T000000Z/P1D', '20101221T000000Z/P1D', '20101225T000000Z/P1D']],

				['20170717', 'DATE',
				 '20170718', 'DATE',
				 'FREQ=YEARLY;COUNT=3;INTERVAL=2;BYMONTH=5;BYMONTHDAY=22,23,24,25,26,27,28;BYDAY=MO',
				 '20170717T000000/20270717T000000',
				 '20101129T000000,20101221T000000',
				 ['20190527T000000Z/P1D', '20210524T000000Z/P1D',
				  '20230522T000000Z/P1D']],

				['20100831T061500Z', 'DATETIME',
				 '20100831T064500Z', 'DATETIME',
				 'FREQ=MONTHLY;UNTIL=20110101',
				 '20100831T000000/20110131T000000',
				 null,
				 ['20100831T061500Z/PT30M', '20101031T061500Z/PT30M', '20101231T061500Z/PT30M']],

				['20100831T061500Z', 'DATETIME',
				 '20100831T064500Z', 'DATETIME',
				 'FREQ=MONTHLY;UNTIL=20110101',
				 '20100831T000000/20110131T000000',
				 '20101031T061500Z',
				 ['20100831T061500Z/PT30M', '20101231T061500Z/PT30M']],

				['20100831T061500Z', 'DATETIME',
				 '20100831T064500Z', 'DATETIME',
				 'FREQ=MONTHLY;UNTIL=20110101;BYMONTHDAY=31',
				 '20100831T000000/20110131T000000',
				 null,
				 ['20100831T061500Z/PT30M', '20101031T061500Z/PT30M', '20101231T061500Z/PT30M']],

				['20100831T061500Z', 'DATETIME',
				 '20100831T064500Z', 'DATETIME',
				 'FREQ=MONTHLY;UNTIL=20110101;BYMONTHDAY=31',
				 '20100831T000000/20110131T000000',
				 '20100831T061500Z',
				 ['20101031T061500Z/PT30M', '20101231T061500Z/PT30M']]

		]*.toArray()
	}

	@Test
	void test() {
		VEvent event = new ContentBuilder().vevent {
			dtstart(start, parameters: parameters() { value(startParam) })
			dtend(end, parameters: parameters() { value(endParam) })
			rrule(rule)
			if (exDates) exdate(exDates)
		}

		def actual = event.calculateRecurrenceSet(period)

		println actual
		assert actual == expected
	}
}
