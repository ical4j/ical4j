package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.Method;

/**
 * Created by fortuna on 19/07/2017.
 */
public abstract class AbstractUserAgent implements UserAgent {

    private final Property role;

    private final Method method;

    public AbstractUserAgent(Property role, Method method) {
        this.role = role;
        this.method = method;
    }

    @Override
    public Property getRole() {
        return role;
    }

    @Override
    public Method getMethod() {
        return method;
    }
}
