/*
 *  Copyright (c) 2024, Ben Fortuna
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

package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.property.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static net.fortuna.ical4j.model.Property.*;

public interface TimeZonePropertyAccessor extends PropertyContainer {


    /**
     * @return the mandatory timezone identifier property
     */
    default Optional<TzId> getTimeZoneId() {
        return getProperty(TZID);
    }

    /**
     * @return the optional last-modified property
     */
    default Optional<LastModified> getLastModified() {
        return getProperty(LAST_MODIFIED);
    }

    /**
     * @return the optional timezone url property
     */
    default Optional<TzUrl> getTimeZoneUrl() {
        return getProperty(TZURL);
    }

    default Optional<TzOffsetFrom> getTimeZoneOffsetFrom() {
        return getProperty(TZOFFSETFROM);
    }

    default Optional<TzOffsetTo> getTimeZoneOffsetTo() {
        return getProperty(TZOFFSETTO);
    }

    /**
     * Returns the mandatory dtstart property.
     *
     * @return the DTSTART property or null if not specified
     */
    default Optional<DtStart<LocalDateTime>> getStartDate() {
        return getProperty(DTSTART);
    }
}
