package net.fortuna.ical4j.model.property

import spock.lang.Specification

import java.util.stream.Collectors

class CategoriesSpec extends Specification {

    def 'test merge categories'() {
        given: 'two Categories properties'
        def categories = [
                new Categories('work,personal'),
                new Categories('urgent,personal')
        ]

        when: 'the categories are merged'
        def mergedCategories = categories.stream().flatMap(c -> c.getCategories().getTexts().stream()).collect(Collectors.toSet())

        then: 'the merged categories should contain all unique values'
        mergedCategories == ['personal', 'urgent', 'work'] as Set
    }
}
