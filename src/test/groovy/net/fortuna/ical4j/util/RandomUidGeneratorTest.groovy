package net.fortuna.ical4j.util

import spock.lang.Specification

class RandomUidGeneratorTest extends Specification {

    def 'verify randomness of generated UID'() {
        given: 'a random UID generator'
        RandomUidGenerator uidGenerator = []

        when: 'a list of UIDs is generated'
        def uids = (1..1000).collect {uidGenerator.generateUid()}

        then: 'none of the UIDs are equal'
        new HashSet<>(uids).size() == uids.size()
    }
}
