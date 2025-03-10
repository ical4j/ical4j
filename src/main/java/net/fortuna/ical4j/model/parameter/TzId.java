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

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.Strings;

import java.time.ZoneId;
import java.time.DateTimeException;

/**
 * $Id$ [18-Apr-2004]
 *
 * Defines a Time Zone Identifier parameter.
 * @author benfortuna
 */
public class TzId extends Parameter implements Encodable {

    private static final long serialVersionUID = 2366516258055857879L;

    /**
     * Timezone identifier prefix.
     */
    public static final String PREFIX = "/";

    private final String value;

    /**
     * @param aValue a string representation of a time zone identifier
     */
    public TzId(final String aValue) {
        super(TZID);
        // parameter values may be quoted if they contain characters in the
        // set [:;,]..
        this.value = Strings.unquote(aValue);
    }

    /**
     * Provides a {@link ZoneId} representation of this instance. If created with a local timezone registry the
     * returned value will provide the corresponding globally unique value.
     *
     * @return a zone id represented by this instance
     */
    public ZoneId toZoneId() {
        return toZoneId(null);
    }

    /**
     *
     * @param timeZoneRegistry
     * @return
     */
    public ZoneId toZoneId(TimeZoneRegistry timeZoneRegistry) {
        if (timeZoneRegistry != null && !timeZoneRegistry.getZoneRules().isEmpty()) {
            try {
                return timeZoneRegistry.getZoneId(getValue());
            } catch (DateTimeException e) {
                // fails with "Unknown timezone identifier" if tzid not in VTIMEZONE
                // fall through to try GlobalZone instead
            }
        }
        return TimeZoneRegistry.getGlobalZoneId(getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return value;
    }

    public static class Factory extends Content.Factory implements ParameterFactory<TzId> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(TZID);
        }

        @Override
        public TzId createParameter(final String value) {
            return new TzId(Strings.unescape(value));
        }
    }
}
