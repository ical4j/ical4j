package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

/**
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD               1      MUST be "COUNTER"
 * VTODO                1
 *     ATTENDEE         1+
 *     DTSTAMP          1
 *     ORGANIZER        1
 *     PRIORITY         1
 *     SUMMARY          1      Can be null
 *     UID              1
 *
 *     ATTACH           0+
 *     CATEGORIES       0 or 1 This property MAY contain a list of values
 *     CLASS            0 or 1
 *     COMMENT          0 or 1
 *     CONTACT          0+
 *     CREATED          0 or 1
 *     DESCRIPTION      0 or 1 Can be null
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
 *     RECURRENCE-ID    0 or 1 MUST only 3.5if referring to an instance of a
 *                             recurring calendar component.  Otherwise it
 *                             MUST NOT be present.
 *     RELATED-TO       0+
 *     REQUEST-STATUS   0+
 *     RESOURCES        0 or 1 This property MAY contain a list of values
 *     RRULE            0 or 1
 *     SEQUENCE         0 or 1 MUST echo the original SEQUENCE number.
 *                             MUST be present if non-zero. MAY be present
 *                             if zero.
 *     STATUS           0 or 1 MAY be one of COMPLETED/NEEDS ACTION/IN-
 *                             PROCESS/CANCELLED
 *     URL              0 or 1
 *     X-PROPERTY       0+
 *
 *
 * VALARM               0+
 * VTIMEZONE            0 or 1  MUST be present if any date/time refers to
 *                              a timezone
 * X-COMPONENT          0+
 *
 * VEVENT               0
 * VFREEBUSY            0
 * </pre>
 *
 */
public class VToDoCounterValidator implements Validator<VToDo> {

    private static final long serialVersionUID = 1L;

    public void validate(VToDo target) throws ValidationException {
        PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, target.getProperties());

        PropertyValidator.getInstance().assertOne(Property.DTSTAMP, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.ORGANIZER, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.PRIORITY, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.SUMMARY, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.UID, target.getProperties());

        PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DTSTART, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DUE, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.GEO, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.PERCENT_COMPLETE, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.RRULE, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.URL, target.getProperties());

        for (final VAlarm alarm : target.getAlarms()) {
            alarm.validate(Method.COUNTER);
        }
    }
}