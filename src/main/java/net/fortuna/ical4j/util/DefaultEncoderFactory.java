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
package net.fortuna.ical4j.util;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import net.fortuna.ical4j.model.parameter.Encoding;

import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.QuotedPrintableCodec;

/**
 * Default encoder factory implementation.
 * 
 * $Id$
 *
 * Created on 13/05/2006
 *
 * @author Ben Fortuna
 */
public class DefaultEncoderFactory extends EncoderFactory {

    private static final String UNSUPPORTED_ENCODING_MESSAGE = "Encoder not available for encoding [{0}]";
    
    /**
     * {@inheritDoc}
     */
    public BinaryEncoder createBinaryEncoder(final Encoding encoding)
            throws UnsupportedEncodingException {

        if (Encoding.QUOTED_PRINTABLE.equals(encoding)) {
            return new QuotedPrintableCodec();
        }
        else if (Encoding.BASE64.equals(encoding)) {
            return new Base64();
        }
        throw new UnsupportedEncodingException(MessageFormat.format(UNSUPPORTED_ENCODING_MESSAGE,
                new Object[] {encoding}));
    }

    /**
     * {@inheritDoc}
     */
    public StringEncoder createStringEncoder(final Encoding encoding)
            throws UnsupportedEncodingException {

        if (Encoding.QUOTED_PRINTABLE.equals(encoding)) {
            return new QuotedPrintableCodec();
        }
        throw new UnsupportedEncodingException(MessageFormat.format(UNSUPPORTED_ENCODING_MESSAGE,
                new Object[] {encoding}));
    }
}
