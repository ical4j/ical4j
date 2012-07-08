package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ContentBuilder;
import net.fortuna.ical4j.util.Constants;
import spock.lang.Specification

class StatusFactorySpec extends Specification {

	ContentBuilder builder = []
	
	def 'verify constants are preferred over new instances'() {
		expect: 'constant property'
		assert builder.status(value) == constant
		
		where:
		value							| constant
		Status.VEVENT_CANCELLED.value	| Status.VEVENT_CANCELLED
		Status.VEVENT_CONFIRMED.value	| Status.VEVENT_CONFIRMED
		Status.VEVENT_TENTATIVE.value	| Status.VEVENT_TENTATIVE
	}
}
