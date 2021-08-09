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
 * Defines a Relationship Type parameter.
 *
 * @author benfortuna
 */
public class RelType extends Parameter implements Encodable {

    private static final long serialVersionUID = 5346030888832899016L;

    private static final String VALUE_PARENT = "PARENT";

    private static final String VALUE_CHILD = "CHILD";

    private static final String VALUE_SIBLING = "SIBLING";

    private static final String VALUE_SNOOZE = "SNOOZE";

    /**
     * Parent.
     */
    public static final RelType PARENT = new RelType(VALUE_PARENT);

    /**
     * Child.
     */
    public static final RelType CHILD = new RelType(VALUE_CHILD);

    /**
     * Sibling.
     */
    public static final RelType SIBLING = new RelType(VALUE_SIBLING);

    /**
     * VALARM "Snooze".
     */
    public static final RelType SNOOZE = new RelType(VALUE_SNOOZE);

    private String value;

    /**
     * @param aValue a string representation of a relationship type
     */
    public RelType(final String aValue) {
        super(RELTYPE, new Factory());
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
            super(RELTYPE);
        }

        @Override
        public Parameter createParameter(final String value) throws URISyntaxException {
            RelType parameter = new RelType(value);
            if (RelType.PARENT.equals(parameter)) {
                parameter = RelType.PARENT;
            } else if (RelType.CHILD.equals(parameter)) {
                parameter = RelType.CHILD;
            } else if (RelType.SIBLING.equals(parameter)) {
                parameter = RelType.SIBLING;
            } else if (RelType.SNOOZE.equals(parameter)) {
                parameter = RelType.SNOOZE;
            }
            return parameter;
        }
    }
}
