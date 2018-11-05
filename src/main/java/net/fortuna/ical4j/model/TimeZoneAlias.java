package net.fortuna.ical4j.model;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Maps a list of alias timezone identifiers to an actual timezone ID.
 */
public class TimeZoneAlias {

    private static final Pattern ALIAS_LINE_PATTERN = Pattern.compile("\\s(.+?)(?=\\s*=)\\s*([a-zA-z0-9/;:+-]+)");

    private final String timeZoneId;

    private final List<String> aliases;

    public TimeZoneAlias(String timeZoneId, String...aliases) {
        this.timeZoneId = timeZoneId;
        this.aliases = Arrays.asList(aliases);
    }

    public static List<TimeZoneAlias> loadAliases(InputStream aliasInputStream) {
        List<TimeZoneAlias> timeZoneAliases = new ArrayList<>();

        try (Scanner s = new Scanner(aliasInputStream)) {
            timeZoneAliases.addAll(findAll(s, ALIAS_LINE_PATTERN)
                    .map(m -> new TimeZoneAlias(m.group(2), m.group(1).split(";"))).collect(Collectors.toList()));
        }
//        Scanner scanner = new Scanner(aliasInputStream);
//        while (scanner.hasNextLine()) {
//            String[] result = scanner.nextLine().split("\\s*=\\s*");
//            String timezoneId = result[1];
//            String[] aliases = result[0].split(";");
//            timeZoneAliases.add(new TimeZoneAlias(timezoneId, aliases));
//        }

        return timeZoneAliases;
    }

    private static Stream<MatchResult> findAll(Scanner s, Pattern pattern) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<MatchResult>(
                1000, Spliterator.ORDERED|Spliterator.NONNULL) {
            public boolean tryAdvance(Consumer<? super MatchResult> action) {
                if(s.findWithinHorizon(pattern, 0)!=null) {
                    action.accept(s.match());
                    return true;
                }
                else return false;
            }
        }, false);
    }

    public static Optional<String> getTimeZoneIdFromAlias(List<TimeZoneAlias> aliases, String aliasId) {
        Optional<TimeZoneAlias> alias = aliases.stream().filter(
                timeZoneAlias -> timeZoneAlias.aliases.contains(aliasId)).findFirst();
        if (alias.isPresent()) {
            return Optional.of(alias.get().timeZoneId);
        }
        else {
            return Optional.empty();
        }
    }
}
