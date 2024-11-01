package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.RelationshipPropertyModifiers;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.transform.Transformer;

import java.util.function.BiFunction;

/**
 * @deprecated use {@link net.fortuna.ical4j.model.PropertyContainer#with(BiFunction, Object)} and
 * {@link net.fortuna.ical4j.model.RelationshipPropertyModifiers#ATTENDEE} instead.
 */
@Deprecated
public class AttendeeUpdate implements Transformer<Component> {

    private final Attendee attendee;

    public AttendeeUpdate(Attendee attendee) {
        this.attendee = attendee;
    }

    @Override
    public Component apply(Component object) {
        object.with(RelationshipPropertyModifiers.ATTENDEE, attendee);
        return object;
    }
}
