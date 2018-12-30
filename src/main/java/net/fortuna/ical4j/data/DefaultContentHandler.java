package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.util.Constants;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultContentHandler implements ContentHandler {

    private final Supplier<List<ParameterFactory>> parameterFactorySupplier;

    private final Supplier<List<PropertyFactory>> propertyFactorySupplier;

    private final Supplier<List<ComponentFactory>> componentFactorySupplier;

    private final TimeZoneRegistry tzRegistry;

    private List<TzId> datesMissingTimezones;

    private final Consumer<Calendar> consumer;

    private PropertyBuilder propertyBuilder;

    private ComponentBuilder<CalendarComponent> componentBuilder;

    private ComponentBuilder<Component> subComponentBuilder;

    private Calendar calendar;

    public DefaultContentHandler(Consumer<Calendar> consumer, TimeZoneRegistry tzRegistry) {
        this(consumer, tzRegistry, new DefaultParameterFactorySupplier(), new DefaultPropertyFactorySupplier(),
                new DefaultComponentFactorySupplier());
    }

    public DefaultContentHandler(Consumer<Calendar> consumer, TimeZoneRegistry tzRegistry,
                                 Supplier<List<ParameterFactory>> parameterFactorySupplier,
                                 Supplier<List<PropertyFactory>> propertyFactorySupplier,
                                 Supplier<List<ComponentFactory>> componentFactorySupplier) {

        this.consumer = consumer;
        this.tzRegistry = tzRegistry;
        this.parameterFactorySupplier = parameterFactorySupplier;
        this.propertyFactorySupplier = propertyFactorySupplier;
        this.componentFactorySupplier = componentFactorySupplier;
    }

    @Override
    public void startCalendar() {
        calendar = new Calendar();
        datesMissingTimezones = new ArrayList<>();
    }

    @Override
    public void endCalendar() throws IOException {
        if (datesMissingTimezones.size() > 0 && tzRegistry != null) {
            for (CalendarComponent component : calendar.getComponents()) {
                resolveTimezones(component.getProperties());

                if (component instanceof VEvent) {
                    for (Component alarm : ((VEvent) component).getAlarms()) {
                        resolveTimezones(alarm.getProperties());
                    }
                }
            }
        }
        consumer.accept(calendar);
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
            calendar.getComponents().add(component);
            if (component instanceof VTimeZone && tzRegistry != null) {
                // register the timezone for use with iCalendar objects..
                tzRegistry.register(new TimeZone((VTimeZone) component));
            }

            componentBuilder = null;
        }
    }

    @Override
    public void startProperty(String name) {
        propertyBuilder = new PropertyBuilder().factories(propertyFactorySupplier.get()).name(name);
    }

    @Override
    public void propertyValue(String value) {
        propertyBuilder.value(value);
    }

    @Override
    public void endProperty(String name) throws URISyntaxException, ParseException, IOException {
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
//            final TimeZone timezone = tzRegistry.getTimeZone(parameter.getValue());
//            if (timezone != null) {
//                propertyBuilder.timeZone(timezone);
//            } else {
                // VTIMEZONE may be defined later, so so keep
                // track of dates until all components have been
                // parsed, and then try again later
                datesMissingTimezones.add((TzId) parameter);
//            }
        }

        propertyBuilder.parameter(parameter);
    }

    private void assertComponent(ComponentBuilder component) {
        if (component == null) {
            throw new CalendarException("Expected component not initialised");
        }
    }

    private void assertProperty(PropertyBuilder property) {
        if (property == null) {
            throw new CalendarException("Expected property not initialised");
        }
    }

    private void resolveTimezones(List<Property> properties) throws IOException {

        // Go through each property and try to resolve the TZID.
        for (TzId tzParam : datesMissingTimezones) {

            //lookup timezone
            final TimeZone timezone = tzRegistry.getTimeZone(tzParam.getValue());

            // If timezone found, then update date property
            if (timezone != null) {
                for (Property property : properties) {
                    if (tzParam.equals(property.getParameter(Parameter.TZID))) {

                        // Get the String representation of date(s) as
                        // we will need this after changing the timezone
                        final String strDate = property.getValue();

                        // Change the timezone
                        if (property instanceof DateProperty) {
                            ((DateProperty) property).setTimeZone(timezone);
                        } else if (property instanceof DateListProperty) {
                            ((DateListProperty) property).setTimeZone(timezone);
                        } else {
                            throw new CalendarException("Invalid parameter: " + tzParam.getName());
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
}
