package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

import static net.fortuna.ical4j.model.Component.VALARM;
import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.One;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

public class VEventValidator extends ComponentValidator<VEvent> {

    private final Validator<VAlarm> itipValidator = new ComponentValidator<>(
            new ValidationRule<>(One, ACTION, TRIGGER),
            new ValidationRule<>(OneOrLess, DESCRIPTION, DURATION, REPEAT, SUMMARY));

    private final boolean alarmsAllowed;

    public VEventValidator(ValidationRule<VEvent>... rules) {
        this(true, rules);
    }

    public VEventValidator(boolean alarmsAllowed, ValidationRule<VEvent>... rules) {
        super(rules);
        this.alarmsAllowed = alarmsAllowed;
    }

    @Override
    public void validate(VEvent target) throws ValidationException {
        super.validate(target);

        if (alarmsAllowed) {
            target.getAlarms().getAll().forEach(itipValidator::validate);
        } else {
            Validator.assertFalse(input -> input.stream().anyMatch(c -> c.getName().equals(VALARM)),
                    ASSERT_NONE_MESSAGE, false, target.getAlarms().getAll(), VALARM);
        }
    }
}
