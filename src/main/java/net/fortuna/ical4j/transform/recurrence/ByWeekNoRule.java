package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.Recur.Frequency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR;

/**
 * Applies BYWEEKNO rules specified in this Recur instance to the specified date list. If no BYWEEKNO rules are
 * specified the date list is returned unmodified.
 */
public class ByWeekNoRule<T extends Temporal> extends AbstractDateExpansionRule<T> {

    private transient Logger log = LoggerFactory.getLogger(ByWeekNoRule.class);

    private final List<Integer> weekNoList;

    public ByWeekNoRule(List<Integer> weekNoList, Frequency frequency) {
        super(frequency);
        this.weekNoList = weekNoList;
    }

    @Override
    public List<T> transform(List<T> dates) {
        if (weekNoList.isEmpty()) {
            return dates;
        }
        final List<T> weekNoDates = new ArrayList<>();
        for (final T date : dates) {
            final int numWeeksInYear = (int) IsoFields.WEEK_OF_WEEK_BASED_YEAR.rangeRefinedBy(date).getMaximum();
            for (final Integer weekNo : weekNoList) {
                if (weekNo == 0 || weekNo < -numWeeksInYear || weekNo > numWeeksInYear) {
                    if (log.isTraceEnabled()) {
                        log.trace("Invalid week of year: " + weekNo);
                    }
                    continue;
                }
                T candidate;
                if (weekNo > 0) {
                    if (numWeeksInYear < weekNo) {
                        continue;
                    }
                    candidate = withTemporalField(date, ALIGNED_WEEK_OF_YEAR, weekNo);
                } else {
                    if (numWeeksInYear < -weekNo) {
                        continue;
                    }
                    candidate = withTemporalField(date, ALIGNED_WEEK_OF_YEAR, numWeeksInYear + 1 + weekNo);
                }
                weekNoDates.add(candidate);
            }
        }
        return weekNoDates;
    }

    /**
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        log = LoggerFactory.getLogger(Recur.class);
    }
}
