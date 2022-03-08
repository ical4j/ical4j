package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.validate.*;

import java.util.stream.Collectors;

@Deprecated
public class VEventValidator extends ComponentValidator<VEvent> {

    private static final ComponentContainerRuleSet NO_ALARMS_RULE_SET = new ComponentContainerRuleSet(
            NO_ALARMS);

    private final boolean alarmsAllowed;

    @SafeVarargs
    public VEventValidator(ValidationRule<VEvent>... rules) {
        this(true, rules);
    }

    @SafeVarargs
    public VEventValidator(boolean alarmsAllowed, ValidationRule<VEvent>... rules) {
        super(Component.VALARM, rules);
        this.alarmsAllowed = alarmsAllowed;
    }

    @Override
    public ValidationResult validate(VEvent target) throws ValidationException {
        ValidationResult result = super.validate(target);

        if (alarmsAllowed) {
            result.getEntries().addAll(target.getAlarms().stream().map(ComponentValidator.VALARM_ITIP::validate)
                    .flatMap(r -> r.getEntries().stream()).collect(Collectors.toList()));
        } else {
            result.getEntries().addAll(NO_ALARMS_RULE_SET.apply(target.getName(), target));
        }
        return result;
    }
}
