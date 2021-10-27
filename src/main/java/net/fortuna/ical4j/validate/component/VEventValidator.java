package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;

public class VEventValidator extends ComponentValidator<VEvent> {

    private final boolean alarmsAllowed;

    @SafeVarargs
    public VEventValidator(ValidationRule<VEvent>... rules) {
        this(true, rules);
    }

    @SafeVarargs
    public VEventValidator(boolean alarmsAllowed, ValidationRule<VEvent>... rules) {
        super(rules);
        this.alarmsAllowed = alarmsAllowed;
    }

    @Override
    public void validate(VEvent target) throws ValidationException {
        super.validate(target);

        if (alarmsAllowed) {
            target.getComponents(Component.VALARM).forEach(a -> ComponentValidator.VALARM_ITIP.validate((VAlarm) a));
        } else {
            ComponentValidator.assertNone(Component.VALARM, target.getAlarms());
        }
    }
}
