package net.fortuna.ical4j.model.adapter

import net.fortuna.ical4j.model.Dur
import org.joda.time.Duration
import org.joda.time.Period
import spock.lang.Specification

/**
 * Created by fortuna on 18/03/14.
 */
class DurAdapterSpec extends Specification {

    def "test adapt dur to joda time duration"() {
        setup:
        Duration result = JodaTimeAdapter.adapt(new Dur("1H"), Duration)

        expect:
        result.toStandardHours().hours == 1
    }

    def "test adapt dur to joda time period"() {
        setup:
        Period result = JodaTimeAdapter.adapt(new Dur("1H"), Period)

        expect:
        result.toStandardHours().hours == 1
    }
}
