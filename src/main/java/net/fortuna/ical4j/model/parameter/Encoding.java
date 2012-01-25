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

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.util.Strings;

/**
 * $Id$ [18-Apr-2004]
 *
 * Defines an Inline Encoding parameter. Constants are provided for all encodings specified in <a
 * href="http://www.ietf.org/rfc/rfc2045.txt">RFC2045</a>.
 *
 * <pre>
 *  4.2.7 Inline Encoding
 *
 *     Parameter Name: ENCODING
 *
 *     Purpose: To specify an alternate inline encoding for the property
 *     value.
 *
 *     Format Definition: The property parameter is defined by the following
 *     notation:
 *
 *       encodingparam      = &quot;ENCODING&quot; &quot;=&quot;
 *                            (&quot;8BIT&quot;
 *          ; &quot;8bit&quot; text encoding is defined in [RFC 2045]
 *                          / &quot;BASE64&quot;
 *          ; &quot;BASE64&quot; binary encoding format is defined in [RFC 2045]
 *                          / iana-token
 *          ; Some other IANA registered iCalendar encoding type
 *                          / x-name)
 *          ; A non-standard, experimental encoding type
 *
 *     Description: The property parameter identifies the inline encoding
 *     used in a property value. The default encoding is &quot;8BIT&quot;,
 *     corresponding to a property value consisting of text. The &quot;BASE64&quot;
 *     encoding type corresponds to a property value encoded using the
 *     &quot;BASE64&quot; encoding defined in [RFC 2045].
 *
 *     If the value type parameter is &quot;;VALUE=BINARY&quot;, then the inline
 *     encoding parameter MUST be specified with the value
 *     &quot;;ENCODING=BASE64&quot;.
 *
 *     Example:
 *
 *       ATTACH;FMTYPE=IMAGE/JPEG;ENCODING=BASE64;VALUE=BINARY:MIICajC
 *        CAdOgAwIBAgICBEUwDQYJKoZIhvcNAQEEBQAwdzELMAkGA1UEBhMCVVMxLDA
 *        qBgNVBAoTI05ldHNjYXBlIENvbW11bmljYXRpb25zIENvcnBvcmF0aW9uMRw
 *        &lt;...remainder of &quot;BASE64&quot; encoded binary data...&gt;
 * </pre>
 *
 * @author Ben Fortuna
 */
public class Encoding extends Parameter {

    private static final long serialVersionUID = 7536336461076399077L;

    private static final String VALUE_SEVEN_BIT = "7BIT";

    private static final String VALUE_EIGHT_BIT = "8BIT";

    private static final String VALUE_BINARY = "BINARY";

    private static final String VALUE_QUOTED_PRINTABLE = "QUOTED-PRINTABLE";

    private static final String VALUE_BASE64 = "BASE64";

    /**
     * 7 bit encoding.
     */
    public static final Encoding SEVEN_BIT = new Encoding(VALUE_SEVEN_BIT);

    /**
     * 8 bit encoding.
     */
    public static final Encoding EIGHT_BIT = new Encoding(VALUE_EIGHT_BIT);

    /**
     * Binary encoding.
     */
    public static final Encoding BINARY = new Encoding(VALUE_BINARY);

    /**
     * Quoted printable encoding.
     */
    public static final Encoding QUOTED_PRINTABLE = new Encoding(
            VALUE_QUOTED_PRINTABLE);

    /**
     * Base64 encoding.
     */
    public static final Encoding BASE64 = new Encoding(VALUE_BASE64);

    private String value;

    /**
     * @param aValue a string representation of an Inline Encoding
     */
    public Encoding(final String aValue) {
        super(ENCODING, ParameterFactoryImpl.getInstance());
        this.value = Strings.unquote(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }
}
