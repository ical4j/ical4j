package net.fortuna.ical4j.model

import spock.lang.Specification

class ParameterCodecTest extends Specification {

    def 'verify value encoding'() {
        given: 'a codec instance'
        ParameterCodec codec = []

        expect: 'encoded value is as expected'
        codec.encode(value) == expectedEncodedValue

        where:
        value                                       | expectedEncodedValue
        ''                                          | ''
        '^'                                         | '^^'
        '^^'                                        | '^^^^'
        '\n'                                        | '^n'
        '"'                                         | "^'"
        'This is ^a \n"test"'                       | "This is ^^a ^n^'test^'"
        'test: 1'                                   | '"test: 1"'
    }

    def 'verify value decoding'() {
        given: 'a codec instance'
        ParameterCodec codec = []

        expect: 'decoded value is as expected'
        codec.decode(value) == expectedDecodedValue

        where:
        value                                       | expectedDecodedValue
        ''                                          | ''
        '^^'                                        | '^'
        '^^^^'                                      | '^^'
        '^n'                                        | '\n'
        "^'"                                        | '"'
        "This is ^^a ^n^'test^'"                    | 'This is ^a \n"test"'
        '"test: 1"'                                 | 'test: 1'
        '"test@example.com","test2@example.com"'    | '"test@example.com","test2@example.com"'
    }
}
