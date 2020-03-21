package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.PropertyFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

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
