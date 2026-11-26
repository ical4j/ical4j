/*
 *  Copyright (c) 2026, Ben Fortuna
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

package net.fortuna.ical4j.data

import spock.lang.Specification

/**
 * RFC 5545 section 3.1.4: the default charset for an iCalendar object is UTF-8. Decoding must not follow the
 * JVM default charset, which on Java 11 and 17 is the host's native encoding.
 */
class CalendarParserCharsetTest extends Specification {

    /** Runs a JVM with the current test runtime classpath and returns [exitCode, combined output]. */
    static List fork(List<String> args) {
        def java = new File(System.getProperty('java.home'), 'bin/java').path
        def process = new ProcessBuilder([java, '-cp', System.getProperty('java.class.path')] + args)
                .redirectErrorStream(true).start()
        def output = process.inputStream.text
        [process.waitFor(), output]
    }

    def 'utf-8 content parses the same under a non-utf-8 default charset'() {
        when:
        def (exitCode, output) = fork(['-Dfile.encoding=ISO-8859-1', CalendarParserCharsetProbe.name])

        then:
        exitCode == 0

        and: 'SUMMARY:Café € reaches the content handler as six characters, not nine Latin-1 ones'
        output.contains('summary=00430061006600e9002020ac')

        and: 'an inline attachment decodes to the bytes the stream carried'
        output.contains('attach=636166c3a9')
    }
}
