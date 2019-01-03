package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;

import static net.fortuna.ical4j.model.Property.*;

/**
 * METHOD:PUBLISH Validator.
 *
 * <pre>
 *
 *  +----------------------------------------------+
 *  | Constraints for a METHOD:PUBLISH of a VEVENT |
 *  +----------------------------------------------+
 *
 *  +--------------------+----------+-----------------------------------+
 *  | Component/Property | Presence | Comment                           |
 *  +--------------------+----------+-----------------------------------+
 *  | METHOD             | 1        | MUST equal PUBLISH.               |
 *  |                    |          |                                   |
 *  | VEVENT             | 1+       |                                   |
 *  |   DTSTAMP          | 1        |                                   |
 *  |   DTSTART          | 1        |                                   |
 *  |   ORGANIZER        | 1        |                                   |
 *  |   SUMMARY          | 1        | Can be null.                      |
 *  |   UID              | 1        |                                   |
 *  |   RECURRENCE-ID    | 0 or 1   | Only if referring to an instance  |
 *  |                    |          | of a recurring calendar           |
 *  |                    |          | component.  Otherwise, it MUST    |
 *  |                    |          | NOT be present.                   |
 *  |   SEQUENCE         | 0 or 1   | MUST be present if value is       |
 *  |                    |          | greater than 0; MAY be present if |
 *  |                    |          | 0.                                |
 *  |   ATTACH           | 0+       |                                   |
 *  |   CATEGORIES       | 0+       |                                   |
 *  |   CLASS            | 0 or 1   |                                   |
 *  |   COMMENT          | 0+       |                                   |
 *  |   CONTACT          | 0 or 1   |                                   |
 *  |   CREATED          | 0 or 1   |                                   |
 *  |   DESCRIPTION      | 0 or 1   | Can be null.                      |
 *  |   DTEND            | 0 or 1   | If present, DURATION MUST NOT be  |
 *  |                    |          | present.                          |
 *  |   DURATION         | 0 or 1   | If present, DTEND MUST NOT be     |
 *  |                    |          | present.                          |
 *  |   EXDATE           | 0+       |                                   |
 *  |   GEO              | 0 or 1   |                                   |
 *  |   LAST-MODIFIED    | 0 or 1   |                                   |
 *  |   LOCATION         | 0 or 1   |                                   |
 *  |   PRIORITY         | 0 or 1   |                                   |
 *  |   RDATE            | 0+       |                                   |
 *  |   RELATED-TO       | 0+       |                                   |
 *  |   RESOURCES        | 0+       |                                   |
 *  |   RRULE            | 0 or 1   |                                   |
 *  |   STATUS           | 0 or 1   | MAY be one of                     |
 *  |                    |          | TENTATIVE/CONFIRMED/CANCELLED.    |
 *  |   TRANSP           | 0 or 1   |                                   |
 *  |   URL              | 0 or 1   |                                   |
 *  |   IANA-PROPERTY    | 0+       |                                   |
 *  |   X-PROPERTY       | 0+       |                                   |
 *  |   ATTENDEE         | 0        |                                   |
 *  |   REQUEST-STATUS   | 0        |                                   |
 *  |                    |          |                                   |
 *  |   VALARM           | 0+       |                                   |
 *  |                    |          |                                   |
 *  | VFREEBUSY          | 0        |                                   |
 *  |                    |          |                                   |
 *  | VJOURNAL           | 0        |                                   |
 *  |                    |          |                                   |
 *  | VTODO              | 0        |                                   |
 *  |                    |          |                                   |
 *  | VTIMEZONE          | 0+       | MUST be present if any date/time  |
 *  |                    |          | refers to a timezone.             |
 *  |                    |          |                                   |
 *  | IANA-COMPONENT     | 0+       |                                   |
 *  | X-COMPONENT        | 0+       |                                   |
 *  +--------------------+----------+-----------------------------------+
 * </pre>
 *
 */
public class VEventPublishValidator implements Validator<VEvent> {

    private static final long serialVersionUID = 1L;

    public void validate(final VEvent target) throws ValidationException {
        CollectionUtils.forAllDo(Arrays.asList(DTSTAMP, DTSTART), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOne(input, target.getProperties());
            }
        });

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            CollectionUtils.forAllDo(Arrays.asList(ORGANIZER, SUMMARY), new Closure<String>() {
                @Override
                public void execute(String input) {
                    PropertyValidator.getInstance().assertOne(input, target.getProperties());
                }
            });
        }

        PropertyValidator.getInstance().assertOne(UID, target.getProperties());

        CollectionUtils.forAllDo(Arrays.asList(RECURRENCE_ID, SEQUENCE, CATEGORIES, CLASS,
                CREATED, DESCRIPTION, DTEND, DURATION, GEO, LAST_MODIFIED,
                LOCATION, PRIORITY, RESOURCES, STATUS, TRANSP, URL),
                new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOneOrLess(input, target.getProperties());
            }
        });

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            PropertyValidator.getInstance().assertNone(ATTENDEE, target.getProperties());
        }

        PropertyValidator.getInstance().assertNone(REQUEST_STATUS, target.getProperties());

        for (final VAlarm alarm : target.getAlarms()) {
            alarm.validate(Method.PUBLISH);
        }
    }
}
