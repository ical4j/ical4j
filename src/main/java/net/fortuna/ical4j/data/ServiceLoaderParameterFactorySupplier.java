package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.ParameterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;

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
