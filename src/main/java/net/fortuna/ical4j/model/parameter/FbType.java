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

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Encodable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

/**
 * $Id$ [18-Apr-2004]
 * <p/>
 * Defines a Free/Busy Time Type parameter.
 *
 * @author benfortuna
 */
public class FbType extends Parameter implements Encodable {

    private static final long serialVersionUID = -2217689716824679375L;

    private static final String VALUE_FREE = "FREE";

    private static final String VALUE_BUSY = "BUSY";

    private static final String VALUE_BUSY_UNAVAILABLE = "BUSY-UNAVAILABLE";

    private static final String VALUE_BUSY_TENTATIVE = "BUSY-TENTATIVE";

    /**
     * Free time.
     */
    public static final FbType FREE = new FbType(VALUE_FREE);

    /**
     * Busy time.
     */
    public static final FbType BUSY = new FbType(VALUE_BUSY);

    /**
     * Busy (unavailable) time.
     */
    public static final FbType BUSY_UNAVAILABLE = new FbType(
            VALUE_BUSY_UNAVAILABLE);

    /**
     * Busy (tentative) time.
     */
    public static final FbType BUSY_TENTATIVE = new FbType(VALUE_BUSY_TENTATIVE);

    private final String value;

    /**
     * @param aValue a string representation of a format type
     */
    public FbType(final String aValue) {
        super(FBTYPE);

        this.value = Strings.unquote(aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return value;
    }

    public static class Factory extends Content.Factory implements ParameterFactory<FbType> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(FBTYPE);
        }

        @Override
        public FbType createParameter(final String value) {
            switch (value.toUpperCase()) {
                case VALUE_BUSY: return BUSY;
                case VALUE_BUSY_TENTATIVE: return BUSY_TENTATIVE;
                case VALUE_BUSY_UNAVAILABLE: return BUSY_UNAVAILABLE;
                case VALUE_FREE: return FREE;
            }
            return new FbType(value);
        }
    }

}
