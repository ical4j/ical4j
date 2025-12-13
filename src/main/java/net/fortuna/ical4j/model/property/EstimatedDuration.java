package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.time.temporal.TemporalAmount;

/*
 * Copyright (c) 2025, Ben Fortuna
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
public class EstimatedDuration extends Property {

    private TemporalAmountAdapter duration;

    public EstimatedDuration() {
        super(ESTIMATED_DURATION);
    }

    public EstimatedDuration(ParameterList aList) {
        super(ESTIMATED_DURATION, aList);
    }

    public final TemporalAmount getDuration() {
        return duration.getDuration();
    }

    public final void setDuration(final TemporalAmount duration) {
        this.duration = new TemporalAmountAdapter(duration);
    }

    @Override
    public void setValue(String aValue) {
        duration = TemporalAmountAdapter.parse(aValue);
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return ValidationResult.EMPTY;
    }

    @Override
    protected PropertyFactory<?> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<EstimatedDuration> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(DURATION);
        }

        @Override
        public EstimatedDuration createProperty(final ParameterList parameters, final String value) {
            EstimatedDuration property = new EstimatedDuration(parameters);
            property.setValue(value);
            return property;
        }

        @Override
        public EstimatedDuration createProperty() {
            return new EstimatedDuration();
        }
    }

    @Override
    public String getValue() {
        return duration.toString();
    }
}
