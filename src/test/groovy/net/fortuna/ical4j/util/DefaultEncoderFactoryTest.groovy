package net.fortuna.ical4j.util

import net.fortuna.ical4j.model.parameter.Encoding
import org.apache.commons.codec.BinaryEncoder
import org.apache.commons.codec.StringEncoder
import spock.lang.Specification

class DefaultEncoderFactoryTest extends Specification {

    DefaultEncoderFactory factory = []

    def "CreateBinaryEncoder"() {
        expect: 'a binary encoder'
        def encoder = factory.createBinaryEncoder(encoding)
        encoder instanceof BinaryEncoder

        where:
        encoding << [Encoding.BASE64, Encoding.QUOTED_PRINTABLE]
    }

    def "CreateStringEncoder"() {
        expect: 'a string encoder'
        def encoder = factory.createStringEncoder(encoding)
        encoder instanceof StringEncoder

        where:
        encoding << [Encoding.QUOTED_PRINTABLE]
    }

    def "assert invalid encoding"() {
        given: 'an invalid string encoding'
        def encoding = Encoding.BASE64

        when: 'attempt to create encoder'
        factory.createStringEncoder(encoding)

        then: 'invalid encoding exception'
        thrown(UnsupportedEncodingException)
    }
}
