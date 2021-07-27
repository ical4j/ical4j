package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;

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
        final PropertyList<Property> properties = t.getProperties(propertyName);
        for (final Property p : properties) {
            if (p.getValue().contains(value.toString())) {
                return true;
            }
        }
        return false;
    }
}
