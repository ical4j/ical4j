/**
 * Copyright (c) 2026, Ben Fortuna
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

package net.fortuna.ical4j.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import net.fortuna.ical4j.model.AddressList;
import net.fortuna.ical4j.model.ParameterCodec;
import net.fortuna.ical4j.model.PropertyCodec;
import net.fortuna.ical4j.model.TextList;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 08 — the escaping codecs and list values. A selector, then one string biased toward the escape characters:
 * PropertyCodec and ParameterCodec round-trip (CR/CRLF normalised to LF by design) with well-formed encodings,
 * TextList and AddressList round-trip, Uris.create throws only URISyntaxException / IllegalArgumentException, decode
 * never throws, and every function is linear (an amplified input of 1 MB in under 3 s).
 */
public class TextCodecsFuzzer {

    private static final String[] TOKENS = {"\\", "\\\\", "\\n", "\\N", "\\,", "\\;", "\\\"", "^", "^^", "^n", "^^n",
            "^'", "^^'", "\"", "\"\"", ",", ";", ":", "\r\n", "\r", "\n", "mailto:", "cid:", "n", "N", " ", "é", "😀",
            " ", "\u0085", "\u0000"};

    private static long executions;

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    /** A string mixing raw text and the dangerous tokens. */
    private static String text(FuzzedDataProvider d, int maxParts) {
        StringBuilder b = new StringBuilder();
        int parts = d.consumeInt(0, maxParts);
        for (int i = 0; i < parts; i++) {
            if (d.consumeBoolean()) {
                b.append(d.pickValue(TOKENS));
            } else {
                b.append(d.consumeString(8));
            }
        }
        return b.toString();
    }

