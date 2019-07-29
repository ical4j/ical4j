package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;

import static net.fortuna.ical4j.model.Property.*;

/**
 * Common validation for all iTIP methods.
 *
 * <pre>
 *    Component/Property  Presence
 *    ------------------- ----------------------------------------------
 *    VTIMEZONE           0+      MUST be present if any date/time refers
 *                                to timezone
 *        DAYLIGHT        0+      MUST be one or more of either STANDARD or
 *                                DAYLIGHT
 *           COMMENT      0 or 1
 *           DTSTART      1       MUST be local time format
 *           RDATE        0+      if present RRULE MUST NOT be present
 *           RRULE        0+      if present RDATE MUST NOT be present
 *           TZNAME       0 or 1
 *           TZOFFSET     1
 *           TZOFFSETFROM 1
 *           TZOFFSETTO   1
 *           X-PROPERTY   0+
 *        LAST-MODIFIED   0 or 1
 *        STANDARD        0+      MUST be one or more of either STANDARD or
 *                                DAYLIGHT
 *           COMMENT      0 or 1
 *           DTSTART      1       MUST be local time format
 *           RDATE        0+      if present RRULE MUST NOT be present
 *           RRULE        0+      if present RDATE MUST NOT be present
 *           TZNAME       0 or 1
 *           TZOFFSETFROM 1
 *           TZOFFSETTO   1
 *           X-PROPERTY   0+
 *        TZID            1
 *        TZURL           0 or 1
 *        X-PROPERTY      0+
 * </pre>
 */
public class VTimeZoneITIPValidator implements Validator<VTimeZone> {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public void validate(VTimeZone target) throws ValidationException {
        for (final Observance observance : target.getObservances()) {
            Arrays.asList(DTSTART, TZOFFSETFROM, TZOFFSETTO).forEach(
                    property -> PropertyValidator.getInstance().assertOne(property, observance.getProperties()));

            PropertyValidator.getInstance().assertOneOrLess(TZNAME, observance.getProperties());
        }
    }
}
