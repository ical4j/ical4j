package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.ContentBuilder
import spock.lang.Specification

/**
 * Created by fortuna on 7/03/15.
 */
class DtStartSpec extends Specification {

    def 'verify the use of the tzid param'() {
        given: 'a property string'
        new ContentBuilder().with {
            dtstart('20150321T193000') {
                tzid 'Australia/Lord_Howe'
            }
        } == 'DTSTART;TZID=Australia/Lord_Howe:20150321T193000\r\n'
    }
}
