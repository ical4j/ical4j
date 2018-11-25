package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;

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
        Arrays.asList(Property.ATTENDEE, Property.DTSTAMP, Property.UID).forEach(
                property -> PropertyValidator.getInstance().assertOne(property, target.getProperties()));

        PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, target.getProperties());

        Arrays.asList(Property.ATTACH, Property.CATEGORIES, Property.CLASS, Property.CONTACT, Property.CREATED,
                Property.DESCRIPTION, Property.DTSTART, Property.DUE, Property.DURATION, Property.EXDATE, Property.EXRULE,
                Property.GEO, Property.LAST_MODIFIED, Property.LOCATION, Property.ORGANIZER, Property.PERCENT_COMPLETE,
                Property.PRIORITY, Property.RDATE, Property.RELATED_TO, Property.REQUEST_STATUS, Property.RESOURCES,
                Property.RRULE, Property.SEQUENCE, Property.STATUS, Property.URL).forEach(
                        property -> PropertyValidator.getInstance().assertNone(property, target.getProperties()));

        ComponentValidator.assertNone(Component.VALARM, target.getAlarms());
    }
}
