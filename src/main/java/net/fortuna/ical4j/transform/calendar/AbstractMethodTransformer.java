package net.fortuna.ical4j.transform.calendar;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentGroup;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.transform.component.SequenceIncrement;
import net.fortuna.ical4j.util.UidGenerator;

import java.util.Optional;

import static net.fortuna.ical4j.model.CalendarPropertyModifiers.METHOD;
import static net.fortuna.ical4j.model.RelationshipPropertyModifiers.UID;

public abstract class AbstractMethodTransformer implements Transformer<Calendar> {

    private final Method method;

    private final UidGenerator uidGenerator;

    private final SequenceIncrement sequenceIncrement;

    private final boolean incrementSequence;
    private final boolean sameUid;

    AbstractMethodTransformer(Method method, UidGenerator uidGenerator, boolean sameUid, boolean incrementSequence) {
        this.method = method;
        this.uidGenerator = uidGenerator;
        this.sequenceIncrement = new SequenceIncrement();
        this.incrementSequence = incrementSequence;
        this.sameUid = sameUid;
    }

    @Override
    public Calendar apply(Calendar object) {
        object.with(METHOD, method);

        Optional<Uid> uid = Optional.empty();
        for (CalendarComponent component : object.getComponents()) {
            component.with(UID, uidGenerator);
            if (!uid.isPresent()) {
                uid = component.getProperty(Property.UID);
            } else if (sameUid && !uid.equals(component.getProperty(Property.UID))) {
                throw new IllegalArgumentException("All components must share the same non-null UID");
            }

            ComponentGroup<CalendarComponent> componentGroup = new ComponentGroup<>(
                    object.getComponents(), uid.get());

            // if a calendar component has already been published previously
            // update the sequence number..
            if (incrementSequence) {
                sequenceIncrement.apply(componentGroup.getLatestRevision());
            }
        }
        return object;
    }
}
