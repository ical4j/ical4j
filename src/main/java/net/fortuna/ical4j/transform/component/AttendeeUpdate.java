package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.property.Attendee;

import java.util.function.UnaryOperator;

public class AttendeeUpdate implements UnaryOperator<Component> {

    private final Attendee attendee;

    public AttendeeUpdate(Attendee attendee) {
        this.attendee = attendee;
    }

    @Override
    public Component apply(Component object) {
        return object.replace(attendee);
    }
}
