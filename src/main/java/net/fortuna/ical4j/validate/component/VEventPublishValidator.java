package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

/**
 * METHOD:PUBLISH Validator.
 *
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD              1       MUST equal "PUBLISH"
 * VEVENT              1+
 *      DTSTAMP        1
 *      DTSTART        1
 *      ORGANIZER      1
 *      SUMMARY        1       Can be null.
 *      UID            1
 *      RECURRENCE-ID  0 or 1  only if referring to an instance of a
 *                             recurring calendar component.  Otherwise
 *                             it MUST NOT be present.
 *      SEQUENCE       0 or 1  MUST be present if value is greater than
 *                             0, MAY be present if 0
 *      ATTACH         0+
 *      CATEGORIES     0 or 1  This property may contain a list of
 *                             values
 *      CLASS          0 or 1
 *      COMMENT        0 or 1
 *      CONTACT        0+
 *      CREATED        0 or 1
 *      DESCRIPTION    0 or 1  Can be null
 *      DTEND          0 or 1  if present DURATION MUST NOT be present
 *      DURATION       0 or 1  if present DTEND MUST NOT be present
 *      EXDATE         0+
 *      EXRULE         0+
 *      GEO            0 or 1
 *      LAST-MODIFIED  0 or 1
 *      LOCATION       0 or 1
 *      PRIORITY       0 or 1
 *      RDATE          0+
 *      RELATED-TO     0+
 *      RESOURCES      0 or 1 This property MAY contain a list of values
 *      RRULE          0+
 *      STATUS         0 or 1 MAY be one of TENTATIVE/CONFIRMED/CANCELLED
 *      TRANSP         0 or 1
 *      URL            0 or 1
 *      X-PROPERTY     0+
 *
 *      ATTENDEE       0
 *      REQUEST-STATUS 0
 *
 * VALARM              0+
 * VFREEBUSY           0
 * VJOURNAL            0
 * VTODO               0
 * VTIMEZONE           0+    MUST be present if any date/time refers to
 *                           a timezone
 * X-COMPONENT         0+
 * </pre>
 *
 */
public class VEventPublishValidator implements Validator<VEvent> {

    private static final long serialVersionUID = 1L;

    public void validate(VEvent target) throws ValidationException {
        PropertyValidator.getInstance().assertOne(Property.DTSTAMP, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.DTSTART, target.getProperties());

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            PropertyValidator.getInstance().assertOne(Property.ORGANIZER, target.getProperties());
            PropertyValidator.getInstance().assertOne(Property.SUMMARY, target.getProperties());
        }

        PropertyValidator.getInstance().assertOne(Property.UID, target.getProperties());

        PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DESCRIPTION, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DTEND, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.DURATION, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.GEO, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LOCATION, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.PRIORITY, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.RESOURCES, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.TRANSP, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.URL, target.getProperties());

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            PropertyValidator.getInstance().assertNone(Property.ATTENDEE, target.getProperties());
        }

        PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, target.getProperties());

        for (final VAlarm alarm : target.getAlarms()) {
            alarm.validate(Method.PUBLISH);
        }
    }
}
