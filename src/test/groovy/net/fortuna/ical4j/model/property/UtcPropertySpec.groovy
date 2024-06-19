package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification

import java.time.format.DateTimeParseException

class UtcPropertySpec extends Specification {

    def 'assert when parsing utc properties only UTC format is allowed'() {
        when: 'factory is invoked with a non-utc date-time value'
        factory.createProperty(value)

        then: 'appropriate exception is throw'
        thrown(DateTimeParseException)

        where:
        factory                 | value
        new Acknowledged.Factory()   | '20240112T120000'
        new Completed.Factory()   | '20240112T120000'
        new Created.Factory()   | '20240112T120000'
        new DtStamp.Factory()   | '20240112T120000'
        new LastModified.Factory()   | '20240112T120000'
        new Trigger.Factory()   | '20240112T120000'
        new TzUntil.Factory()   | '20240112T120000'
    }

    def 'assert when parsing utc properties with relaxed parsing enable any date-time format is allowed'() {
        setup: 'enable relaxed parsing'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

        when: 'factory is invoked with a non-utc date-time value'
        def p = factory.createProperty(value)

        then: 'appropriate exception is throw'
        p.value ==~ /.*Z/

        cleanup: 'reset compatibility hints'
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)

        where:
        factory                 | value
        new Acknowledged.Factory()   | '20240112T120000'
        new Completed.Factory()   | '20240112T120000'
        new Created.Factory()   | '20240112T120000'
        new DtStamp.Factory()   | '20240112T120000'
        new LastModified.Factory()   | '20240112T120000'
        new Trigger.Factory()   | '20240112T120000'
        new TzUntil.Factory()   | '20240112T120000'
    }
}
