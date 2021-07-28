package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.model.Component;

import java.util.List;
import java.util.function.Predicate;

/**
 * Test for a property matching any values in the provided list.
 *
 * @param <T>
 */
public class PropertyInRule<T extends Component> implements Predicate<T> {

    private final String propertyName;

    private final List<?> value;

    public PropertyInRule(String propertyName, List<?> value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public boolean test(Component t) {
        return value.stream().anyMatch(value -> new PropertyEqualToRule<>(propertyName, value).test(t));
    }
}
