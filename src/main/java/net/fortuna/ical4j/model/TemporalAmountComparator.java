package net.fortuna.ical4j.model;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;

public class TemporalAmountComparator implements Comparator<TemporalAmount> {
    @Override
    public int compare(TemporalAmount o1, TemporalAmount o2) {
        int result = 0;
        if (!o1.getClass().equals(o2.getClass())) {
//            throw new UnsupportedOperationException("Unable to compare different Temporal types");
            boolean o1datebased = o1.getUnits().stream().anyMatch(TemporalUnit::isDateBased);
            boolean o2datebased = o2.getUnits().stream().anyMatch(TemporalUnit::isDateBased);
            if (o1datebased != o2datebased) {
                if (o1datebased) {
                    result = Integer.MAX_VALUE;
                } else {
                    result = Integer.MIN_VALUE;
                }
            }
        } else if (o1 instanceof Period && o2 instanceof Period) {
            Period p1 = (Period) o1, p2 = (Period) o2;
            if (p1.isNegative() != p2.isNegative()) {
                if (p1.isNegative()) {
                    result = Integer.MIN_VALUE;
                }
                else {
                    result = Integer.MAX_VALUE;
                }
            }
            else if (p1.getYears() != p2.getYears()) {
                result = p1.getYears() - p2.getYears();
            }
            else if (p1.getMonths() != p2.getMonths()) {
                result = p1.getMonths() - p2.getMonths();
            }
            else {
                result = p1.getDays() - p2.getDays();
            }
            // invert sense of all tests if both durations are negative
            if (p1.isNegative()) {
                return -result;
            }
            else {
                return result;
            }
        } else {
            result =  Duration.from(o1).compareTo(Duration.from(o2));
        }
        return result;
    }
}
