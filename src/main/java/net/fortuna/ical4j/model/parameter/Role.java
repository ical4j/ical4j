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

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.util.Strings;

/**
 * $Id$ [18-Apr-2004]
 *
 * Defines a Participation Role parameter.
 * @author benfortuna
 */
public class Role extends Parameter {

    private static final long serialVersionUID = 1438225631470825963L;

    private static final String VALUE_CHAIR = "CHAIR";

    private static final String VALUE_REQ_PARTICIPANT = "REQ-PARTICIPANT";

    private static final String VALUE_OPT_PARTICIPANT = "OPT-PARTICIPANT";

    private static final String VALUE_NON_PARTICIPANT = "NON-PARTICIPANT";

    /**
     * Chair.
     */
    public static final Role CHAIR = new Role(VALUE_CHAIR);

    /**
     * Required participant.
     */
    public static final Role REQ_PARTICIPANT = new Role(VALUE_REQ_PARTICIPANT);

    /**
     * Optional participant.
     */
    public static final Role OPT_PARTICIPANT = new Role(VALUE_OPT_PARTICIPANT);

    /**
     * Non-participant.
     */
    public static final Role NON_PARTICIPANT = new Role(VALUE_NON_PARTICIPANT);

    private String value;

    /**
     * @param aValue a string representation of a participation role
     */
    public Role(final String aValue) {
        super(ROLE, ParameterFactoryImpl.getInstance());
        this.value = Strings.unquote(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }
}