    private static String normalise(String s) {
        return s.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static void fail(String what, String input, Object detail) {
        throw new AssertionError(what + " — input [" + Ical.abbreviate(input, 300) + "]: " + detail);
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) throws Exception {
        int selector = d.consumeInt(0, 5);
        Ical.hints(d.consumeBoolean());
        switch (selector) {
            case 0:
                propertyCodec(text(d, 24));
                break;
            case 1:
                parameterCodec(text(d, 24));
                break;
            case 2: {
                String s = text(d, 24);
                // decoding arbitrary (received) text never throws, and follows RFC 5545 3.3.11
                String decoded = PropertyCodec.INSTANCE.decode(s);
                if (!decoded.equals(rfcDecode(s))) {
                    fail("PropertyCodec.decode differs from RFC 5545 §3.3.11", s,
                            "[" + decoded + "] instead of [" + rfcDecode(s) + "]");
                }
                ParameterCodec.INSTANCE.decode(s);
                Strings.unescape(s);
                if (!Strings.unquote(Strings.quote(s)).equals(s)) {
                    fail("unquote(quote(s)) != s", s, Strings.unquote(Strings.quote(s)));
                }
                List<String> items = new ArrayList<>(new TextList(s).getTexts());
                if (!items.equals(rfcItems(s))) {
                    fail("TextList items differ from RFC 5545 §3.3.11", s, items + " instead of " + rfcItems(s));
                }
                break;
            }
            case 3:
                textList(d);
                break;
            case 4:
                addressList(d);
                break;
            case 5: {
                String s = text(d, 24);
                try {
                    Uris.create(s);
                } catch (URISyntaxException | IllegalArgumentException e) {
                    // documented
                }
                try {
                    new AddressList(s);
                } catch (URISyntaxException e) {
                    // documented
                }
                break;
            }
            default:
                break;
        }
        // the linearity check (256 KB of input) is expensive: every 256th execution, and always when reproducing,
        // after the case of the input, on a unit drawn from the rest of it
        if (++executions % 256 == 0 || Ical.DEBUG) {
            linear(d);
        }
    }

    private static void propertyCodec(String s) throws EncoderException, DecoderException {
        String enc = PropertyCodec.INSTANCE.encode(s);
        if (enc.indexOf('\r') >= 0 || enc.indexOf('\n') >= 0) {
            fail("PropertyCodec.encode leaves a CR or LF", s, Ical.abbreviate(enc, 300));
        }
        for (int i = 0; i < enc.length(); i++) {
            char c = enc.charAt(i);
            if (c == '\\') {
                i++; // the escaped character
            } else if (c == ',' || c == ';') {
                fail("PropertyCodec.encode leaves an unescaped " + c, s, Ical.abbreviate(enc, 300));
            }
        }
        String dec = PropertyCodec.INSTANCE.decode(enc);
        if (!dec.equals(normalise(s))) {
            fail("PropertyCodec.decode(encode(s)) != s", s, "encoded [" + Ical.abbreviate(enc, 300) + "] decoded ["
                    + Ical.abbreviate(dec, 300) + "]");
        }
        if (!Strings.unescape(Strings.escape(s)).equals(normalise(s))) {
            fail("Strings.unescape(escape(s)) != s", s, Strings.unescape(Strings.escape(s)));
        }
    }

    private static void parameterCodec(String s) throws EncoderException, DecoderException {
        String enc = ParameterCodec.INSTANCE.encode(s);
        if (enc.indexOf('\r') >= 0 || enc.indexOf('\n') >= 0) {
            fail("ParameterCodec.encode leaves a CR or LF", s, Ical.abbreviate(enc, 300));
        }
        boolean quoted = enc.length() >= 2 && enc.startsWith("\"") && enc.endsWith("\"");
        String inner = quoted ? enc.substring(1, enc.length() - 1) : enc;
        if (inner.indexOf('"') >= 0) {
            fail("ParameterCodec.encode leaves a quote inside the value", s, Ical.abbreviate(enc, 300));
        }
        if (!quoted && ParameterCodec.QUOTABLE_VALUE_EX.matcher(inner).find()) {
            fail("ParameterCodec.encode does not quote a value with : ; , or non-ASCII", s, Ical.abbreviate(enc, 300));
        }
        String dec = ParameterCodec.INSTANCE.decode(enc);
        if (!dec.equals(normalise(s))) {
            fail("ParameterCodec.decode(encode(s)) != s", s, "encoded [" + Ical.abbreviate(enc, 300) + "] decoded ["
                    + Ical.abbreviate(dec, 300) + "]");
        }
    }

    private static void textList(FuzzedDataProvider d) {
        int n = d.consumeInt(1, 6);
        List<String> items = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            items.add(text(d, 6));
        }
        LinkedHashSet<String> expected = new LinkedHashSet<>();
        for (String item : items) {
            // an empty item has no representation in a comma-separated list: not expected back
            if (!item.isEmpty()) {
                expected.add(normalise(item));
            }
        }
        String written = new TextList(items).toString();
        List<String> back = new ArrayList<>(new TextList(written).getTexts());
        if (!back.equals(new ArrayList<>(expected))) {
            fail("new TextList(new TextList(items).toString()) != items", String.join("|", items),
                    "written [" + Ical.abbreviate(written, 300) + "] read back " + back);
        }
    }

