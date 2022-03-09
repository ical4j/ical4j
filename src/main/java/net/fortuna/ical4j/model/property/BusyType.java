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
import net.fortuna.ical4j.validate.ValidationResult;

/**
 * $Id$
 *
 * Created: [Apr 6, 2004]
 *
 * Defines a BUSYTYPE iCalendar component property.
 *
 *    Format Definition:  This property is defined by the following
 *    notation:
 *
 *      busytype      = "BUSYTYPE" busytypeparam ":" busytypevalue CRLF
 *
 *      busytypeparam = *(";" xparam)
 *
 *      busytypevalue = "BUSY" / "BUSY-UNAVAILABLE" /
 *                      "BUSY-TENTATIVE" / iana-token / x-name
 *                      ; Default is "BUSY-UNAVAILABLE"
 *
 * @author Ben Fortuna
 * @author Mike Douglass
 */
public class BusyType extends Property {

	private static final long serialVersionUID = -5140360270562621159L;

    public static final String VALUE_BUSY = "BUSY";
    public static final String VALUE_BUSY_UNAVAILABLE = "BUSY-UNAVAILABLE";
    public static final String VALUE_BUSY_TENTATIVE = "BUSY-TENTATIVE";

	/**
	 * Constant for busy time.
	 */
	public static final BusyType BUSY = new ImmutableBusyType(VALUE_BUSY);

    /**
     * Constant for busy unavailable time.
     */
    public static final BusyType BUSY_UNAVAILABLE = new ImmutableBusyType(VALUE_BUSY_UNAVAILABLE);

    /**
     * Constant for tentatively busy time.
     */
    public static final BusyType BUSY_TENTATIVE = new ImmutableBusyType(VALUE_BUSY_TENTATIVE);

    /** An immutable instance of BusyType.
     *
     * @author Ben Fortuna
     * @author Mike Douglass
     */
    private static final class ImmutableBusyType extends BusyType implements ImmutableContent {

		private static final long serialVersionUID = -2454749569982470433L;

		/**
         * @param value
         */
        private ImmutableBusyType(final String value) {
            super(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setValue(final String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }

        @Override
        public ImmutableBusyType add(Parameter parameter) {
            throwException();
            return null;
        }

        @Override
        public ImmutableBusyType remove(Parameter parameter) {
            throwException();
            return null;
        }

        @Override
        public ImmutableBusyType removeAll(String... parameterName) {
            throwException();
            return null;
        }

        @Override
        public ImmutableBusyType replace(Parameter parameter) {
            throwException();
            return null;
        }
    }

    private String value;

    /**
     * Default constructor.
     */
    public BusyType() {
        super(BUSYTYPE);
    }

    /**
     * @param aValue a value string for this component
     */
    public BusyType(final String aValue) {
        super(BUSYTYPE);
        this.value = aValue;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public BusyType(final ParameterList aList, final String aValue) {
        super(BUSYTYPE, aList);
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final String aValue) {
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return value;
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return ValidationResult.EMPTY;
    }

    @Override
    protected PropertyFactory<BusyType> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<BusyType> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(BUSYTYPE);
        }

        @Override
        public BusyType createProperty(final ParameterList parameters, final String value) {

            if (parameters.getAll().isEmpty()) {
                switch (value) {
                    case VALUE_BUSY: return BUSY;
                    case VALUE_BUSY_UNAVAILABLE: return BUSY_UNAVAILABLE;
                    case VALUE_BUSY_TENTATIVE: return BUSY_TENTATIVE;
                }
            }
            return new BusyType(parameters, value);
        }

        @Override
        public BusyType createProperty() {
            return new BusyType();
        }
    }

}
