package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Encodable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * Parameter Name:  FEATURE
 *
 *  Purpose:  To specify a feature or features of a conference or
 *  broadcast system.
 *
 *  Format Definition:  This property parameter is defined by the
 *  following notation:
 *
 *  featureparam = "FEATURE" "=" featuretext *("," featuretext)
 *  featuretext  =  ("AUDIO" /     ; Audio capability
 *  "CHAT" /      ; Chat or instant messaging
 *  "FEED" /      ; Blog or Atom feed
 *  "MODERATOR" / ; Moderator dial-in code
 *  "PHONE" /     ; Phone conference
 *  "SCREEN" /    ; Screen sharing
 *  "VIDEO" /     ; Video capability
 *  x-name /      ; Experimental type
 *  iana-token)   ; Other IANA registered type
 *
 *  Description:  This property parameter MAY be specified on the
 *  "CONFERENCE" property.  Multiple values can be specified.  The
 *  "MODERATOR" value is used to indicate that the property value is
 *  specific to the owner/initiator of the conference and contains a
 *  URI that "activates" the system (e.g., a "moderator" access code
 *  for a phone conference system that is different from the "regular"
 *  access code).
 *
 *  Example:
 *
 *  CONFERENCE;VALUE=URI;FEATURE=AUDIO:rtsp://audio.example.com/
 *  event
 *  CONFERENCE;VALUE=URI;FEATURE=AUDIO,VIDEO:http://video-chat.exam
 *  ple.com/;group-id=1234
 *  </pre>
 */
public class Feature extends Parameter implements Encodable {

    private static final long serialVersionUID = 1L;

    private static final String PARAMETER_NAME = "FEATURE";

    public enum Value {
        AUDIO, CHAT, FEED, MODERATOR, PHONE, SCREEN, VIDEO;
    }

    private final String[] values;

    public Feature(String value) {
        super(PARAMETER_NAME, new Factory());
        String[] valueStrings = value.split(",");
        for (String valueString : valueStrings) {
            try {
                Value.valueOf(valueString);
            } catch (IllegalArgumentException iae) {
                if (!valueString.startsWith(Parameter.EXPERIMENTAL_PREFIX)) {
                    throw iae;
                }
            }
        }
        this.values = valueStrings;
    }

    @Override
    public String getValue() {
        return StringUtils.join(values, ",");
    }

    public static class Factory extends Content.Factory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(PARAMETER_NAME);
        }

        @Override
        public Parameter createParameter(final String value) {
            return new Feature(value);
        }
    }
}
