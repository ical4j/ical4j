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
package net.fortuna.ical4j.model;

import java.io.Serializable;

/**
 * A factory for creating iCalendar properties.
 *
 * @author Ben Fortuna
 *         <p/>
 *         Note that implementations must be {@link Serializable} to support referencing
 *         from {@link Property} instances.
 *         <p/>
 *         $Id$
 *         <p/>
 *         Created on 16/06/2005
 */
public interface PropertyFactory<T extends Property> extends Serializable {

    /**
     * @return a new instance of the specified property
     */
    T createProperty();

    /**
     * Creates a property instance with no parameters.
     * @param value the property value
     * @return a new property instance
     * @throws IllegalArgumentException some properties may throw this exception when parsing the property value
     */
    default T createProperty(String value) {
        return createProperty(new ParameterList(), value);
    }

    /**
     * @param parameters a list of property parameters
     * @param value      a property value
     * @return a new instance of the specified property
     * @throws IllegalArgumentException where data contains an invalid URI
     */
    T createProperty(ParameterList parameters, String value);

    boolean supports(String name);
}
