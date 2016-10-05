package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;

/**
 * METHOD:ADD Validator.
 *
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD              1      MUST be "ADD"
 * VEVENT              1
 *     DTSTAMP         1
 *     DTSTART         1
 *     ORGANIZER       1
 *     SEQUENCE        1      MUST be greater than 0
 *     SUMMARY         1      Can be null
 *     UID             1      MUST match that of the original event
 *
 *     ATTACH          0+
 *     ATTENDEE        0+
 *     CATEGORIES      0 or 1 This property MAY contain a list of values
 *     CLASS           0 or 1
 *     COMMENT         0 or 1
 *     CONTACT         0+
 *     CREATED         0 or 1
 *     DESCRIPTION     0 or 1  Can be null
 *     DTEND           0 or 1  if present DURATION MUST NOT be present
 *     DURATION        0 or 1  if present DTEND MUST NOT be present
 *     EXDATE          0+
 *     EXRULE          0+
 *     GEO             0 or 1
 *     LAST-MODIFIED   0 or 1
 *     LOCATION        0 or 1
 *     PRIORITY        0 or 1
 *     RDATE           0+
 *     RELATED-TO      0+
 *     RESOURCES       0 or 1  This property MAY contain a list of values
 *     RRULE           0+
 *     STATUS          0 or 1  MAY be one of TENTATIVE/CONFIRMED
 *     TRANSP          0 or 1
 *     URL             0 or 1
 *     X-PROPERTY      0+
 *
 *     RECURRENCE-ID   0
 *     REQUEST-STATUS  0
 *
 * VALARM              0+
 * VTIMEZONE           0+     MUST be present if any date/time refers to
 *                            a timezone
 * X-COMPONENT         0+
 *
 * VFREEBUSY           0
 * VTODO               0
 * VJOURNAL            0
 * </pre>
 *
 */
public class VEventAddValidator implements Validator<VEvent> {

    private static final long serialVersionUID = 1L;

    public void validate(final VEvent target) throws ValidationException {
        CollectionUtils.forAllDo(Arrays.asList(Property.DTSTAMP, Property.DTSTART, Property.ORGANIZER, Property.SEQUENCE,
                Property.SUMMARY, Property.UID), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOne(input, target.getProperties());
            }
        });

        CollectionUtils.forAllDo(Arrays.asList(Property.CATEGORIES, Property.CLASS, Property.CREATED, Property.DESCRIPTION,
                Property.DTEND, Property.DURATION, Property.GEO, Property.LAST_MODIFIED, Property.LOCATION, Property.PRIORITY,
                Property.RESOURCES, Property.STATUS, Property.TRANSP, Property.URL), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOneOrLess(input, target.getProperties());
            }
        });

        CollectionUtils.forAllDo(Arrays.asList(Property.RECURRENCE_ID, Property.REQUEST_STATUS), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertNone(input, target.getProperties());
            }
        });

        for (final VAlarm alarm : target.getAlarms()) {
            alarm.validate(Method.ADD);
        }
    }
}
