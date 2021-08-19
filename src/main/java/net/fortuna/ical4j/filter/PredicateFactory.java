package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.filter.expression.BinaryExpression;
import net.fortuna.ical4j.filter.expression.UnaryExpression;

import java.util.List;
import java.util.function.Predicate;

public interface PredicateFactory<T> {

    default Predicate<T> predicate(FilterExpression expression) {
        if (expression instanceof UnaryExpression) {
            return predicate((UnaryExpression) expression);
        } else if (expression instanceof BinaryExpression) {
            return predicate((BinaryExpression) expression);
        }
        throw new IllegalArgumentException("Not a valid filter");
    }

    Predicate<T> predicate(UnaryExpression expression);

    Predicate<T> predicate(BinaryExpression expression);

    static <T> Predicate<T> and(List<Predicate<T>> predicates) {
        // TODO Handle case when argument is null or empty or has only one element
        return predicates.stream().reduce(t -> true, Predicate::and);
    }

    static <T> Predicate<T> or(List<Predicate<T>> predicates) {
        // TODO Handle case when argument is null or empty or has only one element
        return predicates.stream().reduce(t -> true, Predicate::or);
    }
}
