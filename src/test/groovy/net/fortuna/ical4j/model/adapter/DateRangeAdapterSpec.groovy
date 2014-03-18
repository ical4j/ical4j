package net.fortuna.ical4j.model.adapter

import net.fortuna.ical4j.model.DateTime
import net.fortuna.ical4j.model.Dur
import net.fortuna.ical4j.model.Period
import org.joda.time.Interval
import spock.lang.Specification

/**
 * Created by fortuna on 18/03/14.
 */
class DateRangeAdapterSpec extends Specification {
    def "test adapt period to joda time interval"() {
        setup:
        Period period = new Period(new DateTime(new Date()), new Dur('1H'))
        Interval result = JodaTimeAdapter.adapt(period, Interval)

        and:
        Date instant = []

        expect:
        result.toDurationMillis() == period.duration.getTime(instant).time - instant.time
    }
}
