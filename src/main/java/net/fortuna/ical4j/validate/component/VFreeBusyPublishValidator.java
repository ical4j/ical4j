package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;

import static net.fortuna.ical4j.model.Property.*;

/**
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD              1       MUST be "PUBLISH"
 *
 * VFREEBUSY           1+
 *     DTSTAMP         1
 *     DTSTART         1       DateTime values must be in UTC
 *     DTEND           1       DateTime values must be in UTC
 *     FREEBUSY        1+      MUST be BUSYTIME. Multiple instances are
 *                             allowed. Multiple instances must be sorted
 *                             in ascending order
 *     ORGANIZER       1       MUST contain the address of originator of
 *                             busy time data.
 *     UID             1
 *     COMMENT         0 or 1
 *     CONTACT         0+
 *     X-PROPERTY      0+
 *     URL             0 or 1  Specifies busy time URL
 *
 *     ATTENDEE        0
 *     DURATION        0
 *     REQUEST-STATUS  0
 *
 * X-COMPONENT         0+
 *
 * VEVENT              0
 * VTODO               0
 * VJOURNAL            0
 * VTIMEZONE           0
 * VALARM              0
 * </pre>
 *
 */
public class VFreeBusyPublishValidator implements Validator<VFreeBusy> {

    private static final long serialVersionUID = 1L;

    public void validate(final VFreeBusy target) throws ValidationException {
        PropertyValidator.getInstance().assertOneOrMore(FREEBUSY, target.getProperties());

        Arrays.asList(DTSTAMP, DTSTART, DTEND, ORGANIZER,
                UID).forEach(property -> PropertyValidator.getInstance().assertOne(property, target.getProperties()));

        PropertyValidator.getInstance().assertOneOrLess(URL, target.getProperties());

        Arrays.asList(ATTENDEE, DURATION, REQUEST_STATUS).forEach(property -> PropertyValidator.getInstance().assertNone(property, target.getProperties()));
    }
}
