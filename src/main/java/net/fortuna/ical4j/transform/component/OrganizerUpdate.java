package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.transform.Transformer;

public class OrganizerUpdate implements Transformer<Component> {

    private final Organizer organizer;

    public OrganizerUpdate(Organizer organizer) {
        this.organizer = organizer;
    }

    @Override
    public Component apply(Component object) {
        return object.replace(organizer);
    }
}
