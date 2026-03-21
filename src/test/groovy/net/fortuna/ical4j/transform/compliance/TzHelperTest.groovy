/*
 *  Copyright (c) 2024, Ben Fortuna
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

package net.fortuna.ical4j.transform.compliance

import spock.lang.Specification

import java.nio.charset.StandardCharsets

class TzHelperTest extends Specification {

    def 'test retrieval of msTimezone alias'() {
        expect:
        TzHelper.getCorrectedTimeZoneIdFrom('W. Europe Standard Time') == 'Europe/Vienna'
    }

    def 'test Hawaiian timezone mapping'() {
        expect:
        // US/Hawaii is normalized to Pacific/Honolulu by the TimeZoneRegistry
        TzHelper.getCorrectedTimeZoneIdFrom('Hawaiian Standard Time') == 'Pacific/Honolulu'
    }

    def 'test W. Australia timezone mapping'() {
        expect:
        TzHelper.getCorrectedTimeZoneIdFrom('W. Australia Standard Time') == 'Australia/Perth'
    }

    def 'test Tonga timezone mapping'() {
        expect:
        TzHelper.getCorrectedTimeZoneIdFrom('Tonga Standard Time') == 'Pacific/Tongatapu'
    }

    def 'test Azores timezone mapping'() {
        expect:
        TzHelper.getCorrectedTimeZoneIdFrom('Azores Standard Time') == 'Atlantic/Azores'
    }

    def 'test Arabic timezone mapping'() {
        expect:
        TzHelper.getCorrectedTimeZoneIdFrom('Arabic Standard Time') == 'Asia/Baghdad'
    }

    def 'test Central Asia timezone mapping'() {
        expect:
        TzHelper.getCorrectedTimeZoneIdFrom('Central Asia Standard Time') == 'Asia/Almaty'
    }

    def 'test Mitteleuropaeische Zeit timezone mapping'() {
        expect:
        TzHelper.getCorrectedTimeZoneIdFrom('Mitteleuropäische Zeit') == 'Europe/Vienna'
    }

    def 'verify all MS timezone names resolve correctly'() {
        given: "Load all timezone names from msTimezoneNames file with UTF-8"
        def properties = new Properties()
        def inputStream = TzHelper.class.getResourceAsStream('msTimezoneNames')
        def reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        properties.load(reader)
        reader.close()
        inputStream.close()

        expect: "All MS timezone names should resolve to valid timezone IDs"
        properties.each { msName, expectedTzId ->
            def result = TzHelper.getCorrectedTimeZoneIdFrom(msName as String)
            assert result != null : "Failed to resolve MS timezone name: ${msName} (expected: ${expectedTzId})"
            println "✓ ${msName} -> ${result}"
        }
    }

    def 'verify all MS timezone IDs resolve correctly'() {
        given: "Load all timezone IDs from msTimezoneIds file with UTF-8"
        def properties = new Properties()
        def inputStream = TzHelper.class.getResourceAsStream('msTimezoneIds')
        def reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        properties.load(reader)
        reader.close()
        inputStream.close()

        expect: "All MS timezone IDs should resolve to valid timezone IDs"
        properties.each { msId, expectedTzId ->
            def result = TzHelper.getCorrectedTimeZoneIdFrom(msId as String)
            assert result != null : "Failed to resolve MS timezone ID: ${msId} (expected: ${expectedTzId})"
            println "✓ ${msId} -> ${result}"
        }
    }

    def 'verify all combined MS timezones resolve correctly'() {
        given: "Load all timezones from msTimezones file with UTF-8"
        def lines
        TzHelper.class.getResourceAsStream('msTimezones').withCloseable { inputStream ->
            new InputStreamReader(inputStream, StandardCharsets.UTF_8).withCloseable { reader ->
                lines = reader.readLines()
            }
        }
        def failedMappings = []

        when: "Parse and verify each timezone mapping"
        lines.each { line ->
            if (line.trim().isEmpty()) return

            def parts = line.split('=')
            if (parts.length != 2) return

            def expectedTzId = parts[1]
            def nameAndId = parts[0].split(';')

            if (nameAndId.length == 2) {
                def msName = nameAndId[0]
                def msDisplayId = nameAndId[1]

                // Test the MS name
                def nameResult = TzHelper.getCorrectedTimeZoneIdFrom(msName)
                if (nameResult == null) {
                    failedMappings << "MS Name '${msName}' failed (expected: ${expectedTzId})"
                } else {
                    println "✓ Name: ${msName} -> ${nameResult}"
                }

                // Test the display ID
                def idResult = TzHelper.getCorrectedTimeZoneIdFrom(msDisplayId)
                if (idResult == null) {
                    failedMappings << "MS Display ID '${msDisplayId}' failed (expected: ${expectedTzId})"
                } else {
                    println "✓ ID: ${msDisplayId} -> ${idResult}"
                }
            }
        }

        then: "No mappings should fail"
        failedMappings.isEmpty() || { println "Failed mappings:\n${failedMappings.join('\n')}"; false }()
    }
}
