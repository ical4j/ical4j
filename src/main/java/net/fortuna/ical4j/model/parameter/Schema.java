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
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * $Id$ [18-Apr-2004]
 * <p/>
 * Provides identifying information about the nature of the content of the
 * corresponding "STRUCTURED-DATA" property value. .
 *
 * @author Mike Douglass
 */
public class Schema extends Parameter {

    private static final long serialVersionUID = -8581904779721020689L;

    public static final Schema SCHEMA_ACTION = new Schema(URI.create("https://schema.org/Action"));

    public static final Schema SCHEMA_PLACE = new Schema(URI.create("https://schema.org/Place"));

    public static final Schema SCHEMA_THING = new Schema(URI.create("https://schema.org/Thing"));

    public static final Schema SCHEMA_EVENT = new Schema(URI.create("https://schema.org/Event"));

    public static final Schema SCHEMA_PERSON = new Schema(URI.create("https://schema.org/Person"));

    private final URI uri;

    /**
     * @param aValue a string representation of a schema uri
     * @throws IllegalArgumentException when the specified string is not a valid (quoted) uri
     */
    public Schema(final String aValue) {
        super(SCHEMA);
        try {
            this.uri = Uris.create(Strings.unquote(aValue));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * @param aUri a URI reference to a schema
     */
    public Schema(final URI aUri) {
        super(SCHEMA);
        this.uri = aUri;
    }

    /**
     * @return Returns the uri.
     */
    public final URI getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return Uris.decode(Strings.valueOf(getUri()));
    }

    public static class Factory extends Content.Factory implements ParameterFactory<Schema> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(SCHEMA);
        }

        public Schema createParameter(final String value) {
            return new Schema(value);
        }
    }

}
