package net.fortuna.ical4j.model;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;

public class TemporalComparator implements Comparator<Temporal> {

    private final TemporalUnit comparisonUnit;

    public TemporalComparator() {
        this(ChronoUnit.SECONDS);
    }

    public TemporalComparator(TemporalUnit comparisonUnit) {
        this.comparisonUnit = comparisonUnit;
    }

    @Override
    public int compare(Temporal o1, Temporal o2) {
        // sort ascending by default..
        return (int) comparisonUnit.between(o2, o1);
    }
}
