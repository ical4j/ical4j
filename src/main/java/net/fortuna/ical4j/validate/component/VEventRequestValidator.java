package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;

/**
 * METHOD:REQUEST Validator.
 *
 * <pre>
 * Component/Property  Presence
 * -----------------------------------------------------------------
 * METHOD              1       MUST be "REQUEST"
 * VEVENT              1+      All components MUST have the same UID
 *     ATTENDEE        1+
 *     DTSTAMP         1
 *     DTSTART         1
 *     ORGANIZER       1
 *     SEQUENCE        0 or 1  MUST be present if value is greater than 0,
 *                             MAY be present if 0
 *     SUMMARY         1       Can be null
 *     UID             1
 *
 *     ATTACH          0+
 *     CATEGORIES      0 or 1  This property may contain a list of values
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
 *     RECURRENCE-ID   0 or 1  only if referring to an instance of a
 *                             recurring calendar component.  Otherwise it
 *                             MUST NOT be present.
 *     RELATED-TO      0+
 *     REQUEST-STATUS  0+
 *     RESOURCES       0 or 1  This property MAY contain a list of values
 *     RRULE           0+
 *     STATUS          0 or 1  MAY be one of TENTATIVE/CONFIRMED
 *     TRANSP          0 or 1
 *     URL             0 or 1
 *     X-PROPERTY      0+
 *
 * VALARM              0+
 * VTIMEZONE           0+      MUST be present if any date/time refers to
 *                             a timezone
 * X-COMPONENT         0+
 * VFREEBUSY           0
 * VJOURNAL            0
 * VTODO               0
 * </pre>
 *
 */
public class VEventRequestValidator implements Validator<VEvent> {

    private static final long serialVersionUID = 1L;

    public void validate(final VEvent target) throws ValidationException {
        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, target.getProperties());
        }

        CollectionUtils.forAllDo(Arrays.asList(Property.DTSTAMP, Property.DTSTART, Property.ORGANIZER, Property.SUMMARY,
                Property.UID), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOne(input, target.getProperties());
            }
        });

        CollectionUtils.forAllDo(Arrays.asList(Property.SEQUENCE, Property.CATEGORIES, Property.CLASS, Property.CREATED,
                Property.DESCRIPTION, Property.DTEND, Property.DURATION, Property.GEO, Property.LAST_MODIFIED, Property.LOCATION,
                Property.PRIORITY, Property.RECURRENCE_ID, Property.RESOURCES, Property.STATUS, Property.TRANSP, Property.URL),
                new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOneOrLess(input, target.getProperties());
            }
        });

        for (final VAlarm alarm : target.getAlarms()) {
            alarm.validate(Method.REQUEST);
        }
    }
}
