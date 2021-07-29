package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.model.Component;

import java.util.function.Predicate;

/**
 * Test for a property that "contains" the provided value.
 *
 * @param <T>
 */
public class PropertyContainsRule<T extends Component> implements Predicate<T> {

    private final String propertyName;

    private final Object value;

    public PropertyContainsRule(String propertyName, Object value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    public boolean test(T t) {
        return t.getProperties().get(propertyName).stream().anyMatch(p -> p.getValue().contains(value.toString()));
    }
}
