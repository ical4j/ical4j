package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import static net.fortuna.ical4j.model.Property.*;

/**
 * <pre>
 * Component/Property   Presence
 * -------------------  ---------------------------------------------
 * METHOD               1      MUST be "REFRESH"
 * VTODO                1
 *     ATTENDEE         1
 *     DTSTAMP          1
 *     UID              1       MUST echo original UID
 *
 *     RECURRENCE-ID    0 or 1  MUST only if referring to an instance of a
 *                              Recurring calendar component. Otherwise it
 *                              MUST NOT be present
 *     X-PROPERTY       0+
 *
 *     ATTACH           0
 *     CATEGORIES       0
 *     CLASS            0
 *     COMMENT          0
 *     CONTACT          0
 *     CREATED          0
 *     DESCRIPTION      0
 *     DTSTART          0
 *     DUE              0
 *     DURATION         0
 *     EXDATE           0
 *     EXRULE           0
 *     GEO              0
 *     LAST-MODIFIED    0
 *     LOCATION         0
 *     ORGANIZER        0
 *     PERCENT-COMPLETE 0
 *     PRIORITY         0
 *     RDATE            0
 *     RELATED-TO       0
 *     REQUEST-STATUS   0
 *     RESOURCES        0
 *     RRULE            0
 *     SEQUENCE         0
 *     STATUS           0
 *     URL              0
 *
 * X-COMPONENT          0+
 *
 * VALARM               0
 * VEVENT               0
 * VFREEBUSY            0
 * VTIMEZONE            0
 * </pre>
 *
 */
public class VToDoRefreshValidator implements Validator<VToDo> {

    private static final long serialVersionUID = 1L;

    public void validate(VToDo target) throws ValidationException {
        PropertyValidator.getInstance().assertOne(ATTENDEE, target.getProperties());
        PropertyValidator.getInstance().assertOne(DTSTAMP, target.getProperties());
        PropertyValidator.getInstance().assertOne(UID, target.getProperties());

        PropertyValidator.getInstance().assertOneOrLess(RECURRENCE_ID, target.getProperties());

        PropertyValidator.getInstance().assertNone(ATTACH, target.getProperties());
        PropertyValidator.getInstance().assertNone(CATEGORIES, target.getProperties());
        PropertyValidator.getInstance().assertNone(CLASS, target.getProperties());
        PropertyValidator.getInstance().assertNone(CONTACT, target.getProperties());
        PropertyValidator.getInstance().assertNone(CREATED, target.getProperties());
        PropertyValidator.getInstance().assertNone(DESCRIPTION, target.getProperties());
        PropertyValidator.getInstance().assertNone(DTSTART, target.getProperties());
        PropertyValidator.getInstance().assertNone(DUE, target.getProperties());
        PropertyValidator.getInstance().assertNone(DURATION, target.getProperties());
        PropertyValidator.getInstance().assertNone(EXDATE, target.getProperties());
        PropertyValidator.getInstance().assertNone(EXRULE, target.getProperties());
        PropertyValidator.getInstance().assertNone(GEO, target.getProperties());
        PropertyValidator.getInstance().assertNone(LAST_MODIFIED, target.getProperties());
        PropertyValidator.getInstance().assertNone(LOCATION, target.getProperties());
        PropertyValidator.getInstance().assertNone(ORGANIZER, target.getProperties());
        PropertyValidator.getInstance().assertNone(PERCENT_COMPLETE, target.getProperties());
        PropertyValidator.getInstance().assertNone(PRIORITY, target.getProperties());
        PropertyValidator.getInstance().assertNone(RDATE, target.getProperties());
        PropertyValidator.getInstance().assertNone(RELATED_TO, target.getProperties());
        PropertyValidator.getInstance().assertNone(REQUEST_STATUS, target.getProperties());
        PropertyValidator.getInstance().assertNone(RESOURCES, target.getProperties());
        PropertyValidator.getInstance().assertNone(RRULE, target.getProperties());
        PropertyValidator.getInstance().assertNone(SEQUENCE, target.getProperties());
        PropertyValidator.getInstance().assertNone(STATUS, target.getProperties());
        PropertyValidator.getInstance().assertNone(URL, target.getProperties());

        ComponentValidator.assertNone(Component.VALARM, target.getAlarms());
    }
}
