/**
 * Copyright (c) 2012, Ben Fortuna
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
package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Encodable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

import java.net.URISyntaxException;

/**
 * $Id$ [18-Apr-2004]
 * <p/>
 * Defines a Participation Status parameter.
 *
 * @author benfortuna
 */
public class PartStat extends Parameter implements Encodable {

    private static final long serialVersionUID = -7856347127343842441L;

    private static final String VALUE_NEEDS_ACTION = "NEEDS-ACTION";

    private static final String VALUE_ACCEPTED = "ACCEPTED";

    private static final String VALUE_DECLINED = "DECLINED";

    private static final String VALUE_TENTATIVE = "TENTATIVE";

    private static final String VALUE_DELEGATED = "DELEGATED";

    private static final String VALUE_COMPLETED = "COMPLETED";

    private static final String VALUE_IN_PROCESS = "IN-PROCESS";

    /**
     * Needs action.
     */
    public static final PartStat NEEDS_ACTION = new PartStat(VALUE_NEEDS_ACTION);

    /**
     * Accepted.
     */
    public static final PartStat ACCEPTED = new PartStat(VALUE_ACCEPTED);

    /**
     * Declined.
     */
    public static final PartStat DECLINED = new PartStat(VALUE_DECLINED);

    /**
     * Tentative.
     */
    public static final PartStat TENTATIVE = new PartStat(VALUE_TENTATIVE);

    /**
     * Delegated.
     */
    public static final PartStat DELEGATED = new PartStat(VALUE_DELEGATED);

    /**
     * Completed.
     */
    public static final PartStat COMPLETED = new PartStat(VALUE_COMPLETED);

    /**
     * In-process.
     */
    public static final PartStat IN_PROCESS = new PartStat(VALUE_IN_PROCESS);

    private String value;

    /**
     * @param aValue a string representation of a participation status
     */
    public PartStat(final String aValue) {
        super(PARTSTAT, new Factory());
        this.value = Strings.unquote(aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return value;
    }

    public static class Factory extends Content.Factory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(PARTSTAT);
        }

        @Override
        public Parameter createParameter(final String value) throws URISyntaxException {
            PartStat parameter = new PartStat(value);
            if (PartStat.NEEDS_ACTION.equals(parameter)) {
                parameter = PartStat.NEEDS_ACTION;
            } else if (PartStat.ACCEPTED.equals(parameter)) {
                parameter = PartStat.ACCEPTED;
            } else if (PartStat.DECLINED.equals(parameter)) {
                parameter = PartStat.DECLINED;
            } else if (PartStat.TENTATIVE.equals(parameter)) {
                parameter = PartStat.TENTATIVE;
            } else if (PartStat.DELEGATED.equals(parameter)) {
                parameter = PartStat.DELEGATED;
            } else if (PartStat.COMPLETED.equals(parameter)) {
                parameter = PartStat.COMPLETED;
            } else if (PartStat.IN_PROCESS.equals(parameter)) {
                parameter = PartStat.IN_PROCESS;
            }
            return parameter;
        }
    }

}
