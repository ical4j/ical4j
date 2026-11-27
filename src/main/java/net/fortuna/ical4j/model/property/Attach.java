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
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.DecoderFactory;
import net.fortuna.ical4j.util.EncoderFactory;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import net.fortuna.ical4j.validate.property.DescriptivePropertyValidators;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

    /**
     * Configuration key for the size in bytes from which decoded inline binary data is held in a memory-mapped
     * temporary file rather than on the heap. A negative value keeps all inline data on the heap.
     */
    public static final String KEY_MAPPED_THRESHOLD = "net.fortuna.ical4j.attach.mapped.threshold";

    /**
     * The default for {@link #KEY_MAPPED_THRESHOLD}: 1 MiB.
     */
    public static final int DEFAULT_MAPPED_THRESHOLD = 1024 * 1024;

    private @Nullable URI uri;

    private transient @Nullable ByteBuffer binary;

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
    public final @Nullable ByteBuffer getBinary() {
        return binary;
    }

    /**
     * Returns the binary data as a byte array. If the binary data is backed by
     * an array, it returns that array directly. Otherwise, it creates a new
     * byte array and copies the data from the ByteBuffer.
     *
     * @return byte array containing the binary data, or null if no binary data is set
     */
    public byte @Nullable [] getBinaryData() {
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
    public final @Nullable URI getUri() {
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
    /**
     * Holds decoded inline binary data. Data below {@link #KEY_MAPPED_THRESHOLD} is kept on the heap. Larger data is
     * copied to a memory-mapped temporary file, so it doesn't stay on the heap for as long as the property is
     * reachable, and the file is deleted as soon as it is mapped: the mapping stays valid, and the disk space is
     * freed when the buffer is garbage collected. A file system that can't delete a mapped file (e.g. on Windows)
     * falls back to deleting it on JVM exit. Decoding still needs the whole value on the heap, so the threshold
     * doesn't raise the largest attachment that can be parsed, only what is retained afterwards..
     */
    private static ByteBuffer buffer(final byte[] decodedBytes) {
        final int threshold = Configurator.getIntProperty(KEY_MAPPED_THRESHOLD).orElse(DEFAULT_MAPPED_THRESHOLD);
        if (threshold < 0 || decodedBytes.length < threshold) {
            return ByteBuffer.wrap(decodedBytes);
        }

        Path file = null;
        try {
            // created owner-only, unlike File.createTempFile which honours the umask
            file = Files.createTempFile("ical4j-attach", ".tmp");
            final MappedByteBuffer mapped;
            try (var channel = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
                mapped = channel.map(FileChannel.MapMode.READ_WRITE, 0, decodedBytes.length);
            }
            mapped.put(decodedBytes);
            mapped.rewind();
            deleteMapped(file);
            return mapped;
        } catch (IOException e) {
            LOG.warn("Unable to buffer binary data in a temporary file, keeping it in memory", e);
            if (file != null) {
                deleteMapped(file);
            }
            return ByteBuffer.wrap(decodedBytes);
        }
    }

    private static void deleteMapped(final Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // a mapped file can't be deleted on some platforms (e.g. Windows), so remove it on exit instead..
            LOG.debug("Unable to delete mapped temporary file {}, deleting on exit", file, e);
            file.toFile().deleteOnExit();
        }
    }

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
                binary = buffer(decodedBytes);
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
    @SuppressWarnings("NullAway") // unset/cleared value, see package-info
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
        out.writeObject(getBinaryData());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        try {
            var data = (byte[]) in.readObject();
            this.binary = (data != null) ? ByteBuffer.wrap(data) : null;
        } catch (OptionalDataException e) {
            // Backward compatibility: older serialized forms do not include the extra data appended in writeObject().
            if (!e.eof) {
                // An unexpected error occurred
                throw e;
            }
            this.binary = null;
        } catch (EOFException e) {
            // Backward compatibility: older serialized forms do not include the extra data appended in writeObject().
            this.binary = null;
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
