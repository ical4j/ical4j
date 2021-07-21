package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;

import java.util.stream.Collectors;

public class Participant extends Component {

    private ComponentList<VLocation> locations;

    private ComponentList<VResource> resources;

    public Participant() {
        this(new PropertyList());
    }

    public Participant(PropertyList p) {
        super(PARTICIPANT, p);
        this.locations = new ComponentList<>();
        this.resources = new ComponentList<>();
    }

    public Participant(PropertyList p, final ComponentList<? extends Component> subComponents) {
        super(PARTICIPANT, p);
        this.locations = new ComponentList<>(subComponents.getAll().stream()
                .filter(c -> c.getName().equals(VLOCATION)).map(c -> (VLocation) c)
                .collect(Collectors.toList()));
        this.resources = new ComponentList<>(subComponents.getAll().stream()
                .filter(c -> c.getName().equals(VRESOURCE)).map(c -> (VResource) c)
                .collect(Collectors.toList()));
    }

    public ComponentList<VLocation> getLocations() {
        return locations;
    }

    public ComponentList<VResource> getResources() {
        return resources;
    }

    public void add(VLocation location) {
        this.locations = (ComponentList<VLocation>) locations.add(location);
    }

    public void add(VResource resource) {
        this.resources = (ComponentList<VResource>) resources.add(resource);
    }

    @Override
    public void validate(boolean recurse) throws ValidationException {
        
    }

    @Override
    public final String toString() {
        return BEGIN + ':' + getName() + Strings.LINE_SEPARATOR +
                getProperties() +
                getLocations() +
                getResources() +
                END + ':' + getName() + Strings.LINE_SEPARATOR;
    }

    @Override
    protected ComponentFactory<Participant> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements ComponentFactory<Participant> {

        public Factory() {
            super(PARTICIPANT);
        }

        @Override
        public Participant createComponent() {
            return new Participant();
        }

        @Override
        public Participant createComponent(PropertyList properties) {
            return new Participant(properties);
        }

        @Override
        public Participant createComponent(PropertyList properties, ComponentList<? extends Component> subComponents) {
            return new Participant(properties, subComponents);
        }
    }
}
