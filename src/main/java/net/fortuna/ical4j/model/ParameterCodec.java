package net.fortuna.ical4j.model;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Support for encoding/decoding property values that include quotes, newlines, and escape characters.
 */
public class ParameterCodec implements StringEncoder, StringDecoder {

    public static final ParameterCodec INSTANCE = new ParameterCodec();

    private static final String ENCODED_CARET = "^^";

    private static final String ENCODED_NEWLINE = "^n";

    private static final String ENCODED_QUOTE = "^'";


    // matches an unencoded caret character..
    private static final Pattern CARET_EX = Pattern.compile("\\^");

    // matches an unencoded line break in any form (CRLF, bare CR or bare LF), as a bare CR
    // cannot be represented in a parameter value and would otherwise terminate the content line..
    private static final Pattern NEWLINE_EX = Pattern.compile("\r\n|\r|\n");

    // matches an unencoded quote character..
    private static final Pattern QUOTE_EX = Pattern.compile("\"");

    // matches an encoded caret, newline or quote character..
    private static final Pattern ENCODED_EX = Pattern.compile("\\^([\\^n'])");

    // matches one or more characters that require a quoted value..
    public static final Pattern QUOTABLE_VALUE_EX = Pattern.compile("[:;,]|[^\\p{ASCII}]");

    // matches a quoted string..
    public static final Pattern QUOTED_EX = Pattern.compile("^\"[^\"]*\"$");


    @Override
    public String decode(String source) throws DecoderException {
        if (source != null) {
            // remove the enclosing quotes of a quoted value before decoding, so that encoded quotes (^')
            // at either end of the value are not mistaken for them..
            var value = QUOTED_EX.matcher(source).matches() ? source.substring(1, source.length() - 1) : source;

            // decode in a single left-to-right pass, so that the caret of an encoded caret (^^) is never
            // the start of another escape sequence (RFC 6868 section 3)..
            return ENCODED_EX.matcher(value).replaceAll(m -> {
                switch (m.group(1)) {
                    case "n":
                        return "\n";
                    case "'":
                        return "\"";
                    default:
                        return Matcher.quoteReplacement("^");
                }
            });
        } else {
            throw new DecoderException("Input cannot be null");
        }
    }

    @Override
    public Object decode(Object source) throws DecoderException {
        if (source != null) {
            return decode(source.toString());
        } else {
            throw new DecoderException("Input cannot be null");
        }
    }

    @Override
    public String encode(String source) throws EncoderException {
        if (source != null) {
            // order is significant here as we don't want to double-encode carets..
            var encoded = QUOTE_EX.matcher(
                    NEWLINE_EX.matcher(
                            CARET_EX.matcher(source).replaceAll(ENCODED_CARET)
                    ).replaceAll(ENCODED_NEWLINE)
            ).replaceAll(ENCODED_QUOTE);

            // apply quotes if value includes special characters..
            if (QUOTABLE_VALUE_EX.matcher(encoded).find()) {
                return "\"" + encoded + "\"";
            } else {
                return encoded;
            }
        } else {
            throw new EncoderException("Input cannot be null");
        }
    }

    @Override
    public Object encode(Object source) throws EncoderException {
        if (source != null) {
            return encode(source.toString());
        } else {
            throw new EncoderException("Input cannot be null");
        }
    }
}
