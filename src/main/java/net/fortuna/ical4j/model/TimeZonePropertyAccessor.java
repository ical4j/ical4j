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

import static net.fortuna.ical4j.model.Property.*;

public interface TimeZonePropertyAccessor extends PropertyContainer {


    /**
     * @return the mandatory timezone identifier property
     */
    default TzId getTimeZoneId() {
        return (TzId) getProperty(TZID).orElse(null);
    }

    /**
     * @return the optional last-modified property
     */
    default LastModified getLastModified() {
        return (LastModified) getProperty(LAST_MODIFIED).orElse(null);
    }

    /**
     * @return the optional timezone url property
     * @throws ConstraintViolationException if the property is not present
     */
    default TzUrl getTimeZoneUrl() {
        return (TzUrl) getProperty(TZURL).orElse(null);
    }

    /**
     *
     * @return
     * @throws ConstraintViolationException if the property is not present
     */
    default TzOffsetFrom getTimeZoneOffsetFrom() {
        return (TzOffsetFrom) getProperty(TZOFFSETFROM).orElse(null);
    }

    /**
     *
     * @return
     * @throws ConstraintViolationException if the property is not present
     */
    default TzOffsetTo getTimeZoneOffsetTo() {
        return (TzOffsetTo) getProperty(TZOFFSETTO).orElse(null);
    }

    /**
     * Returns the mandatory dtstart property.
     *
     * @return the DTSTART property or null if not specified
     * @throws ConstraintViolationException if the property is not present
     */
    default DtStart<LocalDateTime> getStartDate() {
        return (DtStart<LocalDateTime>) getProperty(DTSTART).orElse(null);
    }
}
