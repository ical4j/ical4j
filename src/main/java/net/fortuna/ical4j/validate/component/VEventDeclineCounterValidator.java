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
 * METHOD:DECLINECOUNTER Validator.
 *
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD              1      MUST be "DECLINECOUNTER"
 *
 * VEVENT              1
 *     DTSTAMP         1
 *     ORGANIZER       1
 *     UID             1       MUST, same UID specified in original
 *                             REQUEST and subsequent COUNTER
 *     COMMENT         0 or 1
 *     RECURRENCE-ID   0 or 1  MUST only if referring to an instance of a
 *                             recurring calendar component.  Otherwise it
 *                             MUST NOT be present.
 *     REQUEST-STATUS  0+
 *     SEQUENCE        0 OR 1  MUST be present if value is greater than 0,
 *                             MAY be present if 0
 *     X-PROPERTY      0+
 *     ATTACH          0
 *     ATTENDEE        0
 *     CATEGORIES      0
 *     CLASS           0
 *     CONTACT         0
 *     CREATED         0
 *     DESCRIPTION     0
 *     DTEND           0
 *     DTSTART         0
 *     DURATION        0
 *     EXDATE          0
 *     EXRULE          0
 *     GEO             0
 *     LAST-MODIFIED   0
 *     LOCATION        0
 *     PRIORITY        0
 *     RDATE           0
 *     RELATED-TO      0
 *     RESOURCES       0
 *     RRULE           0
 *     STATUS          0
 *     SUMMARY         0
 *     TRANSP          0
 *     URL             0
 *
 * X-COMPONENT         0+
 * VTODO               0
 * VJOURNAL            0
 * VFREEBUSY           0
 * VTIMEZONE           0
 * VALARM              0
 * </pre>
 *
 */
public class VEventDeclineCounterValidator implements Validator<VEvent> {

    private static final long serialVersionUID = 1L;

    public void validate(final VEvent target) throws ValidationException {
        CollectionUtils.forAllDo(Arrays.asList(Property.DTSTAMP, Property.ORGANIZER, Property.UID), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOne(input, target.getProperties());
            }
        });

        CollectionUtils.forAllDo(Arrays.asList(Property.RECURRENCE_ID, Property.SEQUENCE), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOneOrLess(input, target.getProperties());
            }
        });

        CollectionUtils.forAllDo(Arrays.asList(Property.ATTACH, Property.ATTENDEE, Property.CATEGORIES, Property.CLASS,
                Property.CONTACT, Property.CREATED, Property.DESCRIPTION, Property.DTEND, Property.DTSTART, Property.DURATION,
                Property.EXDATE, Property.EXRULE, Property.GEO, Property.LAST_MODIFIED, Property.LOCATION, Property.PRIORITY,
                Property.RDATE, Property.RELATED_TO, Property.RESOURCES, Property.RRULE, Property.STATUS, Property.SUMMARY,
                Property.TRANSP, Property.URL), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertNone(input, target.getProperties());
            }
        });

        ComponentValidator.assertNone(Component.VALARM, target.getAlarms());
    }
}
