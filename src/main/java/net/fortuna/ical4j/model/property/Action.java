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

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines an ACTION iCalendar component property.
 *
 * @author benf
 */
public class Action extends Property {

    private static final long serialVersionUID = -2353353838411753712L;

    /**
     * Constant actions for playing an audible sound.
     */
    public static final Action AUDIO;

    /**
     * Constant action for displaying a visible notification.
     */
    public static final Action DISPLAY;

    /**
     * Constant action for sending an email.
     */
    public static final Action EMAIL;

    /**
     * Constant action for a procedure.
     */
    public static final Action PROCEDURE;

    static {
        AUDIO = new ImmutableAction("AUDIO");
        DISPLAY = new ImmutableAction("DISPLAY");
        EMAIL = new ImmutableAction("EMAIL");
        PROCEDURE = new ImmutableAction("PROCEDURE");
    }

    /**
     * @author Ben Fortuna An immutable instance of Action.
     */
    private static final class ImmutableAction extends Action implements ImmutableContent {

        private static final long serialVersionUID = -2752235951243969905L;

        /**
         * @param value
         */
        private ImmutableAction(final String value) {
            super(value);
        }

        @Override
        public void add(Parameter parameter) {
            throwException();
        }

        @Override
        public void remove(Parameter parameter) {
            throwException();
        }

        /**
         * {@inheritDoc}
         */
        public void setValue(final String aValue) {
            throw new UnsupportedOperationException(
                    "Cannot modify constant instances");
        }

        @Override
        public void removeAll(String parameterName) {
            throwException();
        }

        @Override
        public void replace(Parameter parameter) {
            throwException();
        }

    }

    private String value;

    /**
     * Default constructor.
     */
    public Action() {
        super(ACTION);
    }

    /**
     * @param aValue a value string for this component
     */
    public Action(final String aValue) {
        super(ACTION);
        this.value = aValue;
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Action(final ParameterList aList, final String aValue) {
        super(ACTION, aList);
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

    @Override
    public void validate() throws ValidationException {

    }

    @Override
    protected PropertyFactory<Action> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Action> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(ACTION);
        }

        public Action createProperty(final ParameterList parameters, final String value) {
            Action action;
            if (AUDIO.getValue().equals(value)) {
                action = AUDIO;
            }
            else if (DISPLAY.getValue().equals(value)) {
                action = DISPLAY;
            }
            else if (EMAIL.getValue().equals(value)) {
                action = EMAIL;
            }
            else if (PROCEDURE.getValue().equals(value)) {
                action = PROCEDURE;
            } else {
                action = new Action(parameters, value);
            }
            return action;
        }

        public Action createProperty() {
            return new Action();
        }
    }
}
