package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.PropertyFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Customize behaviour of {@link ContentHandler} implementations.
 */
public class ContentHandlerContext {

    private Supplier<List<ParameterFactory<?>>> parameterFactorySupplier = new DefaultParameterFactorySupplier();

    private Supplier<List<PropertyFactory<?>>> propertyFactorySupplier = new DefaultPropertyFactorySupplier();

    private Supplier<List<ComponentFactory<?>>> componentFactorySupplier = new DefaultComponentFactorySupplier();

    private List<String> ignoredPropertyNames = Collections.emptyList();

    public ContentHandlerContext withParameterFactorySupplier(Supplier<List<ParameterFactory<?>>> parameterFactorySupplier) {
        ContentHandlerContext context = new ContentHandlerContext();
        context.parameterFactorySupplier = parameterFactorySupplier;
        context.propertyFactorySupplier = this.propertyFactorySupplier;
        context.componentFactorySupplier = this.componentFactorySupplier;
        context.ignoredPropertyNames = this.ignoredPropertyNames;
        return context;
    }

    public ContentHandlerContext withPropertyFactorySupplier(Supplier<List<PropertyFactory<?>>> propertyFactorySupplier) {
        ContentHandlerContext context = new ContentHandlerContext();
        context.parameterFactorySupplier = this.parameterFactorySupplier;
        context.propertyFactorySupplier = propertyFactorySupplier;
        context.componentFactorySupplier = this.componentFactorySupplier;
        context.ignoredPropertyNames = this.ignoredPropertyNames;
        return context;
    }

    public ContentHandlerContext withComponentFactorySupplier(Supplier<List<ComponentFactory<?>>> componentFactorySupplier) {
        ContentHandlerContext context = new ContentHandlerContext();
        context.parameterFactorySupplier = this.parameterFactorySupplier;
        context.propertyFactorySupplier = this.propertyFactorySupplier;
        context.componentFactorySupplier = componentFactorySupplier;
        context.ignoredPropertyNames = this.ignoredPropertyNames;
        return context;
    }

    public ContentHandlerContext withIgnoredPropertyNames(List<String> ignoredPropertyNames) {
        ContentHandlerContext context = new ContentHandlerContext();
        context.parameterFactorySupplier = this.parameterFactorySupplier;
        context.propertyFactorySupplier = this.propertyFactorySupplier;
        context.componentFactorySupplier = this.componentFactorySupplier;
        context.ignoredPropertyNames = ignoredPropertyNames;
        return context;
    }

    public Supplier<List<ParameterFactory<?>>> getParameterFactorySupplier() {
        return parameterFactorySupplier;
    }

    public Supplier<List<PropertyFactory<?>>> getPropertyFactorySupplier() {
        return propertyFactorySupplier;
    }

    public Supplier<List<ComponentFactory<?>>> getComponentFactorySupplier() {
        return componentFactorySupplier;
    }

    public List<String> getIgnoredPropertyNames() {
        return ignoredPropertyNames;
    }
}
