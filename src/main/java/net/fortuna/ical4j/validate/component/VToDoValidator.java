package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;

public class VToDoValidator extends ComponentValidator<VToDo> {

    private final boolean alarmsAllowed;

    public VToDoValidator(ValidationRule... rules) {
        this(true, rules);
    }

    public VToDoValidator(boolean alarmsAllowed, ValidationRule... rules) {
        super(rules);
        this.alarmsAllowed = alarmsAllowed;
    }

    @Override
    public void validate(VToDo target) throws ValidationException {
        ComponentValidator.VTODO.validate(target);

        final Status status = target.getProperty(Property.STATUS);
        if (status != null && !Status.VTODO_NEEDS_ACTION.getValue().equals(status.getValue())
                && !Status.VTODO_COMPLETED.getValue().equals(status.getValue())
                && !Status.VTODO_IN_PROCESS.getValue().equals(status.getValue())
                && !Status.VTODO_CANCELLED.getValue().equals(status.getValue())) {
            throw new ValidationException("Status property [" + status + "] may not occur in VTODO");
        }

        if (alarmsAllowed) {
            target.getAlarms().forEach(ComponentValidator.VALARM_ITIP::validate);
        } else {
            ComponentValidator.assertNone(Component.VALARM, target.getAlarms());
        }
    }
}
