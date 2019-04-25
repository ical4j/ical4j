package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.DefaultTimeZoneRegistryFactory
import net.fortuna.ical4j.util.CompatibilityHints
import spock.lang.Specification

class DtStampSpec extends Specification {

    def 'test dtstamp creation in relaxed parsing mode'() {
        given: 'relaxed parsing is enabled'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true)

        when: 'creating a dtstamp with a timezone'
        def dtstamp = new DtStamp.Factory().createProperty()
        dtstamp.parameters.add(new net.fortuna.ical4j.model.parameter.TzId('Europe/Prague'))
        dtstamp.timeZone = DefaultTimeZoneRegistryFactory.instance.createRegistry().getTimeZone('Europe/Prague')
        dtstamp.value = '20190104T155229'

        then: 'dtstamp is created successfully'
        dtstamp as String == 'DTSTAMP;TZID=Europe/Prague:20190104T155229\r\n'
    }

    def 'test dtstamp creation in default mode'() {
        given: 'relaxed parsing is disabled'
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, false)

        when: 'creating a dtstamp with a timezone'
        def dtstamp = new DtStamp.Factory().createProperty()
        dtstamp.parameters.add(new net.fortuna.ical4j.model.parameter.TzId('Europe/Prague'))
        dtstamp.timeZone = DefaultTimeZoneRegistryFactory.instance.createRegistry().getTimeZone('Europe/Prague')
        dtstamp.value = '20190104T155229'

        then: 'exception is thrown'
        thrown(UnsupportedOperationException)
    }
}
