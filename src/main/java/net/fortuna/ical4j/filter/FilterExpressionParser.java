package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.model.TemporalAmountAdapter;

import java.time.*;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalAmount;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Support for parsing a query string to produce a {@link FilterExpression} instance.
 */
public class FilterExpressionParser {

    private static final Map<String, Function<String, ?>> FUNCTIONS = new HashMap<>();
    static {
        FUNCTIONS.put("now", (Function<String, Temporal>) s -> {
            if (!s.isEmpty()) {
                TemporalAmount temporalAmount = TemporalAmountAdapter.parse(s).getDuration();
                return Instant.now().plus(temporalAmount);
            }
            return Instant.now();
        });
        FUNCTIONS.put("startOfDay", (Function<String, Temporal>) s -> {
            if (!s.isEmpty()) {
                TemporalAmount temporalAmount = TemporalAmountAdapter.parse(s).getDuration();
                return LocalDate.now().atStartOfDay().plus(temporalAmount);
            }
            return LocalDate.now().atStartOfDay();
        });
        FUNCTIONS.put("endOfDay", (Function<String, Temporal>) s -> {
            if (!s.isEmpty()) {
                TemporalAmount temporalAmount = TemporalAmountAdapter.parse(s).getDuration();
                return LocalDate.now().atTime(23, 59).plus(temporalAmount);
            }
            return LocalDate.now().atTime(23, 59);
        });
        FUNCTIONS.put("startOfWeek", (Function<String, Temporal>) s -> {
            DayOfWeek first = WeekFields.ISO.getFirstDayOfWeek();
            if (!s.isEmpty()) {
                TemporalAmount temporalAmount = TemporalAmountAdapter.parse(s).getDuration();
                return LocalDate.now().with(TemporalAdjusters.previousOrSame(first)).plus(temporalAmount);
            }
            return LocalDate.now().with(TemporalAdjusters.previousOrSame(first));
        });
        FUNCTIONS.put("endOfWeek", (Function<String, Temporal>) s -> {
            DayOfWeek last = DayOfWeek.of(WeekFields.ISO.getMinimalDaysInFirstWeek());
            if (!s.isEmpty()) {
                TemporalAmount temporalAmount = TemporalAmountAdapter.parse(s).getDuration();
                return LocalDate.now().with(TemporalAdjusters.nextOrSame(last)).plus(temporalAmount);
            }
            return LocalDate.now().with(TemporalAdjusters.nextOrSame(last));
        });
        FUNCTIONS.put("startOfMonth", (Function<String, Temporal>) s -> {
            if (!s.isEmpty()) {
                TemporalAmount temporalAmount = TemporalAmountAdapter.parse(s).getDuration();
                return YearMonth.now().atDay(1).atStartOfDay().plus(temporalAmount);
            }
            return YearMonth.now().atDay(1).atStartOfDay();
        });
        FUNCTIONS.put("endOfMonth", (Function<String, Temporal>) s -> {
            if (!s.isEmpty()) {
                TemporalAmount temporalAmount = TemporalAmountAdapter.parse(s).getDuration();
                return YearMonth.now().atEndOfMonth().atTime(23, 59).plus(temporalAmount);
            }
            return YearMonth.now().atEndOfMonth().atTime(23, 59);
        });
        FUNCTIONS.put("startOfYear", (Function<String, Temporal>) s -> {
            if (!s.isEmpty()) {
                TemporalAmount temporalAmount = TemporalAmountAdapter.parse(s).getDuration();
                return Year.now().atMonth(1).atDay(1).atStartOfDay().plus(temporalAmount);
            }
            return Year.now().atMonth(1).atDay(1).atStartOfDay();
        });
        FUNCTIONS.put("endOfYear", (Function<String, Temporal>) s -> {
            if (!s.isEmpty()) {
                TemporalAmount temporalAmount = TemporalAmountAdapter.parse(s).getDuration();
                return Year.now().atMonth(12).atEndOfMonth().atTime(23, 59).plus(temporalAmount);
            }
            return Year.now().atMonth(12).atEndOfMonth().atTime(23, 59);
        });
    }

    public FilterExpression parse(String filterExpression) {
        FilterExpression expression = new FilterExpression();
        Arrays.stream(filterExpression.split("\\s*and\\s*")).forEach(part -> {
            if (part.matches("[\\w-]+\\s*>=\\s*\\w+")) {
                String[] greaterThanEqual = part.split("\\s*>=\\s*");
                expression.greaterThanEqual(greaterThanEqual[0], resolveValue(greaterThanEqual[1]));
            } else if (part.matches("[\\w-]+\\s*<=\\s*\\w+")) {
                String[] lessThanEqual = part.split("\\s*<=\\s*");
                expression.lessThanEqual(lessThanEqual[0], resolveValue(lessThanEqual[1]));
            } else if (part.matches("[\\w-]+\\s*=\\s*[^<>=]+")) {
                String[] equalTo = part.split("\\s*=\\s*");
                expression.equalTo(equalTo[0], resolveValue(equalTo[1]));
            } else if (part.matches("[\\w-]+\\s*>\\s*\\w+")) {
                String[] greaterThan = part.split("\\s*>\\s*");
                expression.greaterThan(greaterThan[0], resolveValue(greaterThan[1]));
            } else if (part.matches("[\\w-]+\\s*<\\s*\\w+")) {
                String[] lessThan = part.split("\\s*<\\s*");
                expression.lessThan(lessThan[0], resolveValue(lessThan[1]));
            } else if (part.matches("[\\w-]+\\s+in\\s+\\[[^<>=]+]")) {
                String[] in = part.split("\\s*in\\s*");
                List<String> items = Arrays.asList(in[1].replaceAll("[\\[\\]]", "")
                        .split("\\[?\\s*,\\s*]?"));
                expression.in(in[0], items);
            } else if (part.matches("[\\w-]+\\s+contains\\s+\".+\"")) {
                String[] contains = part.split("\\s*contains\\s*");
                expression.contains(contains[0], contains[1].replaceAll("^\"?|\"?$", ""));
            } else if (part.matches("[\\w-]+\\s+exists")) {
                String[] exists = part.split("\\s*exists");
                expression.exists(exists[0]);
            } else if (part.matches("[\\w-]+\\s+not exists")) {
                String[] notExists = part.split("\\s*not exists");
                expression.notExists(notExists[0]);
            } else {
                throw new IllegalArgumentException("Invalid filter expression: " + filterExpression);
            }
        });
        return expression;
    }

    private Object resolveValue(String valueString) {
        if (valueString.matches("\\w+\\(.*\\)")
                && FUNCTIONS.containsKey(valueString.replaceAll("\\(.*\\)", ""))) {
            return FUNCTIONS.get(valueString.replaceAll("\\(.*\\)", ""))
                    .apply(valueString.split("\\(|\\)")[1]);
        } else {
            return valueString;
        }
    }
}
