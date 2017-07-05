package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;

/**
 * <pre>
 * Component/Property   Presence
 * -------------------  ---------------------------------------------
 * METHOD               1       MUST be "CANCEL"
 * VJOURNAL             1+      All MUST have the same UID
 *     DTSTAMP          1
 *     ORGANIZER        1
 *     SEQUENCE         1
 *     UID              1       MUST be the UID of the original REQUEST
 *
 *     ATTACH           0+
 *     ATTENDEE         0+
 *     CATEGORIES       0 or 1  This property MAY contain a list of values
 *     CLASS            0 or 1
 *     COMMENT          0 or 1
 *     CONTACT          0+
 *     CREATED          0 or 1
 *     DESCRIPTION      0 or 1
 *     DTSTART          0 or 1
 *     EXDATE           0+
 *     EXRULE           0+
 *     LAST-MODIFIED    0 or 1
 *     RDATE            0+
 *     RECURRENCE-ID    0 or 1  only if referring to an instance of a
 *                              recurring calendar component.  Otherwise
 *                              it MUST NOT be present.
 *     RELATED-TO       0+
 *     RRULE            0+
 *     STATUS           0 or 1  MAY be present, must be "CANCELLED" if
 *                              present
 *     SUMMARY          0 or 1
 *     URL              0 or 1
 *     X-PROPERTY       0+
 *
 *     REQUEST-STATUS   0
 *
 * VTIMEZONE            0+      MUST be present if any date/time refers to
 *                              a timezone
 * X-COMPONENT          0+
 * VALARM               0
 * VEVENT               0
 * VFREEBUSY            0
 * VTODO                0
 * </pre>
 *
 */
public class VJournalCancelValidator implements Validator<VJournal> {

    private static final long serialVersionUID = 1L;

    public void validate(final VJournal target) throws ValidationException {
        PropertyValidator.getInstance().assertOne(Property.DTSTAMP, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.ORGANIZER, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.SEQUENCE, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.UID, target.getProperties());

        CollectionUtils.forAllDo(Arrays.asList(Property.CATEGORIES, Property.CLASS, Property.CREATED, Property.DESCRIPTION,
                Property.DTSTART, Property.LAST_MODIFIED, Property.RECURRENCE_ID, Property.STATUS, Property.SUMMARY,
                Property.URL), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOneOrLess(input, target.getProperties());
            }
        });

        PropertyValidator.getInstance().assertNone(Property.REQUEST_STATUS, target.getProperties());
    }
}
