/*
 * Copyright (c) 2012-2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package net.fortuna.ical4j.filter.predicate;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;

import java.util.function.Predicate;

/**
 * $Id$
 *
 * Created on 2/02/2006
 *
 * A rule that matches any component that occurs in the specified time period.
 * @author Ben Fortuna
 */
public class PeriodRule<T extends Component> implements Predicate<T> {

    private Period period;

    /**
     * Constructs a new instance using the specified period.
     * @param period a period instance to match on
     */
    public PeriodRule(final Period period) {
        this.period = period;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean test(final Component component) {

        /*
        DtStart start = (DtStart) component.getProperty(Property.DTSTART);
        DtEnd end = (DtEnd) component.getProperty(Property.DTEND);
        Duration duration = (Duration) component.getProperty(Property.DURATION);
        
        if (start == null) {
            return false;
        }
        
        // detect events that consume no time..
        if (end == null && duration == null) {
            if (period.includes(start.getDate(), Period.INCLUSIVE_START)) {
                return true;
            }
        }
        */
        
//        try {
        final PeriodList recurrenceSet = component.calculateRecurrenceSet(period);
        return (!recurrenceSet.isEmpty());
//        }
//        catch (ValidationException ve) {
//            log.error("Invalid component data", ve);
//            return false;
//        }
    }
}
