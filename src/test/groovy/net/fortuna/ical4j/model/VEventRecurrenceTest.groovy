package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.VEvent;
import groovy.util.GroovyTestCase;

class VEventRecurrenceTest extends GroovyTestCase {

	void testCalculateRecurrenceSet() {
		VEvent event = new ContentBuilder().vevent {
			dtstart('20101113', parameters: parameters() {
				value('DATE')})
			dtend('20101114', parameters: parameters() {
				value('DATE')})
			rrule('FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA')
		}
		
		def dates = event.calculateRecurrenceSet(new Period('20101101T000000/20110101T000000'))
		
		def expected = new PeriodList(true)
		expected.add new Period('20101113T000000Z/P1D')
		expected.add new Period('20101129T000000Z/P1D')
		expected.add new Period('20101130T000000Z/P1D')
		expected.add new Period('20101204T000000Z/P1D')
		expected.add new Period('20101220T000000Z/P1D')
		expected.add new Period('20101221T000000Z/P1D')
		expected.add new Period('20101225T000000Z/P1D')
		
		println dates
		assert dates == expected
	}
}
