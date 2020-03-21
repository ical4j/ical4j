package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.ComponentFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

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
