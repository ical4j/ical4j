package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.transform.Transformer;

import java.util.Optional;

/**
 * Created by fortuna on 19/07/2017.
 */
public class SequenceIncrement implements Transformer<CalendarComponent> {

    @Override
    public CalendarComponent transform(CalendarComponent object) {
        Optional<Sequence> sequence = object.getProperty(Property.SEQUENCE);
        if (sequence.isPresent()) {
            Sequence newSequence = new Sequence(sequence.get().getSequenceNo() + 1);
            return object.replace(newSequence);
        } else {
            return object.add(new Sequence(0));
        }
    }
}
