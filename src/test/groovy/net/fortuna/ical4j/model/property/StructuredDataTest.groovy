/*
 *  Copyright (c) 2022-2022, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ParameterList
import net.fortuna.ical4j.model.parameter.Schema
import net.fortuna.ical4j.model.parameter.Value
import net.fortuna.ical4j.validate.ValidationException
import spock.lang.Ignore
import spock.lang.Specification

class StructuredDataTest extends Specification {

    @Ignore
    def 'test validation of json-ld structured data'() {
        given: 'a structured data property with JSON-LD data'
        def structuredData = new StructuredData.Factory().createProperty([] as ParameterList, '''{
  "@context": "https://schema.org",
  "@type": "SportsEvent",
  "name": "2013 World Series",
  "subEvent": {
    "@type": "SportsEvent",
    "@id": "http://mlb.com/ws2013g1",
    "name": "2013 World Series - Game 1"
  }
}
''')

        when: 'validator is applied to target'
        structuredData.validate()

        then: 'validation succeeds'
        notThrown(ValidationException)
    }

    @Ignore('validation removed')
    def 'test validation of place structured data'() {
        given: 'a structured data property with JSON-LD data'
        ParameterList params = []
        params.add(Schema.SCHEMA_PLACE)
        def structuredData = new StructuredData.Factory().createProperty(params, '''{
  "@context": "https://schema.org",
  "@type": "LocalBusiness",
  "address": {
    "@type": "PostalAddress",
    "addressLocality": "Mexico Beach",
    "addressRegion": "FL",
    "streetAddress": "3102 Highway 98"
  },
  "description": "A superb collection of fine gifts and clothing to accent your stay in Mexico Beach.",
  "name": "Beachwalk Beachwear & Giftware",
  "telephone": "850-648-4200"
}
''')

        when: 'validator is applied to target'
        structuredData.validate()

        then: 'validation succeeds'
        notThrown(ValidationException)
    }

    @Ignore('validation removed')
    def 'test validation of invalid json-ld structured data'() {
        given: 'a structured data property with invalid JSON-LD data'
        def structuredData = new StructuredData.Factory().createProperty(
                [Value.TEXT, new Schema('http://example.com')] as ParameterList, '''{
  "@context": "https://schema.org",
  "@graph": "Invalid data",
  "@type": "SportsEvent",
  "name": "2013 World Series",
  "subEvent": {
    "@type": "SportsEvent",
    "@id": "http://mlb.com/ws2013g1",
    "name": "2013 World Series - Game 1"
  }
}
''')

        when: 'validator is applied to target'
        def result = structuredData.validate()

        then: 'validation fails'
        result.hasErrors()
    }

    def 'create structured data for inline vcard'() {
        given: 'a vcard object'
        def card = '''BEGIN:VCARD\r
FN:A Contact\r
CALADRURI:mailto:contact@example.com\r
END:VCARD\r\n'''

        when: 'structured data is created'
        def sd = new StructuredData(card.bytes)

        then: 'result matches expected'
        sd as String == 'STRUCTURED-DATA;VALUE=BINARY;ENCODING=BASE64:QkVHSU46VkNBUkQNCkZOOkEgQ29udGFjdA0KQ0FMQURSVVJJOm1haWx0bzpjb250YWN0QGV4YW1wbGUuY29tDQpFTkQ6VkNBUkQNCg==\r\n'
        
        and: 'internal bytes match expected'
        new String(sd.binary) == card
    }

    def 'create structured data for vcard ref'() {
        given: 'a vcard uri'
        def carduri = URI.create('https://example.com/vcardref.vcf')

        when: 'structured data is created'
        def sd = new StructuredData(carduri)

        then: 'result matches expected'
        sd as String == 'STRUCTURED-DATA;VALUE=URI:https://example.com/vcardref.vcf\r\n'
    }
}
