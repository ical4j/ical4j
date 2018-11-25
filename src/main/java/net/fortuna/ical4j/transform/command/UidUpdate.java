package net.fortuna.ical4j.transform.command;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.UidGenerator;

public class UidUpdate implements Transformer<Component> {

    private final UidGenerator uidGenerator;

    public UidUpdate(UidGenerator uidGenerator) {
        this.uidGenerator = uidGenerator;
    }

    @Override
    public Component transform(Component object) {
        Uid uid = object.getProperties().getProperty(Property.UID);
        if (uid == null) {
            object.getProperties().add(uidGenerator.generateUid());
        }
        return object;
    }
}
