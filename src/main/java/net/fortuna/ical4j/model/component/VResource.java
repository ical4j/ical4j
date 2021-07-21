package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;

public class VResource extends Component {

    public VResource() {
        super(VRESOURCE);
    }

    public VResource(PropertyList<Property> p) {
        super(VRESOURCE, p);
    }

    @Override
    public void validate(boolean recurse) throws ValidationException {
        
    }

    public static class Factory extends Content.Factory implements ComponentFactory<VResource> {

        public Factory() {
            super(VRESOURCE);
        }

        @Override
        public VResource createComponent() {
            return new VResource();
        }

        @Override
        public VResource createComponent(PropertyList<Property> properties) {
            return new VResource(properties);
        }

        @Override
        public VResource createComponent(PropertyList<Property> properties, ComponentList<Component> subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", VRESOURCE));
        }
    }
}
