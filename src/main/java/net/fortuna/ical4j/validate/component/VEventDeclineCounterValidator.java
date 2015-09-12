package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

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

    public void validate(VEvent target) throws ValidationException {
        PropertyValidator.getInstance().assertOne(Property.DTSTAMP, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.ORGANIZER, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.UID, target.getProperties());

        PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, target.getProperties());

        PropertyValidator.getInstance().assertNone(Property.ATTACH, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.ATTENDEE, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.CATEGORIES, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.CLASS, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.CONTACT, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.CREATED, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.DESCRIPTION, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.DTEND, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.DTSTART, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.DURATION, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.EXDATE, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.EXRULE, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.GEO, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.LAST_MODIFIED, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.LOCATION, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.PRIORITY, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.RDATE, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.RELATED_TO, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.RESOURCES, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.RRULE, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.STATUS, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.SUMMARY, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.TRANSP, target.getProperties());
        PropertyValidator.getInstance().assertNone(Property.URL, target.getProperties());

        ComponentValidator.assertNone(Component.VALARM, target.getAlarms());
    }
}
