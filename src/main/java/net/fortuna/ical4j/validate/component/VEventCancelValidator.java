package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;

/**
 * METHOD:CANCEL Validator.
 *
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD              1      MUST be "CANCEL"
 *
 * VEVENT              1+     All must have the same UID
 *     ATTENDEE        0+     MUST include all "Attendees" being removed
 *                            the event. MUST include all "Attendees" if
 *                            the entire event is cancelled.
 *     DTSTAMP         1
 *     ORGANIZER       1
 *     SEQUENCE        1
 *     UID             1       MUST be the UID of the original REQUEST
 *
 *     COMMENT         0 or 1
 *     ATTACH          0+
 *     CATEGORIES      0 or 1  This property may contain a list of values
 *     CLASS           0 or 1
 *     CONTACT         0+
 *     CREATED         0 or 1
 *     DESCRIPTION     0 or 1
 *     DTEND           0 or 1 if present DURATION MUST NOT be present
 *     DTSTART         0 or 1
 *     DURATION        0 or 1 if present DTEND MUST NOT be present
 *     EXDATE          0+
 *     EXRULE          0+
 *     GEO             0 or 1
 *     LAST-MODIFIED   0 or 1
 *     LOCATION        0 or 1
 *     PRIORITY        0 or 1
 *     RDATE           0+
 *     RECURRENCE-ID   0 or 1  MUST be present if referring to one or
 *                             more or more recurring instances.
 *                             Otherwise it MUST NOT be present
 *     RELATED-TO      0+
 *     RESOURCES       0 or 1
 *     RRULE           0+
 *     STATUS          0 or 1  MUST be set to CANCELLED. If uninviting
 *                             specific "Attendees" then MUST NOT be
 *                             included.
 *     SUMMARY         0 or 1
 *     TRANSP          0 or 1
 *     URL             0 or 1
 *     X-PROPERTY      0+
 *     REQUEST-STATUS  0
 *
 * VTIMEZONE           0+     MUST be present if any date/time refers to
 *                            a timezone
 * X-COMPONENT         0+
 *
 * VTODO               0
 * VJOURNAL            0
 * VFREEBUSY           0
 * VALARM              0
 * </pre>
 *
 */
public class VEventCancelValidator implements Validator<VEvent> {

    private static final long serialVersionUID = 1L;

    public final void validate(final VEvent target) throws ValidationException {
        CollectionUtils.forAllDo(Arrays.asList(Property.DTSTAMP, Property.DTSTART, Property.ORGANIZER, Property.SEQUENCE,
                Property.UID), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOne(input, target.getProperties());
            }
        });

        CollectionUtils.forAllDo(Arrays.asList(Property.CATEGORIES, Property.CLASS, Property.CREATED, Property.DESCRIPTION,
                Property.DTEND, Property.DTSTART, Property.DURATION, Property.GEO, Property.LAST_MODIFIED, Property.LOCATION,
                Property.PRIORITY, Property.RECURRENCE_ID, Property.RESOURCES, Property.STATUS, Property.SUMMARY,
                Property.TRANSP, Property.URL), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOneOrLess(input, target.getProperties());
            }
        });

        PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, target.getProperties());

        ComponentValidator.assertNone(Component.VALARM, target.getAlarms());
    }
}
