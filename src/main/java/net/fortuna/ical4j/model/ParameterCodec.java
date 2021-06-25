package net.fortuna.ical4j.model;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;

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

    // matches an unencoded newline character..
    private static final Pattern NEWLINE_EX = Pattern.compile("\n");

    // matches an unencoded quote character..
    private static final Pattern QUOTE_EX = Pattern.compile("\"");

    // matches an encoded caret character..
    private static final Pattern ENCODED_CARET_EX = Pattern.compile(
            ENCODED_CARET.replaceAll("\\^", "\\\\^"));

    // matches an encoded newline character..
    private static final Pattern ENCODED_NEWLINE_EX = Pattern.compile(
            ENCODED_NEWLINE.replaceAll("\\^", "\\\\^"));

    // matches an encoded quote character..
    private static final Pattern ENCODED_QUOTE_EX = Pattern.compile(
            ENCODED_QUOTE.replaceAll("\\^", "\\\\^"));

    // matches one or more characters that require a quoted value..
    public static final Pattern QUOTABLE_VALUE_EX = Pattern.compile("[:;,]|[^\\p{ASCII}]");

    // matches a quoted string..
    public static final Pattern QUOTED_EX = Pattern.compile("^\"[^\"]*\"$");


    @Override
    public String decode(String source) throws DecoderException {
        if (source != null) {
            String decoded = ENCODED_CARET_EX.matcher(
                    ENCODED_NEWLINE_EX.matcher(
                            ENCODED_QUOTE_EX.matcher(source).replaceAll("\"")
                    ).replaceAll("\n")
            ).replaceAll("^");

            // remove extraneous quotes..
            if (QUOTED_EX.matcher(decoded).matches()) {
                return decoded.substring(1, decoded.length() - 1);
            } else {
                return decoded;
            }
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
            String encoded = QUOTE_EX.matcher(
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
