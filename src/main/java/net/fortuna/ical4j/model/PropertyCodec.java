package net.fortuna.ical4j.model;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;

import java.util.regex.Pattern;

/**
 * Support for encoding/decoding property values that include quotes, newlines, and escape characters.
 */
public class PropertyCodec implements StringEncoder, StringDecoder {

    public static final PropertyCodec INSTANCE = new PropertyCodec();

    private static final String ENCODED_NEWLINE = "\\\\n";

    private static final String ENCODED_BACKSLASH = "\\\\\\\\";


    // matches an unencoded backslash character..
    private static final Pattern BACKSLASH_EX = Pattern.compile("\\\\");

    // matches an unencoded newline character..
    private static final Pattern NEWLINE_EX = Pattern.compile("\n");

    // matches an unencoded special character..
    private static final Pattern SPECIALCHAR_EX = Pattern.compile("([,;\"])");

    // matches an encoded backslash character..
    private static final Pattern ENCODED_BACKSLASH_EX = Pattern.compile(ENCODED_BACKSLASH);

    // matches an encoded newline character..
    private static final Pattern ENCODED_NEWLINE_EX = Pattern.compile("(?<!\\\\)" + ENCODED_NEWLINE);

    // matches an encoded special character..
    private static final Pattern ENCODED_SPECIALCHAR_EX = Pattern.compile("\\\\([,;\"])");

    @Override
    public String decode(String source) throws DecoderException {
        if (source != null) {
            String decoded = ENCODED_BACKSLASH_EX.matcher(
                    ENCODED_NEWLINE_EX.matcher(
                            ENCODED_SPECIALCHAR_EX.matcher(source).replaceAll("$1")
                    ).replaceAll("\n")
            ).replaceAll("\\\\");
            return decoded;
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
            // order is significant here as we don't want to double-encode backslash..
            String encoded = SPECIALCHAR_EX.matcher(
                    NEWLINE_EX.matcher(
                            BACKSLASH_EX.matcher(source).replaceAll(ENCODED_BACKSLASH)
                    ).replaceAll(ENCODED_NEWLINE)
            ).replaceAll("\\\\$1");
            return encoded;
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
