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
        PropertyValidator.getInstance().assertOneOrMore(Property.FREEBUSY, target.getProperties());

        CollectionUtils.forAllDo(Arrays.asList(Property.DTSTAMP, Property.DTSTART, Property.DTEND, Property.ORGANIZER,
                Property.UID), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOne(input, target.getProperties());
            }
        });

        PropertyValidator.getInstance().assertOneOrLess(Property.URL, target.getProperties());

        CollectionUtils.forAllDo(Arrays.asList(Property.ATTENDEE, Property.DURATION, Property.REQUEST_STATUS),
                new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertNone(input, target.getProperties());
            }
        });
    }
}
