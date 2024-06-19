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

package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.parameter.Value;

import java.time.Instant;
import java.time.ZoneId;

public class TzUntil extends DateProperty<Instant> implements UtcProperty {

    public TzUntil() {
        super(TZUNTIL, new ParameterList(), CalendarDateFormat.UTC_DATE_TIME_FORMAT, Value.DATE_TIME);
    }

    public TzUntil(ParameterList parameters, String value) {
        super(TZUNTIL, parameters, CalendarDateFormat.UTC_DATE_TIME_FORMAT, Value.DATE_TIME);
        setValue(value);
    }

    @Override
    public void setTimeZoneRegistry(TimeZoneRegistry timeZoneRegistry) {
        UtcProperty.super.setTimeZoneRegistry(timeZoneRegistry);
    }

    @Override
    public void setDefaultTimeZone(ZoneId defaultTimeZone) {
        UtcProperty.super.setDefaultTimeZone(defaultTimeZone);
    }

    @Override
    protected PropertyFactory<TzUntil> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<TzUntil> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(TZUNTIL);
        }

        @Override
        public TzUntil createProperty(final ParameterList parameters, final String value) {
            return new TzUntil(parameters, value);
        }

        @Override
        public TzUntil createProperty() {
            return new TzUntil();
        }
    }
}
