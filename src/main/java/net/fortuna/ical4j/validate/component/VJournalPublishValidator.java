package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

/**
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD               1       MUST be "PUBLISH"
 * VJOURNAL             1+
 *     DESCRIPTION      1       Can be null.
 *     DTSTAMP          1
 *     DTSTART          1
 *     ORGANIZER        1
 *     UID              1
 *
 *     ATTACH           0+
 *     CATEGORIES       0 or 1  This property MAY contain a list of values
 *     CLASS            0 or 1
 *     COMMENT          0 or 1
 *     CONTACT          0+
 *     CREATED          0 or 1
 *     EXDATE           0+
 *     EXRULE           0+
 *     LAST-MODIFIED    0 or 1
 *     RDATE            0+
 *     RECURRENCE-ID    0 or 1  MUST only if referring to an instance of a
 *                              recurring calendar component.  Otherwise
 *                              it MUST NOT be present.
 *     RELATED-TO       0+
 *     RRULE            0+
 *     SEQUENCE         0 or 1  MUST echo the original SEQUENCE number.
 *                              MUST be present if non-zero. MAY be
 *                              present if zero.
 *     STATUS           0 or 1  MAY be one of DRAFT/FINAL/CANCELLED
 *     SUMMARY          0 or 1  Can be null
 *     URL              0 or 1
 *     X-PROPERTY       0+
 *
 *     ATTENDEE         0
 *
 * VALARM               0+
 * VTIMEZONE            0+      MUST be present if any date/time refers to
 *                              a timezone
 * X-COMPONENT          0+
 *
 * VEVENT               0
 * VFREEBUSY            0
 * VTODO                0
 * </pre>
 *
 */
public class VJournalPublishValidator implements Validator<VJournal> {

    private static final long serialVersionUID = 1L;

    public void validate(VJournal target) throws ValidationException {
        PropertyValidator.getInstance().assertOne(Property.DESCRIPTION, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.DTSTAMP, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.DTSTART, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.ORGANIZER, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.UID, target.getProperties());

        PropertyValidator.getInstance().assertOneOrLess(Property.CATEGORIES, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CLASS, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.CREATED, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.RECURRENCE_ID, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SEQUENCE, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.STATUS, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.SUMMARY, target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.URL, target.getProperties());

        PropertyValidator.getInstance().assertNone(Property.ATTENDEE, target.getProperties());
    }
}
