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

    private boolean suppressInvalidProperties;

    public ContentHandlerContext withParameterFactorySupplier(Supplier<List<ParameterFactory<?>>> parameterFactorySupplier) {
        var context = new ContentHandlerContext();
        context.parameterFactorySupplier = parameterFactorySupplier;
        context.propertyFactorySupplier = this.propertyFactorySupplier;
        context.componentFactorySupplier = this.componentFactorySupplier;
        context.ignoredPropertyNames = this.ignoredPropertyNames;
        context.suppressInvalidProperties = this.suppressInvalidProperties;
        return context;
    }

    public ContentHandlerContext withPropertyFactorySupplier(Supplier<List<PropertyFactory<?>>> propertyFactorySupplier) {
        var context = new ContentHandlerContext();
        context.parameterFactorySupplier = this.parameterFactorySupplier;
        context.propertyFactorySupplier = propertyFactorySupplier;
        context.componentFactorySupplier = this.componentFactorySupplier;
        context.ignoredPropertyNames = this.ignoredPropertyNames;
        context.suppressInvalidProperties = this.suppressInvalidProperties;
        return context;
    }

    public ContentHandlerContext withComponentFactorySupplier(Supplier<List<ComponentFactory<?>>> componentFactorySupplier) {
        var context = new ContentHandlerContext();
        context.parameterFactorySupplier = this.parameterFactorySupplier;
        context.propertyFactorySupplier = this.propertyFactorySupplier;
        context.componentFactorySupplier = componentFactorySupplier;
        context.ignoredPropertyNames = this.ignoredPropertyNames;
        context.suppressInvalidProperties = this.suppressInvalidProperties;
        return context;
    }

    public ContentHandlerContext withIgnoredPropertyNames(List<String> ignoredPropertyNames) {
        var context = new ContentHandlerContext();
        context.parameterFactorySupplier = this.parameterFactorySupplier;
        context.propertyFactorySupplier = this.propertyFactorySupplier;
        context.componentFactorySupplier = this.componentFactorySupplier;
        context.ignoredPropertyNames = ignoredPropertyNames;
        context.suppressInvalidProperties = this.suppressInvalidProperties;
        return context;
    }

    public ContentHandlerContext withSupressInvalidProperties(boolean supressInvalidProperties) {
        var context = new ContentHandlerContext();
        context.parameterFactorySupplier = this.parameterFactorySupplier;
        context.propertyFactorySupplier = this.propertyFactorySupplier;
        context.componentFactorySupplier = this.componentFactorySupplier;
        context.ignoredPropertyNames = this.ignoredPropertyNames;
        context.suppressInvalidProperties = supressInvalidProperties;
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

    public boolean isSuppressInvalidProperties() {
        return suppressInvalidProperties;
    }
}
