package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.ParameterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * Provides a list of parameter factories by loading them via the Java ServiceLoader mechanism.
 * This allows for dynamic discovery of parameter factories at runtime, enabling extensibility
 * and customization of iCalendar parameter parsing.
 *
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html">ServiceLoader</a>
 */
public class ServiceLoaderParameterFactorySupplier implements Supplier<List<ParameterFactory<?>>> {

    @Override
    public List<ParameterFactory<?>> get() {
        final ServiceLoader<ParameterFactory> serviceLoader = ServiceLoader.load(ParameterFactory.class,
                ParameterFactory.class.getClassLoader());

        List<ParameterFactory<?>> factories = new ArrayList<>();
        serviceLoader.forEach(factories::add);
        return factories;
    }
}
