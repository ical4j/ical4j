package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.component.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

class DefaultComponentFactorySupplier implements Supplier<List<ComponentFactory>> {

    private final List<ComponentFactory> factories;

    public DefaultComponentFactorySupplier(ComponentFactory... extraFactories) {
        factories = new ArrayList<>();
        factories.add(new Available.Factory());
        factories.add(new Daylight.Factory());
        factories.add(new Standard.Factory());
        factories.add(new VAlarm.Factory());
        factories.add(new VAvailability.Factory());
        factories.add(new VEvent.Factory());
        factories.add(new VFreeBusy.Factory());
        factories.add(new VJournal.Factory());
        factories.add(new VTimeZone.Factory());
        factories.add(new VToDo.Factory());
        factories.add(new VVenue.Factory());

        factories.addAll(Arrays.asList(extraFactories));
    }

    @Override
    public List<ComponentFactory> get() {
        return factories;
    }
}
