/*
 * Copyright (c) 2012-2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package net.fortuna.ical4j.filter.predicate;

import net.fortuna.ical4j.model.DateRange;

import java.util.Date;
import java.util.function.Predicate;

/**
 * @author fortuna
 *
 */
public class DateInRangeRule implements Predicate<Date> {

    private final DateRange range;
    
    private final int inclusiveMask;
    
    /**
     * @param range the range to check
     * @param inclusiveMask indicates inclusiveness of start and end of the range
     */
    public DateInRangeRule(DateRange range, int inclusiveMask) {
        this.range = range;
        this.inclusiveMask = inclusiveMask;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(Date date) {
        return range.includes(date, inclusiveMask);
    }

}
