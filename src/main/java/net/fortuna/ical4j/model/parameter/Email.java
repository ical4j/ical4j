package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Encodable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.apache.commons.validator.routines.EmailValidator;

import static net.fortuna.ical4j.util.CompatibilityHints.KEY_RELAXED_PARSING;
import static net.fortuna.ical4j.util.CompatibilityHints.KEY_RELAXED_VALIDATION;

/**
 * From specification:
 *
 * <pre>
 *  Parameter Name:  EMAIL
 *
 *  Purpose:  To specify an email address that is used to identify or
 *  contact an organizer or attendee.
 *
 *  Format Definition:  This property parameter is defined by the
 *  following notation:
 *
 *  emailparam = "EMAIL" "=" param-value
 *
 *  Description:  This property parameter MAY be specified on "ORGANIZER"
 *  or "ATTENDEE" properties.  This property can be used in situations
 *  where the calendar user address value of "ORGANIZER" and
 *  "ATTENDEE" properties is not likely to be an identifier that
 *  recipients of scheduling messages could use to match the calendar
 *  user with, for example, an address book entry.  The value of this
 *  property is an email address that can easily be matched by
 *  recipients.  Recipients can also use this value as an alternative
 *  means of contacting the calendar user via email.  If a recipient's
 *  calendar user agent allows the recipient to save contact
 *  information based on the "ORGANIZER" or "ATTENDEE" properties,
 *  those calendar user agents SHOULD use any "EMAIL" property
 *  parameter value for the email address of the contact over any
 *  mailto: calendar user address specified as the value of the
 *  property.  Calendar user agents SHOULD NOT include an "EMAIL"
 *  property parameter when its value matches the calendar user
 *  address specified as the value of the property.
 *
 *  Example:
 *
 *  ATTENDEE;CN=Cyrus Daboo;EMAIL=cyrus@example.com:mailto:opaque-toke
 *  n-1234@example.com
 * </pre>
 */
public class Email extends Parameter implements Encodable {

    private static final long serialVersionUID = 1L;

    private static final String PARAMETER_NAME = "EMAIL";

    private final String address;

    public Email(String address) {
        super(PARAMETER_NAME);
        if (CompatibilityHints.isHintEnabled(KEY_RELAXED_PARSING)) {
            this.address = address.replaceFirst("\\.$", "");
        } else {
            this.address = address;
        }
        if (!CompatibilityHints.isHintEnabled(KEY_RELAXED_VALIDATION)
                && !EmailValidator.getInstance().isValid(this.address)) {
            throw new IllegalArgumentException("Invalid address: " + address);
        }
    }

    @Override
    public String getValue() {
        return address;
    }

    public static class Factory extends Content.Factory implements ParameterFactory<Email> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(PARAMETER_NAME);
        }

        @Override
        public Email createParameter(final String value) {
            return new Email(value);
        }
    }
}
