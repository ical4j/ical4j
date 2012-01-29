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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * $Id$
 *
 * Created on 11/09/2005
 *
 * Utility methods for working with URIs.
 * @author Ben Fortuna
 */
public final class Uris {

    // private static final String ENCODING_CHARSET = "UTF-8";

    // private static Log log = LogFactory.getLog(Uris.class);

    /**
     * Constructor made private to enforce static nature.
     */
    private Uris() {
    }

    /**
     * Encodes the specified URI string using the UTF-8 charset. In the event that an exception is thrown, the specifed
     * URI string is returned unmodified.
     * @param s a URI string
     * @return an encoded URI string
     */
    public static String encode(final String s) {
        /*
         * try { return URLEncoder.encode(s, ENCODING_CHARSET); } catch (UnsupportedEncodingException use) {
         * log.error("Error ocurred encoding URI [" + s + "]", use); }
         */

        /*
         * Lotus Notes does not correctly strip angle brackets from cid uris. From RFC2392: A "cid" URL is converted to
         * the corresponding Content-ID message header [MIME] by removing the "cid:" prefix, converting the % encoded
         * character to their equivalent US-ASCII characters, and enclosing the remaining parts with an angle bracket
         * pair, "<" and ">". For example, "cid:foo4%25foo1@bar.net" corresponds to Content-ID: <foo4%25foo1@bar.net>
         * Reversing the process and converting URL special characters to their % encodings produces the original cid. A
         * "mid" URL is converted to a Message-ID or Message-ID/Content-ID pair in a similar fashion.
         */
        if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_NOTES_COMPATIBILITY)
        		&& s.matches("(?i)^cid:.*")) {
        	
            return s.replaceAll("[<>]", "");
        }
        return s;
    }

    /**
     * Decodes the specified URI string using the UTF-8 charset. In the event that an exception is thrown, the specifed
     * URI string is returned unmodified.
     * @param s a URI string
     * @return an encoded URI string
     */
    public static String decode(final String s) {
        /*
         * try { return URLDecoder.decode(s, ENCODING_CHARSET); } catch (UnsupportedEncodingException use) {
         * log.error("Error ocurred decoding URI [" + s + "]", use); }
         */
        return s;
    }

    /**
     * Attempts to create a URI instance and will optionally swallow any resulting URISyntaxException depending on
     * configured {@link CompatibilityHints}. Will also automatically attempt encoding of the string representation for
     * greater compatibility.
     * @param s a string representation of a URI.
     * @return a URI instance, or null if a valid URI string is not specified and relaxed parsing is enabled.
     * @throws URISyntaxException if a valid URI string is not specified and relaxed parsing is disabled
     */
    public static URI create(final String s) throws URISyntaxException {
        try {
            return new URI(encode(s));
        }
        catch (URISyntaxException use) {
            if (CompatibilityHints
                    .isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {

                return null;
            }
            throw use;
        }
    }
}
