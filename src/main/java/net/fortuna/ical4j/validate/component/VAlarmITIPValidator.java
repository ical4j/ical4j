package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;

/**
 * Common validation for all iTIP methods.
 *
 * <pre>
 * Component/Property  Presence
 * ------------------- ----------------------------------------------
 * VALARM              0+
 *     ACTION          1
 *     ATTACH          0+
 *     DESCRIPTION     0 or 1
 *     DURATION        0 or 1  if present REPEAT MUST be present
 *     REPEAT          0 or 1  if present DURATION MUST be present
 *     SUMMARY         0 or 1
 *     TRIGGER         1
 *     X-PROPERTY      0+
 * </pre>
 */
public class VAlarmITIPValidator implements Validator<VAlarm> {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public void validate(final VAlarm target) throws ValidationException {
        Arrays.asList(Property.ACTION, Property.TRIGGER).forEach(property -> PropertyValidator.getInstance().assertOne(property, target.getProperties()));

        Arrays.asList(Property.DESCRIPTION, Property.DURATION, Property.REPEAT, Property.SUMMARY).forEach(property -> PropertyValidator.getInstance().assertOneOrLess(property, target.getProperties()));
    }
}
