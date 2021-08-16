package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.filter.FilterExpression.Op;
import net.fortuna.ical4j.filter.expression.BinaryExpression;
import net.fortuna.ical4j.filter.expression.NumberExpression;
import net.fortuna.ical4j.filter.expression.StringExpression;
import net.fortuna.ical4j.filter.expression.TargetExpression;
import net.fortuna.ical4j.model.TemporalAmountAdapter;
import org.jparsec.*;

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

    private static final String[] OPERATORS = {
        ">", "<", "=", ">=", "<=", "<>", ".", "(", ")", "[", "]", ":", ","
    };

    private static final String[] KEYWORDS = {
        "by", "order", "asc", "desc",
        "and", "or", "not", "in", "exists", "between", "is", "null", "like",
        "contains", "matches"
    };

    private static final String[] FUNCTION_NAMES = {
        "now", "startOfDay", "endOfDay", "startOfWeek", "endOfWeek", "startOfMonth", "endOfMonth",
        "startOfYear", "endOfYear", "startOfWeek", "endOfWeek", "startOfMonth", "endOfMonth",
    };

    private static final Terminals TERMS = Terminals.operators(OPERATORS)
            .words(Scanners.IDENTIFIER).caseInsensitiveKeywords(Arrays.asList(KEYWORDS))
            .keywords(FUNCTION_NAMES).build();

    private static final Parser<?> TOKENIZER = Parsers.or(
            Terminals.IntegerLiteral.TOKENIZER, Terminals.StringLiteral.SINGLE_QUOTE_TOKENIZER,
            TERMS.tokenizer());

    static final Parser<FilterTarget.Attribute> ATTRIBUTE_PARSER = Parsers.sequence(Terminals.Identifier.PARSER,
            term(":"), Terminals.Identifier.PARSER, (name, x, value) -> new FilterTarget.Attribute(name, value))
            .or(Terminals.Identifier.PARSER.map(FilterTarget.Attribute::new));

    static final Parser<List<FilterTarget.Attribute>> ATTRIBUTE_LIST_PARSER = ATTRIBUTE_PARSER
            .between(term("["), term("]")).sepBy(term(","));

    static final Parser<NumberExpression> NUMBER = Terminals.IntegerLiteral.PARSER.map(NumberExpression::new);

    static final Parser<StringExpression> STRING = Terminals.StringLiteral.PARSER.map(StringExpression::new);

    static final Parser<TargetExpression> NAME = Parsers.sequence(
                    Terminals.Identifier.PARSER, ATTRIBUTE_LIST_PARSER, TargetExpression::new)
            .or(Terminals.Identifier.PARSER.map(TargetExpression::new));
//    static final Parser<SpecificationExpression> NAME = Terminals.Identifier.PARSER
//            .map(SpecificationExpression::new);

    static final Parser<Void> IGNORED = Parsers.or(
            Scanners.JAVA_LINE_COMMENT,
            Scanners.JAVA_BLOCK_COMMENT,
            Scanners.WHITESPACES).skipMany();

    static final Parser<List<String>> COLLECTION_PARSER = Terminals.StringLiteral.PARSER
            .between(term("("), term(")")).sepBy(term(",")).from(TOKENIZER, IGNORED);

//    static final Parser<StringExpression> STRING = Terminals.StringLiteral.SINGLE_QUOTE_TOKENIZER.map(StringExpression::new);

//    static final Parser<TemporalExpression> TEMPORAL = Terminals.StringLiteral.PARSER.map(TemporalExpression::new);

    static Parser<?> term(String... names) {
        return TERMS.token(names);
    }

    static <T> Parser<T> op(String name, T value) {
        return term(name).retn(value);
    }
    
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
        FilterExpression expression = null;
        for (String part : filterExpression.split("\\s*and\\s*")) {
            if (part.matches("[\\w-]+\\s*>=\\s*\\w+")) {
                String[] greaterThanEqual = part.split("\\s*>=\\s*");
                expression = FilterExpression.greaterThanEqual(greaterThanEqual[0], resolveValue(greaterThanEqual[1]));
            } else if (part.matches("[\\w-]+\\s*<=\\s*\\w+")) {
                String[] lessThanEqual = part.split("\\s*<=\\s*");
                expression = FilterExpression.lessThanEqual(lessThanEqual[0], resolveValue(lessThanEqual[1]));
            } else if (part.matches("[\\w-]+\\s*=\\s*[^<>=]+")) {
                String[] equalTo = part.split("\\s*=\\s*");
                expression = FilterExpression.equalTo(equalTo[0], (String) resolveValue(equalTo[1]));
            } else if (part.matches("[\\w-]+\\s*>\\s*\\w+")) {
                String[] greaterThan = part.split("\\s*>\\s*");
                expression = FilterExpression.greaterThan(greaterThan[0], (Integer) resolveValue(greaterThan[1]));
            } else if (part.matches("[\\w-]+\\s*<\\s*\\w+")) {
                String[] lessThan = part.split("\\s*<\\s*");
                expression = FilterExpression.lessThan(lessThan[0], resolveValue(lessThan[1]));
            } else if (part.matches("[\\w-]+\\s+in\\s+\\[[^<>=]+]")) {
                String[] in = part.split("\\s*in\\s*");
                List<String> items = Arrays.asList(in[1].replaceAll("[\\[\\]]", "")
                        .split("\\[?\\s*,\\s*]?"));
                expression = FilterExpression.in(in[0], items);
            } else if (part.matches("[\\w-]+\\s+contains\\s+\".+\"")) {
                String[] contains = part.split("\\s*contains\\s*");
                expression = FilterExpression.contains(contains[0], contains[1].replaceAll("^\"?|\"?$", ""));
            } else if (part.matches("[\\w-]+\\s+exists")) {
                String[] exists = part.split("\\s*exists");
                expression = FilterExpression.exists(exists[0]);
            } else if (part.matches("[\\w-]+\\s+not exists")) {
                String[] notExists = part.split("\\s*not exists");
                expression = FilterExpression.notExists(notExists[0]);
            } else {
                throw new IllegalArgumentException("Invalid filter expression: " + filterExpression);
            }
        }
        return expression;
    }

    private <T> T resolveValue(String valueString) {
        if (valueString.matches("\\w+\\(.*\\)")
                && FUNCTIONS.containsKey(valueString.replaceAll("\\(.*\\)", ""))) {
            return (T) FUNCTIONS.get(valueString.replaceAll("\\(.*\\)", ""))
                    .apply(valueString.split("\\(|\\)")[1]);
        } else if (valueString.matches("\\d+")) {
            return (T) Integer.valueOf(valueString);
        } else {
            return (T) valueString;
        }
    }

    public static Parser<FilterExpression> newInstance() {
        Parser.Reference<FilterExpression> ref = Parser.newReference();
        Parser<FilterExpression> unit = ref.lazy().between(term("("), term(")"))
                .or(NUMBER).or(NAME).or(STRING); //.or(TEMPORAL);
        Parser<FilterExpression> parser = new OperatorTable<FilterExpression>()
                .infixl(op("=", (l, r) -> new BinaryExpression(l, Op.equalTo, r)), 10)
                .infixl(op("<>", (l, r) -> new BinaryExpression(l, Op.notEqualTo, r)), 10)
                .build(unit);
        ref.set(parser);
        return parser.from(TOKENIZER, IGNORED);
    }
}
