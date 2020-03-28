package net.fortuna.ical4j.transform.command;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
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
        PropertyList compProps = object.getProperties();

        Optional<Sequence> sequence = compProps.getProperty(Property.SEQUENCE);
        if (sequence.isPresent()) {
            compProps.remove(sequence.get());
            compProps.add(new Sequence(sequence.get().getSequenceNo() + 1));
        }
        else {
            compProps.add(new Sequence(0));
        }

        return object;
    }
}
