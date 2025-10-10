package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.ComponentFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * Provides a list of component factories by loading them via the Java ServiceLoader mechanism.
 * This allows for dynamic discovery of component factories at runtime, enabling extensibility
 * and customization of iCalendar component parsing.
 *
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html">ServiceLoader</a>
 */
public class ServiceLoaderComponentFactorySupplier implements Supplier<List<ComponentFactory<?>>> {

    @Override
    public List<ComponentFactory<?>> get() {
        final ServiceLoader<ComponentFactory> serviceLoader = ServiceLoader.load(ComponentFactory.class,
                ComponentFactory.class.getClassLoader());

        List<ComponentFactory<?>> factories = new ArrayList<>();
        serviceLoader.forEach(factories::add);
        return factories;
    }
}
