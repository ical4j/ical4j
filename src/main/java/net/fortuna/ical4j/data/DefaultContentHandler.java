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

public class DefaultContentHandler implements ContentHandler {

    private final Supplier<List<ParameterFactory<? extends Parameter>>> parameterFactorySupplier;

    private final ContentHandlerContext context;

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
        this(consumer, tzRegistry, new ContentHandlerContext());
    }

    public DefaultContentHandler(Consumer<Calendar> consumer, TimeZoneRegistry tzRegistry,
                                 ContentHandlerContext context) {

        this.consumer = consumer;
        this.tzRegistry = tzRegistry;
        this.context = context;
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

        ComponentBuilder<CalendarComponent> componentBuilder = new ComponentBuilder<>(
                context.getComponentFactorySupplier().get());
        componentBuilder.name(name);
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
        if (!context.getIgnoredPropertyNames().contains(name.toUpperCase())) {
            propertyBuilder = new PropertyBuilder(context.getPropertyFactorySupplier().get()).name(name).timeZoneRegistry(tzRegistry);
        } else {
            propertyBuilder = null;
        }
    }

    @Override
    public void propertyValue(String value) {
        if (propertyBuilder != null) {
            propertyBuilder.value(value);
        }
    }

    @Override
    public void endProperty(String name) throws URISyntaxException, IOException {
        if (!context.getIgnoredPropertyNames().contains(name.toUpperCase())) {
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
    }

    @Override
    public void parameter(String name, String value) throws URISyntaxException {
        if (propertyBuilder != null) {
            Parameter parameter = new ParameterBuilder(context.getParameterFactorySupplier().get())
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
