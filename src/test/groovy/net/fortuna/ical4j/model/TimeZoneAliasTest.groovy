package net.fortuna.ical4j.model


import spock.lang.Specification

class TimeZoneAliasTest extends Specification {

    def 'assert alias lookup'() {
        given: 'a list of tz alias instances'
        List<TimeZoneAlias> aliases = Arrays.asList(
                new TimeZoneAlias('America/New_York',
                        'US/Eastern', 'U.S. Eastern Standard Time'))

        when: 'looking up an alias timezone id'
        Optional<String> alias = TimeZoneAlias.getTimeZoneIdFromAlias(aliases, 'US/Eastern')

        then: 'the expected match is returned'
        alias.get() == aliases.iterator().next().timeZoneId
    }

    def 'assert unmatched alias lookup'() {
        given: 'a list of tz alias instances'
        List<TimeZoneAlias> aliases = Arrays.asList(
                new TimeZoneAlias('America/New_York',
                        'US/Eastern', 'U.S. Eastern Standard Time'))

        when: 'looking up a non-existent alias timezone id'
        Optional<String> alias = TimeZoneAlias.getTimeZoneIdFromAlias(aliases, 'US/Central')

        then: 'no match is returned'
        !alias.present
    }
}
