/**
 * Copyright (c) 2004-2021, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.filter

import net.fortuna.ical4j.filter.expression.NumberExpression
import net.fortuna.ical4j.filter.expression.TargetExpression
import org.jparsec.Parser
import spock.lang.Specification

class FilterExpressionParserTest extends Specification {

    def 'test expression parsing'() {
        given: 'an expression parser instance'
        Parser parser = FilterExpressionParser.newInstance()

        expect: 'a parsed result'
        parser.parse(expression) == expectedResult

        where:
        expression                                          | expectedResult
        '1'                                                 | new NumberExpression('1')
        'due'                                               | new TargetExpression('due')
        "due = 12"                                          | FilterExpression.equalTo('due', 12)
        "related_to = '1234-1234-1234'"                     | FilterExpression.equalTo("related_to", '1234-1234-1234')
        "related_to[rel_type:SIBLING] = '1234-1234-1234'"   | FilterExpression.equalTo("related_to", Collections.singletonList(new FilterTarget.Attribute("rel_type", "SIBLING")), '1234-1234-1234')
        "attendee[role:CHAIR] = '1234-1234-1234'"           | FilterExpression.equalTo("attendee", Collections.singletonList(new FilterTarget.Attribute("role", "CHAIR")), '1234-1234-1234')
    }
}
