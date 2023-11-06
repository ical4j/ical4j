package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Component;

import java.util.function.UnaryOperator;

public abstract class AbstractMethodTransfomer implements UnaryOperator<Component> {

    @Override
    public Component apply(Component object) {
        removeProperties(object);
        return object;
    }

    protected abstract void removeProperties(Component component);
}
