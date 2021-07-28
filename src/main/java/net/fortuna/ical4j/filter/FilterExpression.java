package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterExpression {

    private final Map<String, Object> equalToMap = new HashMap<>();

    private final Map<String, List<?>> inMap = new HashMap<>();

    private final Map<String, Object> greaterThanMap = new HashMap<>();

    private final Map<String, Object> greaterThanEqualMap = new HashMap<>();

    private final Map<String, Object> lessThanMap = new HashMap<>();

    private final Map<String, Object> lessThanEqualMap = new HashMap<>();

    private final Map<String, Object> containsMap = new HashMap<>();

    private final Set<String> existsSet = new HashSet<>();

    private final Set<String> notExistsSet = new HashSet<>();

    public FilterExpression equalTo(String name, Object value) {
        equalToMap.put(name, value);
        return this;
    }

    public FilterExpression in(String name, List<?> value) {
        inMap.put(name, value);
        return this;
    }

    public FilterExpression greaterThan(String name, Object value) {
        greaterThanMap.put(name, value);
        return this;
    }

    public FilterExpression greaterThanEqual(String name, Object value) {
        greaterThanEqualMap.put(name, value);
        return this;
    }

    public FilterExpression lessThan(String name, Object value) {
        lessThanMap.put(name, value);
        return this;
    }

    public FilterExpression lessThanEqual(String name, Object value) {
        lessThanEqualMap.put(name, value);
        return this;
    }

    public FilterExpression contains(String name, Object value) {
        containsMap.put(name, value);
        return this;
    }

    public FilterExpression exists(String name) {
        existsSet.add(name);
        return this;
    }

    public FilterExpression notExists(String name) {
        notExistsSet.add(name);
        return this;
    }

    public static FilterExpression parse(String filterExpression) {
        return new FilterExpressionParser().parse(filterExpression);
    }

    public static <T> Predicate<T> and(List<Predicate> predicates) {
        // TODO Handle case when argument is null or empty or has only one element
        return predicates.stream().reduce(t -> true, Predicate::and);
    }

    public Predicate<Calendar> toCalendarPredicate() {
        Predicate<Calendar> p = and(equalToMap.entrySet().stream().map(e -> new PropertyEqualToRule<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
        p = p.and(and(inMap.entrySet().stream().map(e -> new PropertyInRule<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList())));
        p = p.and(and(greaterThanMap.entrySet().stream().map(e -> new PropertyGreaterThanRule<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList())));
        p = p.and(and(lessThanMap.entrySet().stream().map(e -> new PropertyLessThanRule<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList())));
        p = p.and(and(containsMap.entrySet().stream().map(e -> new PropertyContainsRule<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList())));
        p = p.and(and(existsSet.stream().map(PropertyExistsRule::new)
                .collect(Collectors.toList())));
        p = p.and(and(notExistsSet.stream().map(prop -> new PropertyExistsRule<>(prop).negate())
                .collect(Collectors.toList())));
        return p;
    }

    public <T extends Component> Predicate<T> toComponentPredicate() {
        Predicate<T> p = and(equalToMap.entrySet().stream()
                .map(e -> new PropertyEqualToRule<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
        p = p.and(and(inMap.entrySet().stream()
                .map(e -> new PropertyInRule<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList())));
        p = p.and(and(greaterThanMap.entrySet().stream()
                .map(e -> new PropertyGreaterThanRule<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList())));
        p = p.and(and(greaterThanEqualMap.entrySet().stream()
                .map(e -> new PropertyGreaterThanRule<>(e.getKey(), e.getValue(), true))
                .collect(Collectors.toList())));
        p = p.and(and(lessThanMap.entrySet().stream()
                .map(e -> new PropertyLessThanRule<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList())));
        p = p.and(and(lessThanEqualMap.entrySet().stream()
                .map(e -> new PropertyLessThanRule<>(e.getKey(), e.getValue(), true))
                .collect(Collectors.toList())));
        p = p.and(and(containsMap.entrySet().stream()
                .map(e -> new PropertyContainsRule<>(e.getKey(), e.getValue()))
                .collect(Collectors.toList())));
        p = p.and(and(existsSet.stream().map(PropertyExistsRule::new)
                .collect(Collectors.toList())));
        p = p.and(and(notExistsSet.stream().map(prop -> new PropertyExistsRule<>(prop).negate())
                .collect(Collectors.toList())));

        return p;
    }

    public Predicate<Property> toParameterPredicate() {
        Predicate<Property> p = and(equalToMap.entrySet().stream()
                .map(e -> new ParameterEqualToRule(e.getKey(), e.getValue()))
                .collect(Collectors.toList()));

        return p;
    }
}
