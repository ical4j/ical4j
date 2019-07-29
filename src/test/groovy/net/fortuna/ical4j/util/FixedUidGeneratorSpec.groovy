package net.fortuna.ical4j.util

import spock.lang.Specification

class FixedUidGeneratorSpec extends Specification {

    def "verify host info is cached"() {
        given: 'a mock host info'
        def hostInfo = Mock(HostInfo)

        when: 'a generator is created and a uid is generated multiple times'
        FixedUidGenerator generator = [hostInfo, '1']
        1..10.each { generator.generateUid() }

        then: 'HostInfo.getHostName() is only called once'
        1 * hostInfo.getHostName()
    }

    def "verify generated uid"() {
        given: 'a simple host info'
        def hostInfo = new SimpleHostInfo('test')

        when: 'a generator is created and a uid is generated multiple times'
        FixedUidGenerator generator = [hostInfo, '1']
        def uid = generator.generateUid()

        then: 'uid is as expected'
        uid.value.endsWith(hostInfo.hostName)
    }
}
