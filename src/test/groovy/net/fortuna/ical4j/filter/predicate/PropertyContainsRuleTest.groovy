package net.fortuna.ical4j.filter.predicate

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.property.Categories
import spock.lang.Specification

class PropertyContainsRuleTest extends Specification {

    ContentBuilder contentBuilder = new ContentBuilder()

    def "test property contains rule"() {
        given: "a PropertyContainsRule with a specific property and value"
        def rule = new PropertyContainsRule(new Categories(), "work")

        when: "the rule is applied to a property"
        def result = rule.test(contentBuilder.vevent {
            categories("work, personal, urgent")
        })

        then: "the result should be true"
        result == true

        when: "the rule is applied to a different property"
        def result2 = rule.test(contentBuilder.vevent {
            categories("personal, urgent")
        })

        then: "the result should be false"
        result2 == false
    }
}
