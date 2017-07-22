package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Uid
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by fortuna on 21/07/2017.
 */
class ComponentGroupTest extends Specification {

    @Shared
    ContentBuilder builder

    Uid uid

    VEvent event, rev1

    def setupSpec() {
        builder = new ContentBuilder()
    }

    def setup() {
        uid = builder.uid('1')

        event = builder.vevent {
            uid(uid)
            dtstart('20101113', parameters: parameters() { value('DATE') })
            dtend('20101114', parameters: parameters() { value('DATE') })
            rrule('FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA')
        }

        rev1 = builder.vevent {
            uid(uid)
            sequence('1')
            dtstart('20101113', parameters: parameters() { value('DATE') })
            dtend('20101114', parameters: parameters() { value('DATE') })
            rrule('FREQ=WEEKLY;WKST=MO;INTERVAL=3;BYDAY=MO,TU,SA')
        }
    }

    def "GetRevisions"() {
        given: 'an event with 2 revisions'
        def components = new ComponentList<VEvent>([event, rev1])

        when: 'retrieving revisions from component group'
        def revisions = new ComponentGroup(components, uid).revisions

        then: 'the expected revisions are returned'
        revisions == [event, rev1]
    }

    def "CalculateRecurrenceSet"() {
    }
}
