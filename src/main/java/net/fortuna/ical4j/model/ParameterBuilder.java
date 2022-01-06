package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.parameter.XParameter;
import org.apache.commons.codec.DecoderException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fortuna on 8/09/14.
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
    public Parameter build() throws URISyntaxException {
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
