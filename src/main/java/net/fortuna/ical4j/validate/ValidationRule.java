package net.fortuna.ical4j.validate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Provides a template for validating presence (or absence) of properties, parameters or components in a list.
 */
public class ValidationRule<T> implements Serializable {

    public enum ValidationType { None,  One, OneOrLess, OneOrMore }

    private final ValidationType type;

    private final Predicate<T> predicate;

    private final List<String> instances;

    private final boolean relaxedModeSupported;

    /**
     * @param type rule type
     * @param instances list of identifiers to check (parameter, property, component, etc.)
     */
    public ValidationRule(ValidationType type, String...instances) {
        this(type, (Predicate<T> & Serializable) (T p) -> true, instances);
    }

    public ValidationRule(ValidationType type, Predicate<T> predicate, String...instances) {
        this(type, predicate, false, instances);
    }

    /**
     * @param type rule type
     * @param relaxedModeSupported indicates if rule can be ignored when relaxed mode is enabled
     * @param instances list of identifiers to check (parameter, property, component, etc.)
     */
    public ValidationRule(ValidationType type, boolean relaxedModeSupported, String...instances) {
        this(type, (Predicate<T> & Serializable) (T p) -> true, relaxedModeSupported, instances);
    }

    public ValidationRule(ValidationType type, Predicate<T> predicate, boolean relaxedModeSupported, String...instances) {
        this.type = type;
        this.predicate = predicate;
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

    public boolean isRelaxedModeSupported() {
        return relaxedModeSupported;
    }
}
