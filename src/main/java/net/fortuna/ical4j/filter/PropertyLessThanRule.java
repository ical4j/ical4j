package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.Sequence;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;
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
            Sequence sequence = t.getProperty(propertyName);
            if (sequence != null) {
                return inclusive ? sequence.getSequenceNo() <= Integer.parseInt(value.toString())
                        : sequence.getSequenceNo() < Integer.parseInt(value.toString());
            }
        } else if (Arrays.asList("due").contains(propertyName)) {
            DateProperty dateProperty = t.getProperty(propertyName);
            return inclusive ? dateProperty.getDate().compareTo(Date.from(Instant.from((Temporal) value))) < 0
                    : dateProperty.getDate().compareTo(Date.from(Instant.from((Temporal) value))) <= 0;
        }
        return false;
    }
}
