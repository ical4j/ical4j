package net.fortuna.ical4j.groovy

import net.fortuna.ical4j.model.Period
import net.fortuna.ical4j.model.PeriodList

class PeriodExtension {
	static Period plus(Period self, Period period) {
		self.add(period)
	}
	
	static PeriodList minus(Period self, Period period) {
		self.subtract(period)
	}
}
