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

package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.TemporalAmountAdapter;

import java.time.temporal.TemporalAmount;

/**
 * <pre>
 *     Purpose:
 *     This property specifies the length of the gap, positive or negative, between two components with a temporal relationship.
 * Format Definition:
 *
 *     This parameter is defined by the following notation, where dur-value is defined in Section 3.3.6 of [RFC5545]. :
 *
 *   gapparam      = "GAP" "=" dur-value
 *
 * Description:
 *
 *     This parameter MAY be specified on the RELATED-TO property and defines the duration of time between the predecessor and successor in an interval. When positive, it defines the lag time between a task and its logical successor. When negative, it defines the lead time.
 * </pre>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9253.html#name-gap">rfc9253</a>
 */
public class Gap extends Parameter {

    private static final String PARAM_NAME = "GAP";

    private TemporalAmountAdapter duration;

    public Gap(String value) {
        super(PARAM_NAME);
        duration = TemporalAmountAdapter.parse(value);
    }

    public Gap(TemporalAmount temporalAmount) {
        super(PARAM_NAME);
        this.duration = new TemporalAmountAdapter(temporalAmount);
    }

    public TemporalAmountAdapter getDuration() {
        return duration;
    }

    @Override
    public String getValue() {
        return duration.toString();
    }

    public static class Factory extends Content.Factory implements ParameterFactory<Gap> {

        public Factory() {
            super(PARAM_NAME);
        }

        @Override
        public Gap createParameter(String value) {
            return new Gap(value);
        }
    }
}
