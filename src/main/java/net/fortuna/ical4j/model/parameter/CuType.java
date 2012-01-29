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

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.util.Strings;

/**
 * $Id$ [18-Apr-2004]
 *
 * Defines a Calendar User Type.
 * @author benfortuna
 */
public class CuType extends Parameter {

    private static final long serialVersionUID = -3134064324693983052L;

    private static final String VALUE_INDIVIDUAL = "INDIVIDUAL";

    private static final String VALUE_GROUP = "GROUP";

    private static final String VALUE_RESOURCE = "RESOURCE";

    private static final String VALUE_ROOM = "ROOM";

    private static final String VALUE_UNKNOWN = "UNKNOWN";

    /**
     * Individual.
     */
    public static final CuType INDIVIDUAL = new CuType(VALUE_INDIVIDUAL);

    /**
     * Group.
     */
    public static final CuType GROUP = new CuType(VALUE_GROUP);

    /**
     * Resource.
     */
    public static final CuType RESOURCE = new CuType(VALUE_RESOURCE);

    /**
     * Room.
     */
    public static final CuType ROOM = new CuType(VALUE_ROOM);

    /**
     * Unknown.
     */
    public static final CuType UNKNOWN = new CuType(VALUE_UNKNOWN);

    private String value;

    /**
     * @param aValue a string representation of a Calendar User Type
     */
    public CuType(final String aValue) {
        super(CUTYPE, ParameterFactoryImpl.getInstance());
        this.value = Strings.unquote(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }
}
