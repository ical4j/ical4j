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
        participant.getProperty('UID').value == '1'
        participant.locations.size() == 1
        participant.locations[0].getProperty('UID').value == '11'
        participant.resources.size() == 1
        participant.resources[0].getProperty('UID').value == '12'
    }
}
