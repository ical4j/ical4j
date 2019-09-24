package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

import java.util.Arrays;
import java.util.List;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.One;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

public class VTimeZoneValidator extends ComponentValidator<VTimeZone> {

    private final Validator itipValidator = new ComponentValidator(Arrays.asList(
            new ValidationRule(One, DTSTART, TZOFFSETFROM, TZOFFSETTO),
            new ValidationRule(OneOrLess, TZNAME)));

    public VTimeZoneValidator(List<ValidationRule> rules) {
        super(rules);
    }

    @Override
    public void validate(VTimeZone target) throws ValidationException {
        super.validate(target);
        target.getObservances().forEach(itipValidator::validate);
    }
}
