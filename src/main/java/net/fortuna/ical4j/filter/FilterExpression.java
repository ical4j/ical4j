package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.filter.expression.*;

import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface FilterExpression {

    enum Op {
        // comparison operators..
        equalTo, notEqualTo, in, notIn,
        greaterThan, greaterThanEqual, lessThan, lessThanEqual, between,
        // object matching operators..
        exists, notExists,
        // value matching operators..
        contains, matches,
        // logical operators..
        and, or, not
    }

    static FilterExpression equalTo(String target, String value) {
        return new BinaryExpression(new TargetExpression(target), Op.equalTo, new StringExpression(value));
    }

    static FilterExpression equalTo(String target, List<FilterTarget.Attribute> attributes, String value) {
        return new BinaryExpression(new TargetExpression(target, attributes), Op.equalTo, new StringExpression(value));
    }

    static FilterExpression equalTo(String target, Date value) {
        return new BinaryExpression(new TargetExpression(target), Op.equalTo, new DateExpression(value));
    }

    static FilterExpression equalTo(String target, Integer value) {
        return new BinaryExpression(new TargetExpression(target), Op.equalTo, new NumberExpression(value));
    }

    static FilterExpression in(String target, Collection<?> value) {
        return new BinaryExpression(new TargetExpression(target), Op.in, new CollectionExpression(value));
    }

    static FilterExpression greaterThan(String target, Temporal value) {
        return new BinaryExpression(new TargetExpression(target), Op.greaterThan, new StringExpression(value.toString()));
    }

    static FilterExpression greaterThan(String target, Number value) {
        return new BinaryExpression(new TargetExpression(target), Op.greaterThan, new StringExpression(value.toString()));
    }

    static FilterExpression greaterThanEqual(String target, Temporal value) {
        return new BinaryExpression(new TargetExpression(target), Op.greaterThanEqual, new TemporalExpression(value));
    }

    static FilterExpression lessThan(String target, Temporal value) {
        return new BinaryExpression(new TargetExpression(target), Op.lessThan, new TemporalExpression(value));
    }

    static FilterExpression lessThanEqual(String target, String value) {
        return new BinaryExpression(new TargetExpression(target), Op.lessThanEqual, new StringExpression(value));
    }

    static FilterExpression contains(String target, String value) {
        return new BinaryExpression(new TargetExpression(target), Op.contains, new StringExpression(value));
    }

    static FilterExpression matches(String target, String value) {
        return new BinaryExpression(new TargetExpression(target), Op.matches, new StringExpression(value));
    }

    default FilterExpression and(FilterExpression expression) {
        return new BinaryExpression(this, Op.and, expression);
    }

    default FilterExpression or(FilterExpression expression) {
        return new BinaryExpression(this, Op.or, expression);
    }

    static FilterExpression not(FilterExpression expression) {
        return new UnaryExpression(Op.not, expression);
    }

    static FilterExpression exists(String target) {
        return new UnaryExpression(Op.exists, new TargetExpression(target));
    }

    static FilterExpression notExists(String target) {
        return new UnaryExpression(Op.notExists, new TargetExpression(target));
    }

    static FilterExpression parse(String filterExpression) {
        return new FilterExpressionParser().parse(filterExpression);
    }
}
