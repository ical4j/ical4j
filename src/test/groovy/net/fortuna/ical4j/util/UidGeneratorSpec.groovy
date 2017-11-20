package net.fortuna.ical4j.util

import spock.lang.Specification

class UidGeneratorSpec extends Specification {

    def "verify host info is cached"() {
        given: 'a mock host info'
        def hostInfo = Mock(HostInfo)

        when: 'a generator is created and a uid is generated multiple times'
        UidGenerator generator = [hostInfo, '1']
        1..10.each { generator.generateUid() }

        then: 'HostInfo.getHostName() is only called once'
        1 * hostInfo.getHostName()
    }
}
