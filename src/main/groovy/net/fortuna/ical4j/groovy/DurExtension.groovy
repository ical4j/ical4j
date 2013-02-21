package net.fortuna.ical4j.groovy

import net.fortuna.ical4j.model.Dur

class DurExtension {
	static Dur plus(Dur self, Dur duration) {
		self.add(duration)
	}
	
	static Dur negative(Dur self) {
		self.negate()
	}
}
