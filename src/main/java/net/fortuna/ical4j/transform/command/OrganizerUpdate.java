package net.fortuna.ical4j.transform.command;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.transform.Transformer;

import java.util.Optional;

public class OrganizerUpdate implements Transformer<Component> {

    private final Organizer organizer;

    public OrganizerUpdate(Organizer organizer) {
        this.organizer = organizer;
    }

    @Override
    public Component transform(Component object) {
        PropertyList props = object.getProperties();
        Optional<Organizer> oldOrganizer = props.getProperty(Property.ORGANIZER);
        oldOrganizer.ifPresent(props::remove);
        props.add(organizer);
        
        return object;
    }
}
