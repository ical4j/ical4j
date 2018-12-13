package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.NumberList;
import net.fortuna.ical4j.transform.Transformer;
import net.fortuna.ical4j.util.Dates;

import java.util.Collections;

/**
 * Applies BYSETPOS rules to <code>dates</code>. Valid positions are from 1 to the size of the date list. Invalid
 * positions are ignored.
 */
public class BySetPosRule implements Transformer<DateList> {

    private final NumberList setPosList;

    public BySetPosRule(NumberList setPosList) {
        this.setPosList = setPosList;
    }

    @Override
    public DateList transform(DateList dates) {
        // return if no SETPOS rules specified..
        if (setPosList.isEmpty()) {
            return dates;
        }
        // sort the list before processing..
        Collections.sort(dates);
        final DateList setPosDates = Dates.getDateListInstance(dates);
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
