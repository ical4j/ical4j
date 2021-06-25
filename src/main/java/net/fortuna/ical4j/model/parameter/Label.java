package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Encodable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;

/**
 * <pre>
 * Parameter Name:  LABEL
 *
 *  Purpose:  To provide a human readable label.
 *
 *  Format Definition:  This property parameter is defined by the
 *  following notation:
 *
 *  infoparam = "LABEL" "=" param-value
 *
 *  Description:  This property parameter MAY be specified on the
 *  "CONFERENCE" property.  It is anticipated that other extensions to
 *  iCalendar will re-use this property parameter on new properties
 *  that they define.  As a result, clients SHOULD expect to find this
 *  property parameter present on many different properties.  It
 *  provides a human readable label that can be presented to calendar
 *  users to allow them to discriminate between properties which might
 *  be similar, or provide additional information for properties that
 *  are not self-describing.
 *
 *  Example:
 *
 *  CONFERENCE;VALUE=URI;FEATURE=VIDEO;
 *  LABEL="Web video chat, access code=76543";
 *  :http://video-chat.example.com/;group-id=1234
 *  </pre>
 */
public class Label extends Parameter implements Encodable {

    private static final long serialVersionUID = 1L;

    private static final String PARAMETER_NAME = "LABEL";

    private final String value;

    public Label(String value) {
        super(PARAMETER_NAME, new Factory());
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static class Factory extends Content.Factory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(PARAMETER_NAME);
        }

        @Override
        public Parameter createParameter(final String value) {
            return new Label(value);
        }
    }
}
