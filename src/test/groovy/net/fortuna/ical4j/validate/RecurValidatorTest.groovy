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

import net.fortuna.ical4j.model.Recur
import spock.lang.Specification

class RecurValidatorTest extends Specification {

    def 'test validator for different recurrence rules'() {
        expect: 'rule validation matches expected result'
        ValidationResult result = new RecurValidator().validate(new Recur(rule))
        result.getEntries().size() == expectedResultCount

        where:
        rule                                    | expectedResultCount
        'FREQ=DAILY'                            | 0
        'FREQ=DAILY;BYWEEKNO=1'                 | 1
        'FREQ=DAILY;BYWEEKNO=3;BYYEARDAY=1'     | 2
        'FREQ=WEEKLY'                | 0
        'FREQ=WEEKLY;BYWEEKNO=3'                | 1
        'FREQ=WEEKLY;BYMONTHDAY=3'              | 1
        'FREQ=WEEKLY;BYYEARDAY=3'               | 1
        'FREQ=MONTHLY'               | 0
        'FREQ=MONTHLY;BYWEEKNO=3'               | 1
        'FREQ=MONTHLY;BYYEARDAY=3'               | 1
    }
}
