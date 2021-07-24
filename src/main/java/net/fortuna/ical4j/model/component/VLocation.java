package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;

public class VLocation extends Component {

    public VLocation() {
        super(VLOCATION);
    }

    public VLocation(PropertyList<Property> p) {
        super(VLOCATION, p);
    }

    @Override
    public void validate(boolean recurse) throws ValidationException {
        
    }

    public static class Factory extends Content.Factory implements ComponentFactory<VLocation> {

        public Factory() {
            super(VLOCATION);
        }

        @Override
        public VLocation createComponent() {
            return new VLocation();
        }

        @Override
        public VLocation createComponent(PropertyList<Property> properties) {
            return new VLocation(properties);
        }

        @Override
        public VLocation createComponent(PropertyList<Property> properties, ComponentList<Component> subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", VLOCATION));
        }
    }
}
