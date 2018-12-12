package net.fortuna.ical4j.transform;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.command.MethodUpdate;
import net.fortuna.ical4j.transform.command.SequenceIncrement;
import net.fortuna.ical4j.transform.command.UidUpdate;
import net.fortuna.ical4j.util.UidGenerator;

public abstract class AbstractMethodTransformer implements Transformer<Calendar> {

    private final Method method;

    private final UidUpdate uidUpdate;
    private final SequenceIncrement sequenceIncrement;

    private final boolean incrementSequence;
    private final boolean sameUid;

    public AbstractMethodTransformer(Method method, UidGenerator uidGenerator, boolean sameUid, boolean incrementSequence) {
        this.method = method;
        this.uidUpdate = new UidUpdate(uidGenerator);
        this.sequenceIncrement = new SequenceIncrement();
        this.incrementSequence = incrementSequence;
        this.sameUid = sameUid;
    }

    @Override
    public Calendar transform(Calendar object) {
        MethodUpdate methodUpdate = new MethodUpdate(method);
        methodUpdate.transform(object);

        Property uid = null;
        for (CalendarComponent component : object.getComponents()) {
            uidUpdate.transform(component);
            if (uid == null) {
                uid = component.getProperty(Property.UID);
            } else if (sameUid && !uid.equals(component.getProperty(Property.UID))) {
                throw new IllegalArgumentException("All components must share the same non-null UID");
            }

            // if a calendar component has already been published previously
            // update the sequence number..
            if (incrementSequence) {
                sequenceIncrement.transform(component);
            }
        }
        return object;
    }
}
