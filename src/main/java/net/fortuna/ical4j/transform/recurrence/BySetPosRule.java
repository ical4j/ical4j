package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.TemporalComparator;
import net.fortuna.ical4j.transform.Transformer;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Applies BYSETPOS rules to <code>dates</code>. Valid positions are from 1 to the size of the date list. Invalid
 * positions are ignored.
 */
public class BySetPosRule<T extends Temporal> implements Transformer<List<T>> {

    private static final Comparator<Temporal> ONSET_COMPARATOR = new TemporalComparator();

    private final List<Integer> setPosList;

    public BySetPosRule(List<Integer> setPosList) {
        this.setPosList = setPosList;
    }

    @Override
    public List<T> transform(List<T> dates) {
        // return if no SETPOS rules specified..
        if (setPosList.isEmpty() || dates.isEmpty()) {
            return dates;
        }
        // sort the list before processing..
        if (dates.get(0).isSupported(ChronoUnit.SECONDS)) {
            dates.sort(ONSET_COMPARATOR);
        } else {
            dates.sort(new TemporalComparator(ChronoUnit.DAYS));
        }
        final List<T> setPosDates = new ArrayList<>();
        final int size = dates.size();
        for (final Integer setPos : setPosList) {
            final int pos = setPos;
            if (pos > 0 && pos <= size) {
                setPosDates.add(dates.get(pos - 1));
            } else if (pos < 0 && pos >= -size) {
                setPosDates.add(dates.get(size + pos));
            }
        }
        return setPosDates;
    }
}
