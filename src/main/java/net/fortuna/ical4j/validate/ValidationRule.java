package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.util.CompatibilityHints;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.None;

/**
 * Defines a rule for validating iCalendar content. A rule consists of a validation type (e.g. presence/absence of
 * specific content), and one or more identifiers (names) of iCalendar content.
 * For example, a rule might define a test for one or less DTEND properties using the "OneOrLess" validation type
 * and "DTEND" identifier.
 *
 * Alternatively, a Predicate may be specified to provide additional validation beyond the presence/absence of content.
 * For example, a predicate may be used to ensure certain date properties are DATE/DATE-TIME values.
 * Note that the predicate must specify the negative case, such that if it evaluates true the rule fails.
 */
public class ValidationRule<T> implements Serializable {

    public enum ValidationType {
        None("The following MUST NOT be present:"),
        One("The following are REQUIRED, but MUST NOT occur more than once:"),
        OneOrLess("The following are OPTIONAL, but MUST NOT occur more than once:"),
        OneOrMore("The following are OPTIONAL, and MAY occur more than once:"),
        OneExclusive("If one is present, ALL others MUST NOT be present:"),
        AllOrNone("If one is present, ALL must be present:"),
        ValueMatch("Value MUST match expression:");

        private final String description;

        ValidationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final ValidationType type;

    private final String message;

    private final Predicate<T> predicate;

    private final List<String> instances;

    private final boolean relaxedModeSupported;

    /**
     * @param type rule type
     * @param instances list of identifiers to check (parameter, property, component, etc.)
     */
    public ValidationRule(ValidationType type, String...instances) {
        this(type, (Predicate<T> & Serializable) (T p) -> true, null, false, instances);
    }

    /**
     * @param type rule type
     * @param relaxedModeSupported indicates if rule can be ignored when relaxed mode is enabled
     * @param instances list of identifiers to check (parameter, property, component, etc.)
     */
    public ValidationRule(ValidationType type, boolean relaxedModeSupported, String...instances) {
        this(type, (Predicate<T> & Serializable) (T p) -> true, null, relaxedModeSupported, instances);
    }

    /**
     *
     * @param predicate a negative predicate, such that a successful test indicates a rule violation
     * @param message the message used to indicate a rule violation
     * @param instances identifiers for instances under test
     */
    public ValidationRule(Predicate<T> predicate, String message, String...instances) {
        this(None, predicate, message, false, instances);
    }

    public ValidationRule(Predicate<T> predicate, String message, boolean relaxedModeSupported, String...instances) {
        this(None, predicate, message, relaxedModeSupported, instances);
    }

    private ValidationRule(ValidationType type, Predicate<T> predicate, String message, boolean relaxedModeSupported,
                           String...instances) {
        this.type = type;
        this.predicate = predicate;
        this.message = message;
        this.instances = Arrays.asList(instances);
        this.relaxedModeSupported = relaxedModeSupported;
    }

    public ValidationType getType() {
        return type;
    }

    public Predicate<T> getPredicate() {
        return predicate;
    }

    public List<String> getInstances() {
        return instances;
    }

    public String getMessage(String...instances) {
        List<String> match = getInstances();
        if (instances.length > 0) {
            match = Arrays.asList(instances);
        }
        return String.format("%s %s", message != null ? message : getType().getDescription(),
                String.join(",", match));
    }

    public ValidationEntry.Severity getSeverity() {
        boolean warnOnly = CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
                && relaxedModeSupported;
        if (warnOnly) {
            return ValidationEntry.Severity.WARNING;
        } else {
            return ValidationEntry.Severity.ERROR;
        }
    }
}
