package net.fortuna.ical4j.validate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ValidationRule implements Serializable {

    public enum ValidationType { None,  One, OneOrLess, OneOrMore }

    private final ValidationType type;

    private final List<String> instances;

    private final boolean relaxedModeSupported;

    public ValidationRule(ValidationType type, String...instances) {
        this(type, false, instances);
    }

    public ValidationRule(ValidationType type, boolean relaxedModeSupported, String...instances) {
        this.type = type;
        this.instances = Arrays.asList(instances);
        this.relaxedModeSupported = relaxedModeSupported;
    }

    public ValidationType getType() {
        return type;
    }

    public List<String> getInstances() {
        return instances;
    }

    public boolean isRelaxedModeSupported() {
        return relaxedModeSupported;
    }
}
