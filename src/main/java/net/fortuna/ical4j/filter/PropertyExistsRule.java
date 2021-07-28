package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.model.Component;

import java.util.function.Predicate;

/**
 * Test for a property matching any values in the provided list. Supports test for missing property types
 * (e.g. "DUE", "ORGANIZER", etc.), or missing a property with a specific value (e.g. ROLE=CHAIR, etc.).
 *
 * @param <T>
 */
public class PropertyExistsRule<T extends Component> implements Predicate<T> {

    private final String propertyName;

    public PropertyExistsRule(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public boolean test(T t) {
        String[] prop = propertyName.split(":");
        if (prop.length > 1) {
            return new PropertyEqualToRule<>(prop[0], prop[1]).test(t);
        }
        return !t.getProperties(propertyName).isEmpty();
    }
}
