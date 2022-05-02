/*
 *  Copyright (c) 2022, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.validate

import spock.lang.Specification

import static net.fortuna.ical4j.validate.ValidationReport.Format.HTML
import static net.fortuna.ical4j.validate.ValidationReport.Format.TEXT

class ValidationReportTest extends Specification {

    def 'generate empty text format report'() {
        given: 'a validation result'
        ValidationResult result = []

        when: 'a report is generated'
        StringWriter writer = []
        new ValidationReport(TEXT).output(result, writer)

        then: 'output matches expected'
        writer as String == ''
    }

    def 'generate text format report'() {
        given: 'a validation result'
        ValidationResult result = []
        result.entries << new ValidationEntry('Missing mandatory properties', ValidationEntry.Severity.ERROR,
                'VCALENDAR')

        when: 'a report is generated'
        StringWriter writer = []
        new ValidationReport(TEXT).output(result, writer)

        then: 'output matches expected'
        writer as String == 'VCALENDAR: ERROR - Missing mandatory properties\n'
    }

    def 'generate multiline text format report'() {
        given: 'a validation result'
        ValidationResult result = []
        result.entries << new ValidationEntry('Missing mandatory properties', ValidationEntry.Severity.ERROR,
                'VCALENDAR')
        result.entries << new ValidationEntry('Invalid parameter', ValidationEntry.Severity.WARNING,
                'DTSTART')

        when: 'a report is generated'
        StringWriter writer = []
        new ValidationReport(TEXT).output(result, writer)

        then: 'output matches expected'
        writer as String == 'DTSTART: WARNING - Invalid parameter\nVCALENDAR: ERROR - Missing mandatory properties\n'
    }

    def 'generate multiline html format report'() {
        given: 'a validation result'
        ValidationResult result = []
        result.entries << new ValidationEntry('Missing mandatory properties', ValidationEntry.Severity.ERROR,
                'VCALENDAR')
        result.entries << new ValidationEntry('Invalid parameter', ValidationEntry.Severity.WARNING,
                'DTSTART')

        when: 'a report is generated'
        StringWriter writer = []
        new ValidationReport(HTML).output(result, writer)

        then: 'output matches expected'
        writer as String == '<ol><li>DTSTART: WARNING - Invalid parameter</li>\n<li>VCALENDAR: ERROR - Missing mandatory properties</li>\n</ol>'
    }
}
