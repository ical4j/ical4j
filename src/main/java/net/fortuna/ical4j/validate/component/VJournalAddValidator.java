package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;

import static net.fortuna.ical4j.model.Property.*;

/**
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * METHOD               1      MUST be "ADD"
 * VJOURNAL             1
 *     DESCRIPTION      1      Can be null.
 *     DTSTAMP          1
 *     DTSTART          1
 *     ORGANIZER        1
 *     SEQUENCE         1      MUST be greater than 0
 *     UID              1      MUST match that of the original journal
 *
 *     ATTACH           0+
 *     CATEGORIES       0 or 1 This property MAY contain a list of values
 *     CLASS            0 or 1
 *     COMMENT          0 or 1
 *     CONTACT          0+
 *     CREATED          0 or 1
 *     EXDATE           0+
 *     EXRULE           0+
 *     LAST-MODIFIED    0 or 1
 *     RDATE            0+
 *     RELATED-TO       0+
 *     RRULE            0+
 *     STATUS           0 or 1  MAY be one of DRAFT/FINAL/CANCELLED
 *     SUMMARY          0 or 1  Can be null
 *     URL              0 or 1
 *     X-PROPERTY       0+
 *
 *     ATTENDEE         0
 *     RECURRENCE-ID    0
 *
 * VALARM               0+
 * VTIMEZONE            0 or 1 MUST be present if any date/time refers to
 *                             a timezone
 * X-COMPONENT          0+
 *
 * VEVENT               0
 * VFREEBUSY            0
 * VTODO                0
 * </pre>
 *
 */
public class VJournalAddValidator implements Validator<VJournal> {

    private static final long serialVersionUID = 1L;

    public void validate(final VJournal target) throws ValidationException {
        Arrays.asList(DESCRIPTION, DTSTAMP, DTSTART, ORGANIZER,
                SEQUENCE, UID).forEach(property -> PropertyValidator.getInstance().assertOne(property, target.getProperties()));

        Arrays.asList(CATEGORIES, CLASS, CREATED, LAST_MODIFIED,
                STATUS, SUMMARY, URL).forEach(property -> PropertyValidator.getInstance().assertOneOrLess(property, target.getProperties()));

        Arrays.asList(ATTENDEE, RECURRENCE_ID).forEach(
                property -> PropertyValidator.getInstance().assertNone(property, target.getProperties()));
    }
}
