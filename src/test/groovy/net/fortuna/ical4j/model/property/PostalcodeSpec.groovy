package net.fortuna.ical4j.model.property

import spock.lang.Specification

/**
 * Created by fortuna on 5/09/14.
 */
class PostalcodeSpec extends Specification {

    def 'verify factory instantiation'() {
        given: 'a postal code value'
        String value = '3056'

        when: 'a property is created using the factory'
        def postalCode = new Postalcode.Factory().createProperty(null, value)

        then: 'the property value matches the input value'
        postalCode.value == value
    }
}
