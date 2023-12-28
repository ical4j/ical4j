package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.transform.Transformer;

public abstract class AbstractMethodTransfomer implements Transformer<Component> {

    @Override
    public Component apply(Component object) {
        removeProperties(object);
        return object;
    }

    protected abstract void removeProperties(Component component);
}
