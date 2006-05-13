/*
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.DecoderFactory;
import net.fortuna.ical4j.util.EncoderFactory;
import net.fortuna.ical4j.util.ParameterValidator;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines an ATTACH iCalendar component property.
 * 
 * <pre>
 *   4.8.1.1 Attachment
 *   
 *      Property Name: ATTACH
 *   
 *      Purpose: The property provides the capability to associate a document
 *      object with a calendar component.
 *   
 *      Value Type: The default value type for this property is URI. The
 *      value type can also be set to BINARY to indicate inline binary
 *      encoded content information.
 *   
 *      Property Parameters: Non-standard, inline encoding, format type and
 *      value data type property parameters can be specified on this
 *      property.
 *    
 *      Conformance: The property can be specified in a "VEVENT", "VTODO",
 *      "VJOURNAL" or "VALARM" calendar components.
 *   
 *      Description: The property can be specified within "VEVENT", "VTODO",
 *      "VJOURNAL", or "VALARM" calendar components. This property can be
 *      specified multiple times within an iCalendar object.
 *   
 *      Format Definition: The property is defined by the following notation:
 *   
 *        attach     = "ATTACH" attparam ":" uri  CRLF
 *   
 *        attach     =/ "ATTACH" attparam ";" "ENCODING" "=" "BASE64"
 *                      ";" "VALUE" "=" "BINARY" ":" binary
 *   
 *        attparam   = *(
 *   
 *                   ; the following is optional,
 *                   ; but MUST NOT occur more than once
 *   
 *                   (";" fmttypeparam) /
 *   
 *                   ; the following is optional,
 *                   ; and MAY occur more than once
 *   
 *                   (";" xparam)
 *   
 *                   )
 * </pre>
 *
 * @author benf
 */
public class Attach extends Property {
    
    private static final long serialVersionUID = 4439949507756383452L;
    
    private static final Log LOG = LogFactory.getLog(Attach.class);

    private URI uri;

    private byte[] binary;

    /**
     * Default constructor.
     */
    public Attach() {
        super(ATTACH);
    }
    
    /**
     * @param aList
     *            a list of parameters for this component
     * @param aValue
     *            a value string for this component
     * @throws IOException
     *             when there is an error reading the binary stream
     * @throws URISyntaxException
     *             where the specified string is not a valid uri
     */
    public Attach(final ParameterList aList, final String aValue)
            throws IOException, URISyntaxException {
        super(ATTACH, aList);
        setValue(aValue);
    }

    /**
     * @param data
     *            binary data
     */
    public Attach(final byte[] data) {
        super(ATTACH);
        // add required parameters..
        getParameters().add(Encoding.BASE64);
        getParameters().add(Value.BINARY);
        this.binary = data;
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param data
     *            binary data
     */
    public Attach(final ParameterList aList, final byte[] data) {
        super(ATTACH, aList);
        this.binary = data;
    }

    /**
     * @param aUri
     *            a URI
     */
    public Attach(final URI aUri) {
        super(ATTACH);
        this.uri = aUri;
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param aUri
     *            a URI
     */
    public Attach(final ParameterList aList, final URI aUri) {
        super(ATTACH, aList);
        this.uri = aUri;
    }

    /**
     * @see net.fortuna.ical4j.model.Property#validate()
     */
    public final void validate() throws ValidationException {

        /*
         * ; the following is optional, ; but MUST NOT occur more than once
         *
         * (";" fmttypeparam) /
         */
        ParameterValidator.getInstance().assertOneOrLess(Parameter.FMTTYPE,
                getParameters());

        /*
         * ; the following is optional, ; and MAY occur more than once
         *
         * (";" xparam)
         */
        
        /*
         * If the value type parameter is ";VALUE=BINARY", then the inline
         * encoding parameter MUST be specified with the value
         * ";ENCODING=BASE64".
         */
        if (Value.BINARY.equals(getParameter(Parameter.VALUE))) {
            ParameterValidator.getInstance().assertOne(Parameter.ENCODING,
                    getParameters());
            if (!Encoding.BASE64.equals(getParameter(Parameter.ENCODING))) {
                throw new ValidationException(
                        "If the value type parameter is [BINARY], the inline"
                        + "encoding parameter MUST be specified with the value [BASE64]");
            }
        }
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
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public void setValue(final String aValue) throws IOException, URISyntaxException {
        // determine if ATTACH is a URI or an embedded
        // binary..
        if (getParameter(Parameter.ENCODING) != null) {
//            binary = Base64.decode(aValue);
            try {
                BinaryDecoder decoder = DecoderFactory.getInstance()
                    .createBinaryDecoder((Encoding) getParameter(Parameter.ENCODING)); 
                binary = decoder.decode(getBinary());
            }
            catch (UnsupportedEncodingException uee) {
                LOG.error("Error encoding binary data", uee);
            }
            catch (DecoderException de) {
                LOG.error("Error decoding binary data", de);
            }
        }
        // assume URI..
        else {
            uri = new URI(Uris.encode(aValue));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public final String getValue() {
        if (getUri() != null) {
            return Uris.decode(Strings.valueOf(getUri()));
        }
        else if (getBinary() != null) {
//          return Base64.encodeBytes(getBinary(), Base64.DONT_BREAK_LINES);
            try {
                BinaryEncoder encoder = EncoderFactory.getInstance()
                    .createBinaryEncoder((Encoding) getParameter(Parameter.ENCODING)); 
                return new String(encoder.encode(getBinary()));
            }
            catch (UnsupportedEncodingException uee) {
                LOG.error("Error encoding binary data", uee);
            }
            catch (EncoderException ee) {
                LOG.error("Error encoding binary data", ee);
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
}
