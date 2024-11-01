/**
 * Copyright (c) 2023, Ben Fortuna
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
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.apache.commons.codec.DecoderException;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;

/**
 * See: <a href="https://www.rfc-editor.org/rfc/rfc6321.html#section-4.2">rfc6321</a>
 */
public class Xml extends Property implements Encodable {

    private String value;

    private byte[] binary;

    /**
     * Default constructor.
     */
    public Xml() {
        super(XML);
    }

    /**
     * @param aValue a value string for this component
     */
    public Xml(final String aValue) throws URISyntaxException {
        super(XML);
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Xml(final ParameterList aList, final String aValue) {
        super(XML, aList);
        setValue(aValue);
    }

    public Xml(byte[] binary) {
        super(XML, new ParameterList(Arrays.asList(Value.BINARY, Encoding.BASE64)));
        this.binary = binary;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        // value can be either binary or default to text
        if (getParameter(Parameter.ENCODING).isPresent()) {
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
        } else {
            value = aValue;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        if (Optional.of(Value.BINARY).equals(getParameter(Parameter.VALUE))) {
            return PropertyValidator.XML_BIN.validate(this);
        } else {
            return PropertyValidator.XML.validate(this);
        }
    }

    @Override
    protected PropertyFactory<?> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Xml> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(XML);
        }

        public Xml createProperty(final ParameterList parameters, final String value) {
            return new Xml(parameters, value);
        }

        public Xml createProperty() {
            return new Xml();
        }
    }

}
