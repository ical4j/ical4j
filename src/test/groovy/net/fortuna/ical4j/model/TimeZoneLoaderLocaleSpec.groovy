package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.component.VTimeZone
import spock.lang.Specification

import java.time.DayOfWeek
import java.time.ZoneId

/**
 * Generated VTIMEZONE content must use ASCII digits regardless of the default locale (#458).
 */
class TimeZoneLoaderLocaleSpec extends Specification {

    Locale previous = Locale.getDefault()

    def cleanup() {
        Locale.setDefault(previous)
    }

    def 'transition rule text uses ASCII digits under a native-digit locale'() {
        given: 'a default locale that formats numbers with non-Western digits'
        Locale.setDefault(Locale.forLanguageTag(languageTag))

        expect: 'the generated RRULE text is plain ASCII'
        TimeZoneLoader.transitionRuleText(3, -1, DayOfWeek.SUNDAY) == 'FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU'
        TimeZoneLoader.transitionRuleText(10, 1, DayOfWeek.SUNDAY) == 'FREQ=YEARLY;BYMONTH=10;BYDAY=1SU'

        where:
        languageTag << ['ar-EG-u-nu-arab', 'fa-IR', 'th-TH-u-nu-thai']
    }

    def 'generated transition rules are plain ASCII under a native-digit locale'() {
        given: 'a default locale that formats numbers with non-Western digits'
        Locale.setDefault(Locale.forLanguageTag('fa-IR'))

        and: 'a zone with recurring DST transition rules'
        VTimeZone timezone = new VTimeZone()

        when: 'transition rules are generated for the zone'
        TimeZoneLoader.addTransitionRules(ZoneId.of('Europe/London'), 0, timezone)

        then: 'every generated observance serialises as plain ASCII'
        !timezone.observances.isEmpty()
        timezone.observances.every { it.toString() ==~ /[\x20-\x7E\r\n]+/ }
    }
}
