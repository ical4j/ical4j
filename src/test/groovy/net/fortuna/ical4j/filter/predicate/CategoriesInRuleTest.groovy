package net.fortuna.ical4j.filter.predicate

import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Categories
import net.fortuna.ical4j.util.Calendars
import spock.lang.Specification

import java.util.stream.Collectors

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

    def 'test categories not in rule'() {
        given: 'a calendar'
        def calendar = Calendars.load('src/test/resources/samples/valid/Buddhist.ics')

        and: 'a rule that checks for categories not present'
        def rule = new CategoriesInRule('personal')

        expect: 'the rule evaluates to false for the event'
        calendar.components.stream().filter(rule).collect(Collectors.toList()).empty
    }

    def 'test categories in rule'() {
        given: 'a calendar'
        def calendar = Calendars.load('src/test/resources/samples/valid/ArgentinaHolidays.ics')

        and: 'a rule that checks for categories not present'
        def rule = new CategoriesInRule('Public Holiday')

        expect: 'the rule evaluates to false for the event'
        !calendar.components.stream().filter(rule).collect(Collectors.toList()).empty
    }
}
