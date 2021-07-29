package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.TemporalComparator;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Sequence;

import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Test for a property that is by comparison less than the provided value.
 *
 * @param <T>
 */
public class PropertyLessThanRule<T extends Component> implements Predicate<T> {

    private final String propertyName;

    private final Object value;

    private final boolean inclusive;

    public PropertyLessThanRule(String propertyName, Object value) {
        this(propertyName, value, false);
    }

    public PropertyLessThanRule(String propertyName, Object value, boolean inclusive) {
        this.propertyName = propertyName;
        this.value = value;
        this.inclusive = inclusive;
    }

    @Override
    public boolean test(T t) {
        if ("sequence".equalsIgnoreCase(propertyName)) {
            Optional<Sequence> sequence = t.getProperty(propertyName);
            if (sequence.isPresent()) {
                return inclusive ? sequence.get().getSequenceNo() <= Integer.parseInt(value.toString())
                        : sequence.get().getSequenceNo() < Integer.parseInt(value.toString());
            }
        } else if (Arrays.asList("due").contains(propertyName)) {
            Optional<DateProperty> dateProperty = t.getProperty(propertyName);
            if (dateProperty.isPresent()) {
                return inclusive ? new TemporalComparator().compare(dateProperty.get().getDate(), (Temporal) value) < 0
                        : new TemporalComparator().compare(dateProperty.get().getDate(), (Temporal) value) <= 0;
            }
        }
        return false;
    }
}
