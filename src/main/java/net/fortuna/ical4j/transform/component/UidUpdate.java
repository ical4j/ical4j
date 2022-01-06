package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.UidGenerator;

import java.util.Optional;

public class UidUpdate implements Transformer<Component> {

    private final UidGenerator uidGenerator;

    public UidUpdate(UidGenerator uidGenerator) {
        this.uidGenerator = uidGenerator;
    }

    @Override
    public Component transform(Component object) {
        Optional<Uid> uid = object.getProperty(Property.UID);
        if (!uid.isPresent()) {
            object.add(uidGenerator.generateUid());
        }
        return object;
    }
}
