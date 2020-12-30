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
	Set expected

	VEventRecurrenceParametrizedTest(start, startParam, end, endParam, rule,
									 period, exDates, expectedString) {
		this.start = start
		this.startParam = startParam
		this.end = end
		this.endParam = endParam
		this.rule = rule
		this.period = Period.parse(period)
		this.exDates = exDates
		this.expected = expectedString.collect {Period.parse(it) } as Set
	}

	@Parameterized.Parameters
	static List data() {
		[
				['20101113', 'DATE',
				 '20101114', 'DATE',
				 'FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA',
				 '20101101/20110101',
				 null,
				 ['20101113/P1D', '20101129/P1D', '20101130/P1D', '20101204/P1D',
				  '20101220/P1D', '20101221/P1D', '20101225/P1D']],

				['20101112', 'DATE',
				 '20101113', 'DATE',
				 'FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA',
				 '20101101/20110101',
				 null,
				 ['20101113/P1D', '20101129/P1D', '20101130/P1D', '20101204/P1D',
				  '20101220/P1D', '20101221/P1D', '20101225/P1D']],

				['20170717', 'DATE',
				 '20170718', 'DATE',
				 'FREQ=YEARLY;COUNT=3;INTERVAL=2;BYMONTH=5;BYMONTHDAY=22,23,24,25,26,27,28;BYDAY=MO',
				 '20170717/20270717',
				 '20101129,20101221',
				 ['20190527/P1D', '20210524/P1D',
				  '20230522/P1D']],

				['20100831T061500Z', 'DATETIME',
				 '20100831T064500Z', 'DATETIME',
				 'FREQ=MONTHLY;UNTIL=20110101',
				 '20100831T000000Z/20110131T000000Z',
				 null,
				 ['20100831T061500Z/PT30M', '20101031T061500Z/PT30M', '20101231T061500Z/PT30M']],

				['20100831T061500Z', 'DATETIME',
				 '20100831T064500Z', 'DATETIME',
				 'FREQ=MONTHLY;UNTIL=20110101',
				 '20100831T000000Z/20110131T000000Z',
				 '20101031T061500Z',
				 ['20100831T061500Z/PT30M', '20101231T061500Z/PT30M']],

				['20100831T061500Z', 'DATETIME',
				 '20100831T064500Z', 'DATETIME',
				 'FREQ=MONTHLY;UNTIL=20110101;BYMONTHDAY=31',
				 '20100831T000000Z/20110131T000000Z',
				 null,
				 ['20100831T061500Z/PT30M', '20101031T061500Z/PT30M', '20101231T061500Z/PT30M']],

				['20100831T061500Z', 'DATETIME',
				 '20100831T064500Z', 'DATETIME',
				 'FREQ=MONTHLY;UNTIL=20110101;BYMONTHDAY=31',
				 '20100831T000000Z/20110131T000000Z',
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
		assert actual == expected
	}
}
