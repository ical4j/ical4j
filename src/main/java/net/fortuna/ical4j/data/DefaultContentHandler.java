package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.util.Constants;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.zone.ZoneRulesProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultContentHandler implements ContentHandler {

    private final Supplier<List<ParameterFactory<?>>> parameterFactorySupplier;

    private final Supplier<List<PropertyFactory<?>>> propertyFactorySupplier;

    private final Supplier<List<ComponentFactory<?>>> componentFactorySupplier;

    private final TimeZoneRegistry tzRegistry;

    private final Consumer<Calendar> consumer;

    private PropertyBuilder propertyBuilder;

    private ComponentBuilder<CalendarComponent> componentBuilder;

    private ComponentBuilder<Component> subComponentBuilder;

    private List<Property> calendarProperties;

    private List<CalendarComponent> calendarComponents;

    public DefaultContentHandler(Consumer<Calendar> consumer, TimeZoneRegistry tzRegistry) {
        this(consumer, tzRegistry, new DefaultParameterFactorySupplier(), new DefaultPropertyFactorySupplier(),
                new DefaultComponentFactorySupplier());
    }

    public DefaultContentHandler(Consumer<Calendar> consumer, TimeZoneRegistry tzRegistry,
                                 Supplier<List<ParameterFactory<?>>> parameterFactorySupplier,
                                 Supplier<List<PropertyFactory<?>>> propertyFactorySupplier,
                                 Supplier<List<ComponentFactory<?>>> componentFactorySupplier) {

        this.consumer = consumer;
        this.tzRegistry = tzRegistry;
        this.parameterFactorySupplier = parameterFactorySupplier;
        this.propertyFactorySupplier = propertyFactorySupplier;
        this.componentFactorySupplier = componentFactorySupplier;
    }

    @Override
    public void startCalendar() {
        calendarProperties = new ArrayList<>();
        calendarComponents = new ArrayList<>();
    }

    @Override
    public void endCalendar() {
        ZoneRulesProvider.registerProvider(new ZoneRulesProviderImpl(tzRegistry));
        consumer.accept(new Calendar(new PropertyList(calendarProperties),
                new ComponentList<>(calendarComponents)));
    }

    @Override
    public void startComponent(String name) {
        if (componentBuilder != null) {
            subComponentBuilder = new ComponentBuilder<>();
            subComponentBuilder.factories(componentFactorySupplier.get()).name(name);
        } else {
            componentBuilder = new ComponentBuilder<>();
            componentBuilder.factories(componentFactorySupplier.get()).name(name);
        }
    }

    @Override
    public void endComponent(String name) {
        assertComponent(componentBuilder);

        if (subComponentBuilder != null) {
            Component subComponent = subComponentBuilder.build();
            componentBuilder.subComponent(subComponent);

            subComponentBuilder = null;
        } else {
            CalendarComponent component = componentBuilder.build();
            calendarComponents.add(component);
            if (component instanceof VTimeZone && tzRegistry != null) {
                // register the timezone for use with iCalendar objects..
                tzRegistry.register(new TimeZone((VTimeZone) component));
            }

            componentBuilder = null;
        }
    }

    @Override
    public void startProperty(String name) {
        propertyBuilder = new PropertyBuilder().factories(propertyFactorySupplier.get())
                .name(name).timeZoneRegistry(tzRegistry);
    }

    @Override
    public void propertyValue(String value) {
        propertyBuilder.value(value);
    }

    @Override
    public void endProperty(String name) throws URISyntaxException, IOException {
        assertProperty(propertyBuilder);
        Property property = propertyBuilder.build();

        // replace with a constant instance if applicable..
        property = Constants.forProperty(property);
        if (componentBuilder != null) {
            if (subComponentBuilder != null) {
                subComponentBuilder.property(property);
            } else {
                componentBuilder.property(property);
            }
        } else if (calendarProperties != null) {
            calendarProperties.add(property);
        }
    }

    @Override
    public void parameter(String name, String value) throws URISyntaxException {
        assertProperty(propertyBuilder);

        Parameter parameter = new ParameterBuilder().factories(parameterFactorySupplier.get())
                .name(name).value(value).build();

        propertyBuilder.parameter(parameter);
    }

    private void assertComponent(ComponentBuilder<?> component) {
        if (component == null) {
            throw new CalendarException("Expected component not initialised");
        }
    }

    private void assertProperty(PropertyBuilder property) {
        if (property == null) {
            throw new CalendarException("Expected property not initialised");
        }
    }
}
