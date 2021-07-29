package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.property.Sequence;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Test for a property that is by comparison greater than the provided value.
 *
 * @param <T>
 */
public class PropertyGreaterThanRule<T extends Component> implements Predicate<T> {

    private final String propertyName;

    private final Object value;

    private final boolean inclusive;

    public PropertyGreaterThanRule(String propertyName, Object value) {
        this(propertyName, value, false);
    }

    public PropertyGreaterThanRule(String propertyName, Object value, boolean inclusive) {
        this.propertyName = propertyName;
        this.value = value;
        this.inclusive = inclusive;
    }

    @Override
    public boolean test(T t) {
        if ("sequence".equalsIgnoreCase(propertyName)) {
            Optional<Sequence> sequence = t.getProperty(propertyName);
            if (sequence.isPresent()) {
                return inclusive ? sequence.get().getSequenceNo() >= Integer.parseInt(value.toString())
                        : sequence.get().getSequenceNo() > Integer.parseInt(value.toString());
            }
        }
        return false;
    }
}
