package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Encodable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

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

    private final InternetAddress address;

    public Email(String address) throws AddressException {
        super(PARAMETER_NAME, new Factory());
        this.address = InternetAddress.parse(address)[0];
    }

    @Override
    public String getValue() {
        return address.getAddress();
    }

    public static class Factory extends Content.Factory implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(PARAMETER_NAME);
        }

        @Override
        public Parameter createParameter(final String value) {
            try {
                return new Email(value);
            } catch (AddressException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
