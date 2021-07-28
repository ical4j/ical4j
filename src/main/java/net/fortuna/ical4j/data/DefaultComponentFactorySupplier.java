package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.component.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class DefaultComponentFactorySupplier implements Supplier<List<ComponentFactory<? extends Component>>> {

    @Override
    public List<ComponentFactory<? extends Component>> get() {
        final List<ComponentFactory<? extends Component>> rfc5545 =
                Arrays.asList(
                        new Daylight.Factory(),
                        new Standard.Factory(),
                        new VAlarm.Factory(),
                        new VEvent.Factory(),
                        new VFreeBusy.Factory(),
                        new VJournal.Factory(),
                        new VTimeZone.Factory(),
                        new VToDo.Factory());

        // Availability
        final List<ComponentFactory<? extends Component>> rfc7953 =
                Arrays.asList(
                        new Available.Factory(),
                        new VAvailability.Factory());

        // Eventpub
        final List<ComponentFactory<? extends Component>> rfc9073 =
                Arrays.asList(
                        new Participant.Factory(),
                        new VLocation.Factory(),
                        new VResource.Factory());

        final List<ComponentFactory<? extends Component>> vvenueDraft =
                Collections.singletonList(new VVenue.Factory());

        final List<ComponentFactory<? extends Component>> factories = new ArrayList<>(rfc5545);

        factories.addAll(rfc7953);
        factories.addAll(rfc9073);
        factories.addAll(vvenueDraft);

        return factories;
    }
}
