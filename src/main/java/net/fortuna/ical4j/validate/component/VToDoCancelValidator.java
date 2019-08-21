package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;

import static net.fortuna.ical4j.model.Component.VALARM;
import static net.fortuna.ical4j.model.Property.*;

/**
 * <pre>
 * Component/Property   Presence
 * -------------------  ---------------------------------------------
 * METHOD               1     MUST be "CANCEL"
 * VTODO                1
 *     ATTENDEE         0+    include all "Attendees" being removed from
 *                            the todo. MUST include all "Attendees" if
 *                            the entire todo is cancelled.
 *     UID              1     MUST echo original UID
 *     DTSTAMP          1
 *     ORGANIZER        1
 *     SEQUENCE         1
 *
 *     ATTACH           0+
 *     CATEGORIES       0 or 1 This property MAY contain a list of values
 *     CLASS            0 or 1
 *     COMMENT          0 or 1
 *     CONTACT          0+
 *     CREATED          0 or 1
 *     DESCRIPTION      0 or 1
 *     DTSTART          0 or 1
 *     DUE              0 or 1  If present DURATION MUST NOT be present
 *     DURATION         0 or 1  If present DUE MUST NOT be present
 *     EXDATE           0+
 *     EXRULE           0+
 *     GEO              0 or 1
 *     LAST-MODIFIED    0 or 1
 *     LOCATION         0 or 1
 *     PERCENT-COMPLETE 0 or 1
 *     RDATE            0+
 *     RECURRENCE-ID    0 or 1 MUST only if referring to one or more
 *                             instances of a recurring calendar
 *                             component. Otherwise it MUST NOT be
 *                             present.
 *     RELATED-TO       0+
 *     RESOURCES        0 or 1 This property MAY contain a list of values
 *     RRULE            0+
 *     PRIORITY         0 or 1
 *     STATUS           0 or 1  If present it MUST be set to "CANCELLED".
 *                              MUST NOT be used if purpose is to remove
 *                              "ATTENDEES" rather than cancel the entire
 *                              VTODO.
 *     URL              0 or 1
 *     X-PROPERTY       0+
 *
 *     REQUEST-STATUS   0
 *
 * VTIMEZONE            0 or 1  MUST be present if any date/time refers to
 *                              a timezone
 * X-COMPONENT          0+
 *
 * VALARM               0
 * VEVENT               0
 * VFREEBUSY            0
 * </pre>
 *
 */
public class VToDoCancelValidator implements Validator<VToDo> {

    private static final long serialVersionUID = 1L;

    public void validate(final VToDo target) throws ValidationException {
        Arrays.asList(UID, DTSTAMP, ORGANIZER, SEQUENCE).forEach(
                property -> PropertyValidator.getInstance().assertOne(property, target.getProperties()));

        Arrays.asList(CATEGORIES, CLASS, CREATED, DESCRIPTION,
                DTSTART, DUE, DURATION, GEO, LAST_MODIFIED, LOCATION,
                PERCENT_COMPLETE, RECURRENCE_ID, RESOURCES, PRIORITY, STATUS,
                URL).forEach(property -> PropertyValidator.getInstance().assertOneOrLess(property, target.getProperties()));

        PropertyValidator.getInstance().assertNone(REQUEST_STATUS, target.getProperties());

        ComponentValidator.assertNone(VALARM, target.getAlarms());
    }
}
