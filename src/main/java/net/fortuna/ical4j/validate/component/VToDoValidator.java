package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ContentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;

import java.util.Optional;

public class VToDoValidator extends ComponentValidator<VToDo> {

    private final boolean alarmsAllowed;

    @SafeVarargs
    public VToDoValidator(ValidationRule<VToDo>... rules) {
        this(true, rules);
    }

    @SafeVarargs
    public VToDoValidator(boolean alarmsAllowed, ValidationRule<VToDo>... rules) {
        super(rules);
        this.alarmsAllowed = alarmsAllowed;
    }

    @Override
    public void validate(VToDo target) throws ValidationException {
        ComponentValidator.VTODO.validate(target);

        final Optional<Status> status = target.getProperty(Property.STATUS);
        if (status.isPresent() && !Status.VTODO_NEEDS_ACTION.getValue().equals(status.get().getValue())
                && !Status.VTODO_COMPLETED.getValue().equals(status.get().getValue())
                && !Status.VTODO_IN_PROCESS.getValue().equals(status.get().getValue())
                && !Status.VTODO_CANCELLED.getValue().equals(status.get().getValue())) {
            throw new ValidationException("Status property [" + status + "] may not occur in VTODO");
        }

        if (alarmsAllowed) {
            target.getAlarms().forEach(a -> ComponentValidator.VALARM_ITIP.validate(a));
        } else {
            ContentValidator.assertNone(Component.VALARM, target.getAlarms(), false);
        }
    }
}
