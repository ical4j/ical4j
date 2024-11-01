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
import net.fortuna.ical4j.transform.recurrence.Frequency;
import net.fortuna.ical4j.validate.RecurValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.time.temporal.Temporal;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a EXRULE iCalendar component property.
 *
 * @author benf
 */
public class ExRule<T extends Temporal> extends Property {

    private static final long serialVersionUID = -9171193801247139294L;

    private Recur<T> recur;

    /**
     * Default constructor.
     */
    public ExRule() {
        super(EXRULE);
        recur = new Recur<>(Frequency.DAILY, 1);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     * @see Recur#Recur(String)
     */
    public ExRule(final ParameterList aList, final String aValue) {
        super(EXRULE, aList);
        setValue(aValue);
    }

    /**
     * @param aRecur a recurrence
     */
    public ExRule(final Recur<T> aRecur) {
        super(EXRULE);
        recur = aRecur;
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aRecur a recurrence
     */
    public ExRule(final ParameterList aList, final Recur<T> aRecur) {
        super(EXRULE, aList);
        recur = aRecur;
    }

    /**
     * @return Returns the recur.
     */
    public final Recur<T> getRecur() {
        return recur;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setValue(final String aValue) {
        recur = new Recur<>(aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return getRecur().toString();
    }

    /**
     * @param recur The recur to set.
     */
    public final void setRecur(final Recur<T> recur) {
        this.recur = recur;
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return new RecurValidator().validate(recur);
    }

    @Override
    protected PropertyFactory<ExRule<T>> newFactory() {
        return new Factory<>();
    }

    public static class Factory<T extends Temporal> extends Content.Factory implements PropertyFactory<ExRule<T>> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(EXRULE);
        }

        @Override
        public ExRule<T> createProperty(final ParameterList parameters, final String value) {
            return new ExRule<>(parameters, value);
        }

        @Override
        public ExRule<T> createProperty() {
            return new ExRule<>();
        }
    }

}
