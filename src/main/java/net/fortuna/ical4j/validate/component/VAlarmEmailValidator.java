package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;

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
        CollectionUtils.forAllDo(Arrays.asList(Property.DESCRIPTION, Property.SUMMARY), new Closure<String>() {
            @Override
            public void execute(String input) {
                PropertyValidator.getInstance().assertOne(input, target.getProperties());
            }
        });

        PropertyValidator.getInstance().assertOneOrMore(Property.ATTENDEE, target.getProperties());
    }
}
