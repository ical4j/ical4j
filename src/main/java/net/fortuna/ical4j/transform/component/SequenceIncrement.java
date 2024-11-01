package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.ChangeManagementPropertyModifiers;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.transform.Transformer;

import java.util.Optional;

/**
 * Created by fortuna on 19/07/2017.
 * @deprecated use {@link net.fortuna.ical4j.model.ChangeManagementPropertyModifiers#SEQUENCE_INCREMENT}
 * instead.
 */
@Deprecated
public class SequenceIncrement implements Transformer<CalendarComponent> {

    @Override
    public CalendarComponent apply(CalendarComponent object) {
        Optional<Sequence> sequence = object.getProperty(Property.SEQUENCE);
        if (sequence.isPresent()) {
            object.with(ChangeManagementPropertyModifiers.SEQUENCE, sequence.get().getSequenceNo() + 1);
        } else {
            object.with(ChangeManagementPropertyModifiers.SEQUENCE, 0);
        }
        return object;
    }
}
