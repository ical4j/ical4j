package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.parameter.XParameter;
import org.apache.commons.codec.DecoderException;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating {@link Parameter} instances.
 * This builder allows for the specification of parameter factories, a name, and a value.
 * It supports the creation of parameters with custom names and values, including experimental or illegal names
 * if configured to do so.
 * <p>
 * Usage example:
 * <pre>
 * ParameterBuilder builder = new ParameterBuilder()
 *         .factories(Arrays.asList(new MyParameterFactory()))
 *         .name("MY-PARAM")
 *         .value("myValue");
 * Parameter myParam = builder.build();
 * </pre>
 * </p>
 * <p> Note: The builder supports a list of parameter factories that can be used to create specific types of
 * parameters based on the name provided. If no factory supports the name, it will create a generic
 * {@link XParameter} or throw an exception if the name is not allowed.
 * </p>
 */
public class ParameterBuilder extends AbstractContentBuilder {

    private final List<ParameterFactory<?>> factories;

    private String name;

    private String value;

    public ParameterBuilder() {
        this(new ArrayList<>());
    }

    public ParameterBuilder(List<ParameterFactory<? extends Parameter>> factories) {
        this.factories = factories;
    }

    /**
     * Set the list of parameter factories supporting this builder instance.
     * @param factories a list of parameter factories
     * @return the builder instance
     * @deprecated preference the constructor option for specifying factories
     */
    @Deprecated
    public ParameterBuilder factories(List<ParameterFactory<?>> factories) {
        this.factories.clear();
        this.factories.addAll(factories);
        return this;
    }

    public ParameterBuilder name(String name) {
        // parameter names are case-insensitive, but convert to upper case to simplify further processing
        this.name = name.toUpperCase();
        return this;
    }

    public ParameterBuilder value(String value) {
        this.value = value;
        return this;
    }

    /**
     * @return a new parameter instance
     */
    public Parameter build() {
        Parameter parameter = null;
        String decodedValue;
        try {
            decodedValue = ParameterCodec.INSTANCE.decode(value);
        } catch (DecoderException e) {
            decodedValue = value;
        }
        for (ParameterFactory<?> factory : factories) {
            if (factory.supports(name)) {
                parameter = factory.createParameter(decodedValue);
                break;
            }
        }

        if (parameter == null) {
            if (isExperimentalName(name)) {
                parameter = new XParameter(name, decodedValue);
            }
            else if (allowIllegalNames()) {
                parameter = new XParameter(name, decodedValue);
            }
            else {
                throw new IllegalArgumentException(String.format("Unsupported parameter name: %s", name));
            }
        }
        return parameter;
    }
}
