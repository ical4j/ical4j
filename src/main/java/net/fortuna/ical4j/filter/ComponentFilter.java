package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.filter.expression.BinaryExpression;
import net.fortuna.ical4j.filter.expression.UnaryExpression;
import net.fortuna.ical4j.filter.predicate.*;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * ComponentFilter produces predicates for lambda-style filtering of {@link Component iCalendar components}.
 * <p>
 * The following example prints all events where the event's categories contain the the word "Holiday".
 * </p>
 * <pre>
 * Predicate<Component> filter = new ComponentFilter().predicate(FilterExpression.contains(Property.CATEGORIES, "Holiday"));
 * calendar.getComponents(Component.VEVENT).stream()
 *   .filter(filter)
 *   .forEach(System.out::println);
 * </pre>
 */
public class ComponentFilter<T extends Component> extends AbstractFilter<T> {

    public ComponentFilter() {
    }

    public ComponentFilter(Supplier<List<PropertyFactory<?>>> propertyFactorySupplier, Supplier<List<ParameterFactory<?>>> parameterFactorySupplier) {
        super(propertyFactorySupplier, parameterFactorySupplier);
    }

    public Predicate<T> predicate(UnaryExpression expression) {
        switch (expression.operator) {
            case not:
                return predicate(expression.operand).negate();
            case exists:
                return new PropertyExistsRule<T>(property(expression));
            case notExists:
                return new PropertyExistsRule<T>(property(expression)).negate();
        }
        throw new IllegalArgumentException("Not a valid filter");
    }

    public Predicate<T> predicate(BinaryExpression expression) {
        switch (expression.operator) {
            case and:
                return predicate(expression.left).and(predicate(expression.right));
            case or:
                return predicate(expression.left).or(predicate(expression.right));
            case equalTo:
                return new PropertyEqualToRule<>(property(expression));
            case notEqualTo:
                return new PropertyEqualToRule<T>(property(expression)).negate();
            case in:
                return new PropertyInRule<>(properties(expression));
            case notIn:
                return new PropertyInRule<T>(properties(expression)).negate();
            case greaterThan:
                return new PropertyGreaterThanRule<>(property(expression));
            case greaterThanEqual:
                return new PropertyGreaterThanRule<>(property(expression), true);
            case lessThan:
                return new PropertyLessThanRule<>(property(expression));
            case lessThanEqual:
                return new PropertyLessThanRule<>(property(expression), true);
            case between:
                List<Comparable<Property>> properties = properties(expression);
                return new PropertyInRangeRule<>(properties.get(0), properties.get(1), true);
            case contains:
                return new PropertyContainsRule<>(property(expression), literal(expression));
            case matches:
                return new PropertyMatchesRule<>(property(expression), literal(expression));
            case startsWith:
                return new PropertyStartsWithRule<>(property(expression), literal(expression));
        }
        throw new IllegalArgumentException("Not a valid filter");
    }
}
