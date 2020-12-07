package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.util.Constants;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.zone.ZoneRulesProvider;
import java.util.ArrayList;
import java.util.LinkedList;
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

    /**
     * The current component builders.
     */
    private final LinkedList<ComponentBuilder<CalendarComponent>> components = new LinkedList<>();

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

    public ComponentBuilder<CalendarComponent> getComponentBuilder() {
        if (components.size() == 0) {
            return null;
        }
        return components.peek();
    }

    public void endComponent() {
        components.pop();
    }

    @Override
    public void startCalendar() {
        calendarProperties = new ArrayList<>();
        calendarComponents = new ArrayList<>();
        components.clear();
    }

    @Override
    public void endCalendar() {
        ZoneRulesProvider.registerProvider(new ZoneRulesProviderImpl(tzRegistry));
        consumer.accept(new Calendar(new PropertyList(calendarProperties),
                new ComponentList<>(calendarComponents)));
    }

    @Override
    public void startComponent(String name) {
        if (components.size() > 10) {
            throw new RuntimeException("Components nested too deep");
        }

        ComponentBuilder<CalendarComponent> componentBuilder =
                new ComponentBuilder<>();
        componentBuilder.factories(componentFactorySupplier.get()).name(name);
        components.push(componentBuilder);
    }

    @Override
    public void endComponent(String name) {
        assertComponent(getComponentBuilder());

        final ComponentBuilder<CalendarComponent> componentBuilder =
                getComponentBuilder();

        DefaultContentHandler.this.endComponent();

        final ComponentBuilder<CalendarComponent> parent =
                getComponentBuilder();

        if (parent != null) {
            Component subComponent = componentBuilder.build();
            parent.subComponent(subComponent);
        } else {
            CalendarComponent component = componentBuilder.build();
            calendarComponents.add(component);
            if (component instanceof VTimeZone && tzRegistry != null) {
                // register the timezone for use with iCalendar objects..
                tzRegistry.register(new TimeZone((VTimeZone) component));
            }
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
        if (getComponentBuilder() != null) {
            getComponentBuilder().property(property);
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
