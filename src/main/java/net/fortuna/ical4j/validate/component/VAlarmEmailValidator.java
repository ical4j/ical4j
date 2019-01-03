package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;

import static net.fortuna.ical4j.model.Property.*;

/**
 * Created by fortuna on 12/09/15.
 */
public class VAlarmEmailValidator implements Validator<VAlarm> {

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public void validate(final VAlarm target) throws ValidationException {
        /*
         * ; the following are all REQUIRED,
         * ; but MUST NOT occur more than once action / description / trigger / summary
         * ; the following is REQUIRED,
         * ; and MAY occur more than once attendee /
         * ; 'duration' and 'repeat' are both optional,
         * ; and MUST NOT occur more than once each,
         * ; but if one occurs, so MUST the other duration / repeat /
         * ; the following are optional,
         * ; and MAY occur more than once attach / x-prop
         */
        Arrays.asList(DESCRIPTION, SUMMARY).forEach(property -> PropertyValidator.getInstance().assertOne(property, target.getProperties()));

        PropertyValidator.getInstance().assertOneOrMore(ATTENDEE, target.getProperties());
    }
}