    private static void addressList(FuzzedDataProvider d) throws URISyntaxException {
        int n = d.consumeInt(1, 4);
        List<URI> uris = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String local = d.consumeBoolean() ? d.pickValue(new String[]{"joe", "jane.doe", "a+b", "o'neil", "x y",
                    "a,b", "\"q\"", "a;b", "a:b"}) : d.consumeString(10);
            String domain = d.pickValue(new String[]{"example.com", "twake.app", "xn--bcher-kva.example", "[127.0.0.1]"});
            try {
                uris.add(new URI("mailto", local + "@" + domain, null));
            } catch (URISyntaxException e) {
                return;
            }
        }
        String written = new AddressList(uris).toString();
        AddressList back;
        try {
            back = new AddressList(written, false);
        } catch (URISyntaxException e) {
            throw new AssertionError("AddressList does not re-parse its own output [" + Ical.abbreviate(written, 300)
                    + "]: " + e.getMessage(), e);
        }
        if (!back.getAddresses().equals(uris)) {
            fail("AddressList round trip", uris.toString(), "written [" + Ical.abbreviate(written, 300) + "] read back "
                    + back.getAddresses());
        }
    }

    /** RFC 5545 3.3.11, decoded left to right: \\, \n or \N, \, and \; (and \" as PropertyCodec documents). */
    static String rfcDecode(String s) {
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(i + 1);
                if (n == '\\' || n == ',' || n == ';' || n == '"') {
                    b.append(n);
                    i++;
                    continue;
                } else if (n == 'n' || n == 'N') {
                    b.append('\n');
                    i++;
                    continue;
                }
            }
            b.append(c);
        }
        return b.toString();
    }

    /** The items of a TEXT list: split on unescaped commas, decoded, without empty or repeated items. */
    static List<String> rfcItems(String s) {
        Set<String> items = new LinkedHashSet<>();
        int from = 0;
        for (int i = 0; i <= s.length(); i++) {
            if (i == s.length() || s.charAt(i) == ',') {
                String item = rfcDecode(s.substring(from, i));
                if (!item.isEmpty()) {
                    items.add(item);
                }
                from = i + 1;
            } else if (s.charAt(i) == '\\' && i + 1 < s.length()) {
                i++; // the escaped character, never a delimiter (a trailing backslash is kept as is)
            }
        }
        return new ArrayList<>(items);
    }

    private interface Codec {
        void run(String value) throws Exception;
    }

    private static long time(Codec codec, String value) throws Exception {
        long start = System.nanoTime();
        codec.run(value);
        return (System.nanoTime() - start) / 1_000_000;
    }

    private static void linear(FuzzedDataProvider d) throws Exception {
        String unit = text(d, 6);
        if (unit.isEmpty()) {
            return;
        }
        StringBuilder b = new StringBuilder(1 << 18);
        while (b.length() < (1 << 18)) {
            b.append(unit);
        }
        String big = b.toString();
        String small = big.substring(0, 1 << 16);
        Map<String, Codec> codecs = new LinkedHashMap<>();
        codecs.put("PropertyCodec", v -> PropertyCodec.INSTANCE.decode(PropertyCodec.INSTANCE.encode(v)));
        codecs.put("PropertyCodec.decode", PropertyCodec.INSTANCE::decode);
        codecs.put("ParameterCodec", v -> ParameterCodec.INSTANCE.decode(ParameterCodec.INSTANCE.encode(v)));
        codecs.put("ParameterCodec.decode", ParameterCodec.INSTANCE::decode);
        codecs.put("AddressList", v -> {
            try {
                new AddressList(v, true);
            } catch (URISyntaxException e) {
                // documented
            }
        });
        codecs.put("TextList", v -> {
            try {
                new TextList(v).toString();
            } catch (StackOverflowError e) {
                // the JVM cuts the trace before any ical4j frame: name the constructor
                throw new AssertionError("net.fortuna.ical4j.model.TextList.<init> overflows the stack on a "
                        + v.length() / 1024 + " KB value", e);
            }
        });
        // a growth measure, not an absolute budget (a cold JIT and Jazzer's regex hooks make that unreliable):
        // linear code takes about 4 times longer on 4 times the input, quadratic code 16 times
        for (Map.Entry<String, Codec> codec : codecs.entrySet()) {
            codec.getValue().run(small);
            long tSmall = time(codec.getValue(), small);
            long tBig = time(codec.getValue(), big);
            if (tBig > 8 * tSmall + 50) {
                fail("superlinear codec " + codec.getKey() + ": 256 KB took " + tBig + " ms, 64 KB " + tSmall + " ms",
                        unit, "the unit repeated to 64 KB and 256 KB");
            }
        }
    }
}
