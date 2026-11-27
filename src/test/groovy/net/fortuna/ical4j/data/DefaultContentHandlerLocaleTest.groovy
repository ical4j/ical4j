package net.fortuna.ical4j.data

import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.property.Uid
import spock.lang.Specification

import java.util.function.Consumer

/**
 * Property, parameter and component names are case-insensitive ASCII (RFC 5545 3.1), so name
 * matching must not depend on the default locale. Under a locale such as Turkish the default
 * folding of a name containing 'i'/'I' produces a dotted/undotted capital that no longer matches
 * the ASCII name tables, so a standard property is mis-resolved and the ignored-name filter misses.
 */
class DefaultContentHandlerLocaleTest extends Specification {

    Locale previous = Locale.getDefault()

    def cleanup() {
        Locale.setDefault(previous)
    }

    def 'a standard property resolves to its type under the turkish locale'() {
        given: 'the turkish default locale'
        Locale.setDefault(new Locale('tr'))

        and: 'a content handler'
        def result
        DefaultContentHandler handler = [{ result = it } as Consumer<Calendar>,
                                         TimeZoneRegistryFactory.instance.createRegistry()]

        when: 'a calendar carrying a lower case uid property is parsed'
        handler.startCalendar()
        handler.startComponent('vevent')
        handler.startProperty('uid')
        handler.propertyValue('uid-1@example.com')
        handler.endProperty('uid')
        handler.endComponent('vevent')
        handler.endCalendar()

        then: 'uid resolves to a Uid instance rather than an unrecognised property'
        result.getComponent('VEVENT').get().getProperty('UID').get() instanceof Uid
    }

    def 'an ignored property name is honoured under the turkish locale'() {
        given: 'the turkish default locale'
        Locale.setDefault(new Locale('tr'))

        and: 'a content handler that ignores UID'
        def result
        DefaultContentHandler handler = [{ result = it } as Consumer<Calendar>,
                                         TimeZoneRegistryFactory.instance.createRegistry(),
                                         new ContentHandlerContext().withIgnoredPropertyNames(['UID'])]

        when: 'a calendar carrying a lower case uid property is parsed'
        handler.startCalendar()
        handler.startComponent('vevent')
        handler.startProperty('uid')
        handler.propertyValue('uid-1@example.com')
        handler.endProperty('uid')
        handler.endComponent('vevent')
        handler.endCalendar()

        then: 'the ignored property is dropped'
        !result.getComponent('VEVENT').get().getProperty('UID').present
    }
}
