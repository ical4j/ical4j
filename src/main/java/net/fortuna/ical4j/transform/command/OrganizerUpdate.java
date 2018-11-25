package net.fortuna.ical4j.transform.command;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.transform.Transformer;

public class OrganizerUpdate implements Transformer<Component> {

    private final Organizer organizer;

    public OrganizerUpdate(Organizer organizer) {
        this.organizer = organizer;
    }

    @Override
    public Component transform(Component object) {
        PropertyList<Property> props = object.getProperties();
        Organizer oldOrganizer = props.getProperty(Property.ORGANIZER);
        if (oldOrganizer != null) {
            props.remove(oldOrganizer);
        }
        props.add(organizer);
        
        return object;
    }
}
