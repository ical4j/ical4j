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
 * Defines the scheduling agent for CalDAV scheduling.
 * @author Mike Douglass
 */
public class ScheduleAgent extends Parameter {

    //private static final long serialVersionUID = -3134064324693983052L;

    private static final String VALUE_SERVER = "SERVER";

    private static final String VALUE_CLIENT = "CLIENT";

    private static final String VALUE_NONE = "NONE";

    public static final ScheduleAgent SERVER = new ScheduleAgent(VALUE_SERVER);

    public static final ScheduleAgent CLIENT = new ScheduleAgent(VALUE_CLIENT);

    public static final ScheduleAgent NONE = new ScheduleAgent(VALUE_NONE);

    private String value;

    /**
     * @param aValue a string representation of a scheduling agent
     */
    public ScheduleAgent(final String aValue) {
        super(SCHEDULE_AGENT, ParameterFactoryImpl.getInstance());
        this.value = Strings.unquote(aValue);
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Parameter#getValue()
     */
    public final String getValue() {
        return value;
    }
}
