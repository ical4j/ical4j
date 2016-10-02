/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <p>
 * o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p>
 * o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
 * o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p>
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

import net.fortuna.ical4j.model.parameter.Encoding;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.StringEncoder;

import java.io.UnsupportedEncodingException;

/**
 * Abstract base class for encoder factory implementations.
 *
 * $Id$
 *
 * Created on 13/05/2006
 *
 * @author Ben Fortuna
 */
public abstract class EncoderFactory {

  /**
   * The system property used to specify an alternate
   * <code>EncoderFactory</code> implementation.
   */
  public static final String KEY_FACTORY_CLASS = "net.fortuna.ical4j.factory.encoder";

  private static EncoderFactory instance;

  static {
    try {
      @SuppressWarnings("unchecked")
      final Class<EncoderFactory> factoryClass = (Class<EncoderFactory>) Class.forName(Configurator.getProperty(KEY_FACTORY_CLASS));
      instance = factoryClass.newInstance();
    } catch (Exception e) {
      instance = new DefaultEncoderFactory();
    }
  }

  /**
   * @return Returns the instance.
   */
  public static final EncoderFactory getInstance() {
    return instance;
  }

  /**
   * Returns a new {@link BinaryEncoder} for the specified encoding.
   * @param encoding an encoding type
   * @return a {@link BinaryEncoder} instance
   * @throws UnsupportedEncodingException where an encoder supporting the
   * specified encoding is not available.
   */
  public abstract BinaryEncoder createBinaryEncoder(Encoding encoding)
      throws UnsupportedEncodingException;

  /**
   * Returns a new {@link StringEncoder} for the specified encoding.
   * @param encoding an encoding type
   * @return a {@link StringEncoder} instance
   * @throws UnsupportedEncodingException where an encoder supporting the
   * specified encoding is not available.
   */
  public abstract StringEncoder createStringEncoder(Encoding encoding)
      throws UnsupportedEncodingException;
}
