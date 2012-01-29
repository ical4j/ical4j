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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.ValidationException;

/**
 * $Id$
 *
 * Created: [Apr 6, 2004]
 *
 * Defines an ACTION iCalendar component property.
 * @author benf
 */
public class Action extends Property {

    private static final long serialVersionUID = -2353353838411753712L;

    /**
     * Constant action for playing an audible sound.
     */
    public static final Action AUDIO = new ImmutableAction("AUDIO");

    /**
     * Constant action for displaying a visible notification.
     */
    public static final Action DISPLAY = new ImmutableAction("DISPLAY");

    /**
     * Constant action for sending an email.
     */
    public static final Action EMAIL = new ImmutableAction("EMAIL");

    /**
     * Constant action for a procedure.
     */
    public static final Action PROCEDURE = new ImmutableAction("PROCEDURE");

    /**
     * @author Ben Fortuna An immutable instance of Action.
     */
    private static final class ImmutableAction extends Action {

        private static final long serialVersionUID = -2752235951243969905L;

        /**
         * @param value
         */
        private ImmutableAction(final String value) {
            super(new ParameterList(true), value);
        }

        /**
         * {@inheritDoc}
         */
        public void setValue(final String aValue) {
            throw new UnsupportedOperationException(
                    "Cannot modify constant instances");
        }
    }

    private String value;

    /**
     * Default constructor.
     */
    public Action() {
        super(ACTION, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aValue a value string for this component
     */
    public Action(final String aValue) {
        super(ACTION, PropertyFactoryImpl.getInstance());
        this.value = aValue;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Action(final ParameterList aList, final String aValue) {
        super(ACTION, aList, PropertyFactoryImpl.getInstance());
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(final String aValue) {
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
