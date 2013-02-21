package net.fortuna.ical4j.model

import spock.lang.Specification

class PeriodSpec extends Specification {

	def 'extension module test: plus'() {
		expect:
		new Period('20110412T120000/1D') + new Period('20110413T120000/1D') == new Period('20110412T120000/2D')
	}
	
	def 'extension module test: minus'() {
		expect:
		new Period('20110412T120000/1D') - new Period('20110412T130000/1H') == new PeriodList('20110412T120000/1H,20110412T140000/22H')
	}
}
