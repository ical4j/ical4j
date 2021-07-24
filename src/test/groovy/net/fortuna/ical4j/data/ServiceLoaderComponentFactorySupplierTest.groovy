package net.fortuna.ical4j.data

import spock.lang.Specification

class ServiceLoaderComponentFactorySupplierTest extends Specification {

    def 'assert loaded factory count'() {
        given: 'a factory supplier'
        ServiceLoaderComponentFactorySupplier supplier = []

        when: 'factories are supplied'
        def factories = supplier.get()

        then: 'count matches expected'
        factories.size() == 14
    }
}
