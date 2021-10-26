package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;

public class VEventValidator extends ComponentValidator<VEvent> {

    private final boolean alarmsAllowed;

    public VEventValidator(ValidationRule... rules) {
        this(true, rules);
    }

    public VEventValidator(boolean alarmsAllowed, ValidationRule... rules) {
        super(rules);
        this.alarmsAllowed = alarmsAllowed;
    }

    @Override
    public void validate(VEvent target) throws ValidationException {
        super.validate(target);

        if (alarmsAllowed) {
            target.getAlarms().forEach(ComponentValidator.VALARM_ITIP::validate);
        } else {
            ComponentValidator.assertNone(Component.VALARM, target.getAlarms());
        }
    }
}
