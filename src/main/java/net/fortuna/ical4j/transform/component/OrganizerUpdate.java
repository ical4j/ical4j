package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.RelationshipPropertyModifiers;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.transform.Transformer;

import java.util.function.BiFunction;

/**
 * @deprecated use {@link net.fortuna.ical4j.model.PropertyContainer#with(BiFunction, Object)} and
 * {@link net.fortuna.ical4j.model.RelationshipPropertyModifiers#ORGANIZER} instead.
 */
@Deprecated
public class OrganizerUpdate implements Transformer<Component> {

    private final Organizer organizer;

    public OrganizerUpdate(Organizer organizer) {
        this.organizer = organizer;
    }

    @Override
    public Component apply(Component object) {
        object.with(RelationshipPropertyModifiers.ORGANIZER, organizer);
        return object;
    }
}
