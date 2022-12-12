package net.fortuna.ical4j

import net.fortuna.ical4j.data.CalendarBuilder
import spock.lang.Shared
import spock.lang.Specification

abstract class AbstractBuilderTest extends Specification {

    @Shared
    CalendarBuilder builder

    def setupSpec() {
        builder = []
    }
}
