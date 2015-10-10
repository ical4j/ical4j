package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;

/**
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD              1      MUST be "REPLY"
 *
 * VFREEBUSY           1
 *     ATTENDEE        1      (address of recipient replying)
 *     DTSTAMP         1
 *     DTEND           1      DateTime values must be in UTC
 *     DTSTART         1      DateTime values must be in UTC
 *     FREEBUSY        0+      (values MUST all be of the same data
 *                             type. Multiple instances are allowed.
 *                             Multiple instances MUST be sorted in
 *                             ascending order. Values MAY NOT overlap)
 *     ORGANIZER       1       MUST be the request originator's address
 *     UID             1
 *
 *     COMMENT         0 or 1
 *     CONTACT         0+
 *     REQUEST-STATUS  0+
 *     URL             0 or 1  (specifies busy time URL)
 *     X-PROPERTY      0+
 *     DURATION        0
 *     SEQUENCE        0
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
public class VFreeBusyReplyValidator implements Validator<VFreeBusy> {

    private static final long serialVersionUID = 1L;

    public void validate(final VFreeBusy target) throws ValidationException {

        // FREEBUSY is 1+ in RFC2446 but 0+ in Calsify

        CollectionUtils.forAllDo(Arrays.asList(Property.ATTENDEE, Property.DTSTAMP, Property.DTEND, Property.DTSTART,
                Property.ORGANIZER, Property.UID), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOne(input, target.getProperties());
            }
        });

        PropertyValidator.getInstance().assertOneOrLess(Property.URL, target.getProperties());

        CollectionUtils.forAllDo(Arrays.asList(Property.DURATION, Property.SEQUENCE), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertNone(input, target.getProperties());
            }
        });
    }
}
