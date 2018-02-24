package net.fortuna.ical4j.transform.command;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.transform.Transformer;

/**
 * Created by fortuna on 19/07/2017.
 */
public class SequenceIncrement implements Transformer<CalendarComponent> {

    @Override
    public CalendarComponent transform(CalendarComponent object) {
        PropertyList compProps = object.getProperties();

        Sequence sequence = (Sequence) compProps
                .getProperty(Property.SEQUENCE);

        if (sequence == null) {
            compProps.add(new Sequence(0));
        }
        else {
            compProps.remove(sequence);
            compProps.add(new Sequence(sequence.getSequenceNo() + 1));
        }

        return object;
    }
}
