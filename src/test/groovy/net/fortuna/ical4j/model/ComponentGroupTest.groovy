package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtStamp
import net.fortuna.ical4j.model.property.Uid
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by fortuna on 21/07/2017.
 */
class ComponentGroupTest extends Specification {

    @Shared
    ContentBuilder builder = []

    Uid uid

    VEvent event, rev1, rev2

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

        rev2 = builder.vevent {
            uid(uid)
            sequence('1')
            dtstamp(new DtStamp(new DateTime()))
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

    def "GetLatestRevision"() {
        given: 'an event with 3 revisions'
        def components = new ComponentList<VEvent>([event, rev1, rev2])

        when: 'retrieving the latest revision from component group'
        def revision = new ComponentGroup(components, uid).latestRevision

        then: 'the expected revision is returned'
        revision == rev2
    }

    def "CalculateRecurrenceSet"() {
        given: 'an event with 2 revisions'
        def components = new ComponentList<VEvent>([event, rev1])

        when: 'recurrence instances are calculated'
        Period period = ['20101113T120000/P3W']
        def recurrences = new ComponentGroup(components, uid).calculateRecurrenceSet(period)

        then: 'the expected number of recurrences are returned'
        recurrences == event.calculateRecurrenceSet(period).normalise()
    }
}
