package net.fortuna.ical4j.filter.predicate

import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Resources
import spock.lang.Specification

class ResourcesInRuleTest extends Specification {

    def 'test resources in rule'() {
        given: 'a component with resources'
        def event = new VEvent()
        event.add(new Resources('room1'))
        event.add(new Resources('room2'))

        and: 'a rule that checks for specific resources'
        def rule = new ResourcesInRule('room1', 'room2')

        expect: 'the rule evaluates to true for the event'
        rule.test(event)
    }
}
