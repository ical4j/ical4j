package net.fortuna.ical4j.filter.predicate

import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Categories
import spock.lang.Specification

class CategoriesInRuleTest extends Specification {

    def 'test categories in rule'() {
        given: 'a component with categories'
        def event = new VEvent()
        event.add(new Categories('work'))
        event.add(new Categories('personal'))

        and: 'a rule that checks for specific categories'
        def rule = new CategoriesInRule('work', 'personal')

        expect: 'the rule evaluates to true for the event'
        rule.test(event)
    }
}
