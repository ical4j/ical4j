package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

@Deprecated
public class ComponentDeclineCounterTransformer extends AbstractMethodTransfomer {

    @Override
    protected void removeProperties(Component component) {
        // remove properties not applicable for METHOD=REFRESH..
        component.removeAll(Property.DTSTART, Property.DTEND, Property.DURATION);
    }
}
