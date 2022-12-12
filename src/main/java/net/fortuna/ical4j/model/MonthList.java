package net.fortuna.ical4j.model;

import java.time.temporal.ChronoField;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class MonthList extends ArrayList<Month> {

    private final ValueRange valueRange;

    public MonthList() {
        this(ChronoField.MONTH_OF_YEAR.range());
    }

    public MonthList(ValueRange range) {
        this.valueRange = range;
    }

    public MonthList(String aString) {
        this(aString, ChronoField.MONTH_OF_YEAR.range());
    }

    public MonthList(String aString, ValueRange valueRange) {
        this(valueRange);
        addAll(Arrays.stream(aString.split(",")).map(Month::parse).collect(Collectors.toList()));
    }

    @Override
    public final boolean add(final Month month) {
        if (!valueRange.isValidValue(month.getMonthOfYear())) {
            throw new IllegalArgumentException(
                    "Value not in range [" + valueRange + "]: " + month);
        }
        return super.add(month);
    }

    @Override
    public boolean addAll(Collection<? extends Month> c) {
        Optional<? extends Month> invalidMonth = c.stream().filter(m -> !valueRange.isValidValue(m.getMonthOfYear()))
                .findFirst();
        if (invalidMonth.isPresent()) {
            throw new IllegalArgumentException(
                    "Value not in range [" + valueRange + "]: " + invalidMonth);
        }
        return super.addAll(c);
    }

    @Override
    public final String toString() {
        return stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
