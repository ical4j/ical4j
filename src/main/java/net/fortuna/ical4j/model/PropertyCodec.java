package net.fortuna.ical4j.model;

import net.fortuna.ical4j.util.Strings;
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

    private static final String ENCODED_NEWLINE = "\\n";

    private static final String ENCODED_BACKSLASH = "\\\\";

    private static final String ENCODED_QUOTE = "\\\"";

    
    private static final Pattern NEWLINE_EX = Pattern.compile("\n");

    @Override
    public String decode(String source) throws DecoderException {
        if (source != null) {
            return Strings.unescape(source);
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
            return Strings.escape(source);
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
