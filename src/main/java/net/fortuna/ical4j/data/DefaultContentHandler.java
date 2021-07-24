package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultContentHandler implements ContentHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContentHandler.class);

    private final Supplier<List<ParameterFactory<?>>> parameterFactorySupplier;

    private final Supplier<List<PropertyFactory<?>>> propertyFactorySupplier;

    private final Supplier<List<ComponentFactory<?>>> componentFactorySupplier;

    private final TimeZoneRegistry tzRegistry;

    private List<Property> propertiesWithTzId;

    private boolean propertyHasTzId = false;

    private final Consumer<Calendar> consumer;

    private PropertyBuilder propertyBuilder;

    /**
     * The current component builders.
     */
    private final LinkedList<ComponentBuilder<CalendarComponent>> components = new LinkedList<>();

    private Calendar calendar;

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
        calendar = new Calendar();
        components.clear();
        propertiesWithTzId = new ArrayList<>();
    }

    @Override
    public void endCalendar() throws IOException {
        resolveTimezones();

        consumer.accept(calendar);
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
            calendar.getComponents().add(component);
            if (component instanceof VTimeZone && tzRegistry != null) {
                // register the timezone for use with iCalendar objects..
                tzRegistry.register(new TimeZone((VTimeZone) component));
            }
        }
    }

    @Override
    public void startProperty(String name) {
        propertyBuilder = new PropertyBuilder(propertyFactorySupplier.get()).name(name);
        propertyHasTzId = false;
    }

    @Override
    public void propertyValue(String value) {
        propertyBuilder.value(value);
    }

    @Override
    public void endProperty(String name) throws URISyntaxException, ParseException, IOException {
        assertProperty(propertyBuilder);
        Property property = propertyBuilder.build();

        if (propertyHasTzId) {
            propertiesWithTzId.add(property);
        }
        // replace with a constant instance if applicable..
        property = Constants.forProperty(property);
        if (getComponentBuilder() != null) {
            getComponentBuilder().property(property);
        } else if (calendar != null) {
            calendar.getProperties().add(property);
        }

        property = null;
    }

    @Override
    public void parameter(String name, String value) throws URISyntaxException {
        assertProperty(propertyBuilder);

        Parameter parameter = new ParameterBuilder().factories(parameterFactorySupplier.get())
                .name(name).value(value).build();

        if (parameter instanceof TzId && tzRegistry != null) {
            // VTIMEZONE may be defined later, so so keep
            // track of dates until all components have been
            // parsed, and then try again later
            propertyHasTzId = true;
        }

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

    private void resolveTimezones() throws IOException {

        // Go through each property and try to resolve the TZID.
        for (Property property : propertiesWithTzId) {

            TzId tzParam = property.getParameter(Parameter.TZID);

            // extra null check in case validation has removed the TZID param..
            if (tzParam != null) {

                //lookup timezone
                final TimeZone timezone = tzRegistry.getTimeZone(tzParam.getValue());

                // If timezone found, then update date property
                if (timezone != null) {

                    // Get the String representation of date(s) as
                    // we will need this after changing the timezone
                    final String strDate = property.getValue();

                    // Change the timezone
                    if (property instanceof DateProperty) {
                        ((DateProperty) property).setTimeZone(timezone);
                    } else if (property instanceof DateListProperty) {
                        ((DateListProperty) property).setTimeZone(timezone);
                    } else {
                        LOG.warn("Property [%s] doesn't support parameter [%s]", property.getName(), tzParam.getName());
                    }

                    // Reset value
                    try {
                        property.setValue(strDate);
                    } catch (ParseException | URISyntaxException e) {
                        // shouldn't happen as its already been parsed
                        throw new CalendarException(e);
                    }
                }
            }
        }
    }
}
