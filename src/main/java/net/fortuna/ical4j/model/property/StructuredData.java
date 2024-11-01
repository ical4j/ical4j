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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.DecoderFactory;
import net.fortuna.ical4j.util.EncoderFactory;
import net.fortuna.ical4j.util.Uris;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import net.fortuna.ical4j.validate.schema.SchemaValidatorFactory;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static net.fortuna.ical4j.model.Parameter.SCHEMA;

/**
 * $Id$
 * <p/>
 * Created: [May 1 2017]
 * <p/>
 * Defines a STRUCTURED-LOCATION iCalendar component property.
 *
 * @author Mike Douglass
 */
public class StructuredData extends Property implements Encodable {

    private static final long serialVersionUID = 7287564228220558361L;

    private String value;

    private URI uri;

    private byte[] binary;

    /**
     * Default constructor.
     */
    public StructuredData() {
        super(STRUCTURED_DATA, new ParameterList());
    }

    /**
     * @param aValue a value string for this component
     */
    public StructuredData(final String aValue) {
        super(STRUCTURED_DATA, new ParameterList());
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public StructuredData(final ParameterList aList, final String aValue) {
        super(STRUCTURED_DATA, aList);
        setValue(aValue);
    }

    public StructuredData(URI uri) {
        super(STRUCTURED_DATA, new ParameterList(Collections.singletonList(Value.URI)));
        this.uri = uri;
    }

    public StructuredData(byte[] binary) {
        super(STRUCTURED_DATA, new ParameterList(Arrays.asList(Value.BINARY, Encoding.BASE64)));
        this.binary = binary;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        // value can be either binary or a URI or default to text
        if (!getParameters(Parameter.ENCODING).isEmpty()) {
            // binary = Base64.decode(aValue);
            try {
                final var decoder = DecoderFactory.getInstance()
                        .createBinaryDecoder(getRequiredParameter(Parameter.ENCODING));
                binary = decoder.decode(aValue.getBytes());
            } catch (UnsupportedEncodingException uee) {
                var log = LoggerFactory.getLogger(Attach.class);
                log.error("Error encoding binary data", uee);
            } catch (DecoderException de) {
                var log = LoggerFactory.getLogger(Attach.class);
                log.error("Error decoding binary data", de);
            }
        } else if (Value.URI.equals(getRequiredParameter(Parameter.VALUE))) {
            try {
                uri = Uris.create(aValue);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
            value = aValue;
            // assume text..
        } else {
            value = aValue;
        }
    }

    public URI getUri() {
        return uri;
    }

    public byte[] getBinary() {
        return binary;
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        Optional<Value> valueParam = getParameter(Parameter.VALUE);
        if (valueParam.isPresent()) {
            if (Value.URI.equals(valueParam.get())) {
                return uri.toString();
            } else if (Value.BINARY.equals(valueParam.get())) {
                try {
                    var encoder = EncoderFactory.getInstance().createBinaryEncoder(getRequiredParameter(Parameter.ENCODING));
                    return new String(encoder.encode(binary));
                } catch (UnsupportedEncodingException | EncoderException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return value;
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        var result = PropertyValidator.STRUCTURED_DATA.validate(this);

        result = result.merge(SchemaValidatorFactory.newInstance(getRequiredParameter(SCHEMA)).validate(this));
        return result;
    }

    @Override
    protected PropertyFactory<StructuredData> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<StructuredData> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(STRUCTURED_DATA);
        }

        public StructuredData createProperty(final ParameterList parameters, final String value) {
            return new StructuredData(parameters, value);
        }

        public StructuredData createProperty() {
            return new StructuredData();
        }
    }

}
