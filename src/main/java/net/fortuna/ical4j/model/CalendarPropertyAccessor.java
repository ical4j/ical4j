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

import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

/**
 * Convenience accessor methods for calendar properties. Note the default behaviour for these methods
 * is to throw an exception where an underlying property is not present.
 */
public interface CalendarPropertyAccessor extends PropertyContainer {

    /**
     * Returns the mandatory prodid property.
     * @return the PRODID property, or null if property doesn't exist
     * @throws ConstraintViolationException if the property is not present
     */
    default ProdId getProductId() {
        return (ProdId) getProperty(Property.PRODID).orElse(null);
    }

    /**
     * Returns the mandatory version property.
     * @return the VERSION property, or null if property doesn't exist
     * @throws ConstraintViolationException if the property is not present
     */
    default Version getVersion() {
        return (Version) getProperty(Property.VERSION).orElse(null);
    }

    /**
     * Returns the optional calscale property.
     * @return the CALSCALE property, or null if property doesn't exist
     * @throws ConstraintViolationException if the property is not present
     */
    default CalScale getCalendarScale() {
        return (CalScale) getProperty(Property.CALSCALE).orElse(null);
    }

    /**
     * Returns the optional method property.
     * @return the METHOD property, or null if property doesn't exist
     * @throws ConstraintViolationException if the property is not present
     */
    default Method getMethod() {
        return (Method) getProperty(Property.METHOD).orElse(null);
    }
}
