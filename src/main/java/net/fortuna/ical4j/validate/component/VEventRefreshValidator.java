package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;

import static net.fortuna.ical4j.model.Property.*;

/**
 * METHOD:REFRESH Validator.
 *
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD              1      MUST be "REFRESH"
 *
 * VEVENT              1
 *     ATTENDEE        1      MUST be the address of requestor
 *     DTSTAMP         1
 *     ORGANIZER       1
 *     UID             1      MUST be the UID associated with original
 *                            REQUEST
 *     COMMENT         0 or 1
 *     RECURRENCE-ID   0 or 1 MUST only if referring to an instance of a
 *                            recurring calendar component.  Otherwise
 *                            it must NOT be present.
 *     X-PROPERTY      0+
 *
 *     ATTACH          0
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
 *     REQUEST-STATUS  0
 *     RESOURCES       0
 *     RRULE           0
 *     SEQUENCE        0
 *     STATUS          0
 *     SUMMARY         0
 *     TRANSP          0
 *     URL             0
 *
 * X-COMPONENT         0+
 *
 * VTODO               0
 * VJOURNAL            0
 * VFREEBUSY           0
 * VTIMEZONE           0
 * VALARM              0
 * </pre>
 *
 */
public class VEventRefreshValidator implements Validator<VEvent> {

    private static final long serialVersionUID = 1L;

    public void validate(final VEvent target) throws ValidationException {
        Arrays.asList(ATTENDEE, DTSTAMP, ORGANIZER, UID).forEach(property -> PropertyValidator.getInstance().assertOne(property, target.getProperties()));

        PropertyValidator.getInstance().assertOneOrLess(RECURRENCE_ID, target.getProperties());

        Arrays.asList(ATTACH, CATEGORIES, CLASS, CONTACT,
                CREATED, DESCRIPTION, DTEND, DTSTART, DURATION, EXDATE,
                EXRULE, GEO, LAST_MODIFIED, LOCATION, PRIORITY, RDATE,
                RELATED_TO, REQUEST_STATUS, RESOURCES, RRULE, SEQUENCE,
                STATUS, SUMMARY, TRANSP, URL).forEach(property -> PropertyValidator.getInstance().assertNone(property, target.getProperties()));

        ComponentValidator.assertNone(Component.VALARM, target.getAlarms());
    }
}
