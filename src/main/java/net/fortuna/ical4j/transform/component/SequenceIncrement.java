package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Sequence;

import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Created by fortuna on 19/07/2017.
 */
public class SequenceIncrement implements UnaryOperator<CalendarComponent> {

    @Override
    public CalendarComponent apply(CalendarComponent object) {
        Optional<Sequence> sequence = object.getProperty(Property.SEQUENCE);
        if (sequence.isPresent()) {
            Sequence newSequence = new Sequence(sequence.get().getSequenceNo() + 1);
            return object.replace(newSequence);
        } else {
            return object.add(new Sequence(0));
        }
    }
}
