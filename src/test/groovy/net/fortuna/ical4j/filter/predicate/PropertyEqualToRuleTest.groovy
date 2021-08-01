/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.fortuna.ical4j.filter.predicate

import net.fortuna.ical4j.model.ContentBuilder
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Attendee
import net.fortuna.ical4j.model.property.Organizer
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by fortuna on 24/07/2017.
 */
class PropertyEqualToRuleTest extends Specification {

    @Shared
    ContentBuilder builder

    @Shared
    Organizer organiser

    @Shared
    Attendee attendee

    def setupSpec() {
        builder = new ContentBuilder()
        organiser = builder.organizer('Mailto:B@example.com')
        attendee = builder.attendee('Mailto:A@example.com')
    }

    def "Evaluate"() {
        given: 'a component with two properties'
        def event = builder.vevent {
            organizer(organiser)
            attendee(attendee)
        }

        expect: 'a property rule matches when applied'
        rule.test(event)

        where:
        rule << [new PropertyEqualToRule<VEvent, String>(organiser), new PropertyEqualToRule<VEvent, String>(attendee)]
    }
}
