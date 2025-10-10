package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.PropertyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * Provides a list of property factories by loading them via the Java ServiceLoader mechanism.
 * This allows for dynamic discovery of property factories at runtime, enabling extensibility
 * and customization of iCalendar property parsing.
 *
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html">ServiceLoader</a>
 */
public class ServiceLoaderPropertyFactorySupplier implements Supplier<List<PropertyFactory<?>>> {

    @Override
    public List<PropertyFactory<?>> get() {
        final ServiceLoader<PropertyFactory> serviceLoader = ServiceLoader.load(PropertyFactory.class,
                PropertyFactory.class.getClassLoader());

        List<PropertyFactory<?>> factories = new ArrayList<>();
        serviceLoader.forEach(factories::add);
        return factories;
    }
}
