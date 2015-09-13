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
 * METHOD:REPLY Validator.
 *
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD              1       MUST be "REPLY"
 * VEVENT              1+      All components MUST have the same UID
 *     ATTENDEE        1       MUST be the address of the Attendee
 *                             replying.
 *     DTSTAMP         1
 *     ORGANIZER       1
 *     RECURRENCE-ID   0 or 1  only if referring to an instance of a
 *                             recurring calendar component.  Otherwise
 *                             it must NOT be present.
 *     UID             1       MUST be the UID of the original REQUEST
 *
 *     SEQUENCE        0 or 1  MUST if non-zero, MUST be the sequence
 *                             number of the original REQUEST. MAY be
 *                             present if 0.
 *
 *     ATTACH          0+
 *     CATEGORIES      0 or 1  This property may contain a list of values
 *     CLASS           0 or 1
 *     COMMENT         0 or 1
 *     CONTACT         0+
 *     CREATED         0 or 1
 *     DESCRIPTION     0 or 1
 *     DTEND           0 or 1  if present DURATION MUST NOT be present
 *     DTSTART         0 or 1
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
 *     REQUEST-STATUS  0+
 *     RRULE           0+
 *     STATUS          0 or 1
 *     SUMMARY         0 or 1
 *     TRANSP          0 or 1
 *     URL             0 or 1
 *     X-PROPERTY      0+
 *
 * VTIMEZONE           0 or 1 MUST be present if any date/time refers
 *                            to a timezone
 * X-COMPONENT         0+
 *
 * VALARM              0
 * VFREEBUSY           0
 * VJOURNAL            0
 * VTODO               0
 * </pre>
 *
 */
public class VEventReplyValidator implements Validator<VEvent> {

    private static final long serialVersionUID = 1L;

    public void validate(final VEvent target) throws ValidationException {
        CollectionUtils.forAllDo(Arrays.asList(Property.ATTENDEE, Property.DTSTAMP, Property.ORGANIZER, Property.UID),
                new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOne(input, target.getProperties());
            }
        });

        CollectionUtils.forAllDo(Arrays.asList(Property.RECURRENCE_ID, Property.SEQUENCE, Property.CATEGORIES, Property.CLASS,
                Property.CREATED, Property.DESCRIPTION, Property.DTEND, Property.DTSTART, Property.DURATION, Property.GEO,
                Property.LAST_MODIFIED, Property.LOCATION, Property.PRIORITY, Property.RESOURCES, Property.STATUS, Property.SUMMARY,
                Property.TRANSP, Property.URL), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOneOrLess(input, target.getProperties());
            }
        });

        ComponentValidator.assertNone(Component.VALARM, target.getAlarms());
    }
}
