package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Property;

/**
 * Created by fortuna on 19/07/2017.
 */
public abstract class AbstractUserAgent implements UserAgent {

    private final Property role;

    public AbstractUserAgent(Property role) {
        this.role = role;
    }

    @Override
    public Property getRole() {
        return role;
    }
}
