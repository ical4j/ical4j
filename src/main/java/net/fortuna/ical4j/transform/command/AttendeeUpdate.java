package net.fortuna.ical4j.transform.command;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.transform.Transformer;

import java.util.List;

public class AttendeeUpdate implements Transformer<Component> {

    private final Attendee attendee;

    public AttendeeUpdate(Attendee attendee) {
        this.attendee = attendee;
    }

    @Override
    public Component transform(Component object) {
        List<Property> attendees = object.getProperties().getProperties(Property.ATTENDEE);
        attendees.remove(attendee);
        attendees.add(attendee);
        object.getProperties().addAll(attendees);

        return object;
    }
}
