package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;

/**
 * METHOD:REQUEST Validator.
 *
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD              1      MUST be "REQUEST"
 *
 * VFREEBUSY           1
 *     ATTENDEE        1+     contain the address of the calendar store
 *     DTEND           1      DateTime values must be in UTC
 *     DTSTAMP         1
 *     DTSTART         1      DateTime values must be in UTC
 *     ORGANIZER       1      MUST be the request originator's address
 *     UID             1
 *     COMMENT         0 or 1
 *     CONTACT         0+
 *     X-PROPERTY      0+
 *
 *     FREEBUSY        0
 *     DURATION        0
 *     REQUEST-STATUS  0
 *     URL             0
 *
 * X-COMPONENT         0+
 * VALARM              0
 * VEVENT              0
 * VTODO               0
 * VJOURNAL            0
 * VTIMEZONE           0
 * </pre>
 *
 */
public class VFreeBusyRequestValidator implements Validator<VFreeBusy> {

    private static final long serialVersionUID = 1L;

    public void validate(final VFreeBusy target) throws ValidationException {
        PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, target.getProperties());

        Arrays.asList(Property.DTEND, Property.DTSTAMP, Property.DTSTART, Property.ORGANIZER,
                Property.UID).forEach(property -> PropertyValidator.getInstance().assertOne(property, target.getProperties()));

        Arrays.asList(Property.FREEBUSY, Property.DURATION, Property.REQUEST_STATUS,
                Property.URL).forEach(property -> PropertyValidator.getInstance().assertNone(property, target.getProperties()));
    }
}
