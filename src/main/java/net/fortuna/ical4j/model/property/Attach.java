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
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.property.AttachValidator;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines an ATTACH iCalendar component property.
 * <p/>
 * <pre>
 *       4.8.1.1 Attachment
 *
 *          Property Name: ATTACH
 *
 *          Purpose: The property provides the capability to associate a document
 *          object with a calendar component.
 *
 *          Value Type: The default value type for this property is URI. The
 *          value type can also be set to BINARY to indicate inline binary
 *          encoded content information.
 *
 *          Property Parameters: Non-standard, inline encoding, format type and
 *          value data type property parameters can be specified on this
 *          property.
 *
 *          Conformance: The property can be specified in a &quot;VEVENT&quot;, &quot;VTODO&quot;,
 *          &quot;VJOURNAL&quot; or &quot;VALARM&quot; calendar components.
 *
 *          Description: The property can be specified within &quot;VEVENT&quot;, &quot;VTODO&quot;,
 *          &quot;VJOURNAL&quot;, or &quot;VALARM&quot; calendar components. This property can be
 *          specified multiple times within an iCalendar object.
 *
 *          Format Definition: The property is defined by the following notation:
 *
 *            attach     = &quot;ATTACH&quot; attparam &quot;:&quot; uri  CRLF
 *
 *        attach     =/ &quot;ATTACH&quot; attparam &quot;;&quot; &quot;ENCODING&quot; &quot;=&quot; &quot;BASE64&quot;
 *                          &quot;;&quot; &quot;VALUE&quot; &quot;=&quot; &quot;BINARY&quot; &quot;:&quot; binary
 *
 *            attparam   = *(
 *
 *                       ; the following is optional,
 *                       ; but MUST NOT occur more than once
 *
 *                       (&quot;;&quot; fmttypeparam) /
 *
 *                       ; the following is optional,
 *                       ; and MAY occur more than once
 *
 *                       (&quot;;&quot; xparam)
 *
 *                       )
 * </pre>
 *
 * @author benf
 */
public class Attach extends Property {

    private static final long serialVersionUID = 4439949507756383452L;

    private URI uri;

    private byte[] binary;

    /**
     * Default constructor.
     */
    public Attach() {
        super(ATTACH, new Factory());
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     * @throws URISyntaxException where the specified string is not a valid uri
     */
    public Attach(final ParameterList aList, final String aValue)
            throws URISyntaxException {
        super(ATTACH, aList, new Factory());
        setValue(aValue);
    }

    /**
     * @param data binary data
     */
    public Attach(final byte[] data) {
        super(ATTACH, new Factory());
        // add required parameters..
        getParameters().add(Encoding.BASE64);
        getParameters().add(Value.BINARY);
        this.binary = data;
    }

    /**
     * @param aList a list of parameters for this component
     * @param data  binary data
     */
    public Attach(final ParameterList aList, final byte[] data) {
        super(ATTACH, aList, new Factory());
        this.binary = data;
    }

    /**
     * @param aUri a URI
     */
    public Attach(final URI aUri) {
        super(ATTACH, new Factory());
        this.uri = aUri;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aUri  a URI
     */
    public Attach(final ParameterList aList, final URI aUri) {
        super(ATTACH, aList, new Factory());
        this.uri = aUri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate() throws ValidationException {
        new AttachValidator().validate(this);
    }

    /**
     * @return Returns the binary.
     */
    public final byte[] getBinary() {
        return binary;
    }

    /**
     * @return Returns the uri.
     */
    public final URI getUri() {
        return uri;
    }

    /**
     * Sets the current value of the Attach instance. If the specified
     * value is encoded binary data, the value is decoded and stored in
     * the binary field. Otherwise the value is assumed to be a URI
     * location to binary data and is stored as such.
     *
     * @param aValue a string encoded binary or URI value
     * @throws URISyntaxException where the specified value is not a valid URI
     */
    @Override
    public final void setValue(final String aValue) throws
            URISyntaxException {

        // determine if ATTACH is a URI or an embedded
        // binary..
        if (getParameter(Parameter.ENCODING) != null) {
            // binary = Base64.decode(aValue);
            try {
                final BinaryDecoder decoder = DecoderFactory.getInstance()
                        .createBinaryDecoder(
                                getParameter(Parameter.ENCODING));
                binary = decoder.decode(aValue.getBytes());
            } catch (UnsupportedEncodingException uee) {
                Logger log = LoggerFactory.getLogger(Attach.class);
                log.error("Error encoding binary data", uee);
            } catch (DecoderException de) {
                Logger log = LoggerFactory.getLogger(Attach.class);
                log.error("Error decoding binary data", de);
            }
        }
        // assume URI..
        else {
            uri = Uris.create(aValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        if (getUri() != null) {
            return Uris.decode(Strings.valueOf(getUri()));
        } else if (getBinary() != null) {
            // return Base64.encodeBytes(getBinary(), Base64.DONT_BREAK_LINES);
            try {
                final BinaryEncoder encoder = EncoderFactory.getInstance()
                        .createBinaryEncoder(
                                getParameter(Parameter.ENCODING));
                return new String(encoder.encode(getBinary()));
            } catch (UnsupportedEncodingException | EncoderException uee) {
                Logger log = LoggerFactory.getLogger(Attach.class);
                log.error("Error encoding binary data", uee);
            }
        }
        return null;
    }

    /**
     * @param binary The binary to set.
     */
    public final void setBinary(final byte[] binary) {
        this.binary = binary;
        // unset uri..
        this.uri = null;
    }

    /**
     * @param uri The uri to set.
     */
    public final void setUri(final URI uri) {
        this.uri = uri;
        // unset binary..
        this.binary = null;
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Property> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(ATTACH);
        }

        @Override
        public Property createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Attach(parameters, value);
        }

        @Override
        public Property createProperty() {
            return new Attach();
        }
    }

}
