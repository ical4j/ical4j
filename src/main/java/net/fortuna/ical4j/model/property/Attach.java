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
import net.fortuna.ical4j.validate.ValidationResult;
import net.fortuna.ical4j.validate.property.DescriptivePropertyValidators;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Optional;

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
 * </pre>
 *
 * @see DescriptivePropertyValidators#ATTACH_URI
 * @author benf
 */
public class Attach extends Property {

    private static final long serialVersionUID = 4439949507756383452L;

    private static final Logger LOG = LoggerFactory.getLogger(Attach.class);

    private URI uri;

    private transient ByteBuffer binary;

    /**
     * Default constructor.
     */
    public Attach() {
        super(ATTACH);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Attach(final ParameterList aList, final String aValue) {
        super(ATTACH, aList);
        setValue(aValue);
    }

    /**
     * @param data binary data
     */
    public Attach(final ByteBuffer data) {
        this(new ParameterList(Arrays.asList(Encoding.BASE64, Value.BINARY)), data);
    }

    /**
     * @param aList a list of parameters for this component
     * @param data  binary data
     */
    public Attach(final ParameterList aList, final ByteBuffer data) {
        super(ATTACH, aList);
        this.binary = data;
        this.uri = null;
    }

    /**
     * @param aUri a URI
     */
    public Attach(final URI aUri) {
        super(ATTACH);
        this.uri = aUri;
        this.binary = null;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aUri  a URI
     */
    public Attach(final ParameterList aList, final URI aUri) {
        super(ATTACH, aList);
        this.uri = aUri;
        this.binary = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResult validate() throws ValidationException {
        if (Optional.of(Value.BINARY).equals(getParameter(Parameter.VALUE))) {
            return DescriptivePropertyValidators.ATTACH_BIN.validate(this);
        }
        return DescriptivePropertyValidators.ATTACH_URI.validate(this);
    }

    /**
     * @return Returns the binary.
     */
    public final ByteBuffer getBinary() {
        return binary;
    }

    /**
     * Returns the binary data as a byte array. If the binary data is backed by
     * an array, it returns that array directly. Otherwise, it creates a new
     * byte array and copies the data from the ByteBuffer.
     *
     * @return byte array containing the binary data, or null if no binary data is set
     */
    public byte[] getBinaryData() {
        if (binary != null) {
            if (binary.hasArray()) {
                return binary.array();
            } else {
                byte[] data = new byte[binary.remaining()];
                binary.get(data);
                binary.rewind(); // Reset position to the beginning after reading
                return data;
            }
        }
        return null;
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
     */
    @Override
    public final void setValue(final String aValue) {

        // determine if ATTACH is a URI or an embedded
        // binary..
        Optional<Encoding> encoding = getParameter(Parameter.ENCODING);
        if (encoding.isPresent()) {
            // binary = Base64.decode(aValue);
            try {
                final var decoder = DecoderFactory.getInstance().createBinaryDecoder(encoding.get());
                byte[] decodedBytes = decoder.decode(aValue.getBytes());
                try {
                    File binaryData = File.createTempFile("ical4j-attach", ".tmp");
                    binaryData.deleteOnExit();

                    try (RandomAccessFile raf = new RandomAccessFile(binaryData, "rw")) {
                        FileChannel fileChannel = raf.getChannel();
                        MappedByteBuffer binary = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, decodedBytes.length);
                        binary.put(decodedBytes);
                        binary.rewind();
                        this.binary = binary;
                    }
                } catch (IOException e) {
                    LOG.error("Error creating temporary file", e);
                    binary = ByteBuffer.wrap(decodedBytes);
                }
            } catch (UnsupportedEncodingException uee) {
                LOG.error("Error encoding binary data", uee);
            } catch (DecoderException de) {
                LOG.error("Error decoding binary data", de);
            }
        }
        // assume URI..
        else {
            try {
                uri = Uris.create(aValue);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
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
                Encoding encoding = getRequiredParameter(Parameter.ENCODING);
                final var encoder = EncoderFactory.getInstance().createBinaryEncoder(encoding);
                return new String(encoder.encode(getBinaryData()));
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
    public final void setBinary(final ByteBuffer binary) {
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

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        var data = getBinaryData();
        if (data != null) {
            out.writeInt(data.length);
            out.write(data);
        } else {
            out.writeInt(-1);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        int dataLength = in.readInt();
        if (dataLength >= 0) {
            var buffer = new byte[dataLength];
            in.readFully(buffer);
            this.binary = ByteBuffer.wrap(buffer);
        }
    }

    @Override
    protected PropertyFactory<Attach> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Attach> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(ATTACH);
        }

        @Override
        public Attach createProperty(final ParameterList parameters, final String value) {
            return new Attach(parameters, value);
        }

        @Override
        public Attach createProperty() {
            return new Attach();
        }
    }
}
