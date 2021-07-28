package net.fortuna.ical4j.model.component

import net.fortuna.ical4j.model.ContentBuilder
import spock.lang.Specification

class ParticipantFactoryTest extends Specification {

    def 'verify participant creation'() {
        given: 'a content builder'
        ContentBuilder builder = []

        when: 'a participant spec is provided'
        Participant participant = builder.participant() {
            uid '1'
            vlocation {
                uid '11'
            }
            vresource {
                uid '12'
            }
        }

        then: 'result is as expected'
        participant.properties.uid[0].value == '1'
        participant.components.size() == 2
        participant.components[0].properties.uid[0].value == '11'
        participant.components[1].properties.uid[0].value == '12'
    }
}
