package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.filter.expression.*;

import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;

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

    static FilterExpression equalTo(String operand, String value) {
        return new BinaryExpression(new SpecificationExpression(operand), Op.equalTo, new StringExpression(value));
    }

    static FilterExpression equalTo(String operand, Date value) {
        return new BinaryExpression(new SpecificationExpression(operand), Op.equalTo, new DateExpression(value));
    }

    static FilterExpression equalTo(String operand, Integer value) {
        return new BinaryExpression(new SpecificationExpression(operand), Op.equalTo, new NumberExpression(value));
    }

    static FilterExpression in(String name, Collection<?> value) {
        return new BinaryExpression(new SpecificationExpression(name), Op.in, new CollectionExpression(value));
    }

    static FilterExpression greaterThan(String name, Temporal value) {
        return new BinaryExpression(new SpecificationExpression(name), Op.greaterThan, new StringExpression(value.toString()));
    }

    static FilterExpression greaterThan(String name, Number value) {
        return new BinaryExpression(new SpecificationExpression(name), Op.greaterThan, new StringExpression(value.toString()));
    }

    static FilterExpression greaterThanEqual(String name, Temporal value) {
        return new BinaryExpression(new SpecificationExpression(name), Op.greaterThanEqual, new TemporalExpression(value));
    }

    static FilterExpression lessThan(String name, Temporal value) {
        return new BinaryExpression(new SpecificationExpression(name), Op.lessThan, new TemporalExpression(value));
    }

    static FilterExpression lessThanEqual(String name, String value) {
        return new BinaryExpression(new SpecificationExpression(name), Op.lessThanEqual, new StringExpression(value));
    }

    static FilterExpression contains(String name, String value) {
        return new BinaryExpression(new SpecificationExpression(name), Op.contains, new StringExpression(value));
    }

    static FilterExpression matches(String name, String value) {
        return new BinaryExpression(new SpecificationExpression(name), Op.matches, new StringExpression(value));
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

    static FilterExpression exists(String name) {
        return new UnaryExpression(Op.exists, new SpecificationExpression(name));
    }

    static FilterExpression notExists(String name) {
        return new UnaryExpression(Op.notExists, new SpecificationExpression(name));
    }

    static FilterExpression parse(String filterExpression) {
        return new FilterExpressionParser().parse(filterExpression);
    }
}
