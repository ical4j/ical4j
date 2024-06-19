package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.validate.*;

import java.util.Optional;
import java.util.stream.Collectors;

import static net.fortuna.ical4j.model.property.immutable.ImmutableStatus.*;

@Deprecated
public class VToDoValidator extends ComponentValidator<VToDo> {

    private static final ComponentContainerRuleSet NO_ALARMS_RULE_SET = new ComponentContainerRuleSet(
            NO_ALARMS);

    private final boolean alarmsAllowed;

    @SafeVarargs
    public VToDoValidator(ValidationRule<VToDo>... rules) {
        this(true, rules);
    }

    @SafeVarargs
    public VToDoValidator(boolean alarmsAllowed, ValidationRule<VToDo>... rules) {
        super(Component.VTODO, rules);
        this.alarmsAllowed = alarmsAllowed;
    }

    @Override
    public ValidationResult validate(VToDo target) throws ValidationException {
        ValidationResult result = ComponentValidator.VTODO.validate(target);

        final Optional<Status> status = target.getProperty(Property.STATUS);
        if (status.isPresent() && !VTODO_NEEDS_ACTION.equals(status.get())
                && !VTODO_COMPLETED.equals(status.get())
                && !VTODO_IN_PROCESS.equals(status.get())
                && !VTODO_CANCELLED.equals(status.get())) {

            result.getEntries().add(new ValidationEntry("Status property [" + status + "] may not occur in VTODO",
                    ValidationEntry.Severity.ERROR, target.getName()));
        }

        if (alarmsAllowed) {
            result.getEntries().addAll(target.getAlarms().stream().map(ComponentValidator.VALARM_ITIP::validate)
                    .flatMap(r -> r.getEntries().stream()).collect(Collectors.toList()));
        } else {
            result.getEntries().addAll(NO_ALARMS_RULE_SET.apply(target.getName(), target));
        }
        return result;
    }
}
