package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Encodable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * Parameter Name:  DISPLAY
 *
 * Purpose:  To specify different ways in which an image for a calendar
 * or component can be displayed.
 *
 * Format Definition:  This property parameter is defined by the
 * following notation:
 *
 * displayparam = "DISPLAY" "=" displayval *("," displayval)
 *
 * displayval =   ("BADGE" /    ; image inline with the title of the
 *                              ; event
 *                 "GRAPHIC" /  ; a full image replacement for the event
 *                              ; itself
 *                 "FULLSIZE /  ; an image that is used to enhance the
 *                              ; event
 *                 "THUMBNAIL / ; a smaller variant of "FULLSIZE" to be
 *                              ; used when space for the image is
 *                              ; constrained
 *                 x-name /     ; Experimental type
 *                 iana-token)  ; Other IANA registered type
 *                              ;
 *                              ; Default is BADGE
 *
 * Description:  This property parameter MAY be specified on "IMAGE"
 * properties.  In the absence of this parameter, the value "BADGE"
 * MUST be used for the default behavior.  The value determines how a
 * client ought to present an image supplied in iCalendar data to the
 * user.
 *
 * Values for this parameter are registered with IANA as per
 * Section 8.3.  New values can be added to this registry following
 * the procedure outlined in Section 8.2.1 of [RFC5545].
 *
 * Servers and clients MUST handle x-name and iana-token values they
 * don't recognize by not displaying any image at all.
 *
 * Example:
 *
 * IMAGE;VALUE=URI;DISPLAY=BADGE,THUMBNAIL,;FMTTYPE=image/png:http://exa
 * mple.com/images/weather-cloudy.png
 * </pre>
 */
public class Display extends Parameter implements Encodable {

    private static final long serialVersionUID = 1L;

    private static final String PARAMETER_NAME = "DISPLAY";

    public enum Value {
        BADGE, GRAPHIC, FULLSIZE, THUMBNAIL;
    }

    private final String[] values;

    public Display(String value) {
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
            return new Display(value);
        }
    }
}
