package net.fortuna.ical4j.transform.calendar;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ChangeManagementPropertyModifiers;
import net.fortuna.ical4j.model.ComponentGroup;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.transform.Transformer;

import java.util.Optional;
import java.util.function.Supplier;

import static net.fortuna.ical4j.model.CalendarPropertyModifiers.METHOD;
import static net.fortuna.ical4j.model.RelationshipPropertyModifiers.UIDGEN;

public abstract class AbstractMethodTransformer implements Transformer<Calendar> {

    private final Method method;

    private final Supplier<Uid> uidGenerator;

    private final boolean incrementSequence;
    private final boolean sameUid;

    AbstractMethodTransformer(Method method, Supplier<Uid> uidGenerator, boolean sameUid, boolean incrementSequence) {
        this.method = method;
        this.uidGenerator = uidGenerator;
        this.incrementSequence = incrementSequence;
        this.sameUid = sameUid;
    }

    @Override
    public Calendar apply(Calendar object) {
        object.with(METHOD, method);

        Optional<Uid> uid = Optional.empty();
        for (CalendarComponent component : object.getComponents()) {
            component.with(UIDGEN, uidGenerator);
            if (uid.isEmpty()) {
                uid = component.getProperty(Property.UID);
            } else if (sameUid && !uid.equals(component.getProperty(Property.UID))) {
                throw new IllegalArgumentException("All components must share the same non-null UID");
            }

            ComponentGroup<CalendarComponent> componentGroup = new ComponentGroup<>(
                    object.getComponents(), uid.get());

            // if a calendar component has already been published previously
            // update the sequence number..
            if (incrementSequence) {
                ChangeManagementPropertyModifiers.SEQUENCE_INCREMENT.apply(componentGroup.getLatestRevision());
            }
        }
        return object;
    }
}
