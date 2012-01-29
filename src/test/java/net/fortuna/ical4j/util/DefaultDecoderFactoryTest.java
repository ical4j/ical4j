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

import junit.framework.TestCase;
import net.fortuna.ical4j.model.parameter.Encoding;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$
 *
 * Created on 03/10/2006
 *
 * Unit tests for {@link DefaultDecoderFactory}.
 * @author Ben Fortuna
 */
public class DefaultDecoderFactoryTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(
            DefaultDecoderFactoryTest.class);
    
    private DefaultDecoderFactory factory = new DefaultDecoderFactory();
    
    /**
     * Test creation of binary decoder.
     * @throws UnsupportedEncodingException
     * @throws DecoderException
     */
    public void testCreateBinaryDecoder() throws UnsupportedEncodingException,
        DecoderException {
        
        BinaryDecoder decoder = factory.createBinaryDecoder(
                Encoding.QUOTED_PRINTABLE);
        
        assertNotNull(decoder);
        
        String encoded = "=C3=A0 la maison";
        
        String decoded = (String) decoder.decode(encoded);
        
        assertNotNull(decoded);
        
        LOG.info("Decoded: [" + decoded + "]");
        
        try {
            factory.createBinaryDecoder(new Encoding("9-BIT"));
            fail("Should throw UnsupportedEncodingException");
        }
        catch (UnsupportedEncodingException uee) {
            LOG.debug("Caught exception: " + uee.getMessage());
        }
    }

    /**
     * Test creation of string decoder.
     * @throws UnsupportedEncodingException
     * @throws DecoderException
     */
    public void testCreateStringDecoder()  throws UnsupportedEncodingException,
        DecoderException {
        
        StringDecoder decoder = factory.createStringDecoder(
                Encoding.QUOTED_PRINTABLE);
        
        assertNotNull(decoder);
        
        String encoded = "=C3=A0 la maison";
        
        String decoded = (String) decoder.decode(encoded);
        
        assertNotNull(decoded);
        
        LOG.info("Decoded: [" + decoded + "]");
        
        try {
            factory.createStringDecoder(new Encoding("9-BIT"));
            fail("Should throw UnsupportedEncodingException");
        }
        catch (UnsupportedEncodingException uee) {
            LOG.debug("Caught exception: " + uee.getMessage());
        }
    }

}
