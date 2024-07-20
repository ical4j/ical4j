package net.fortuna.ical4j.model;

import net.fortuna.ical4j.util.TimeZones;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;

/**
 * The purpose of this comparator is to compare two temporals regardless of type. Some temporal types are not
 * directly comparable, due to differences in supported units. For these cases we support hints to indicate how
 * they should be compared.
 *
 * Compare {@link Temporal} values based on a specified {@link ChronoUnit}.
 */
public class TemporalComparator implements Comparator<Temporal> {

    public static final TemporalComparator INSTANCE = new TemporalComparator();

    private final TemporalUnit defaultComparisonUnit;

    private final ZoneId defaultZoneId;

    public TemporalComparator() {
        this(ChronoUnit.SECONDS, TimeZones.getDefault().toZoneId());
    }

    public TemporalComparator(TemporalUnit defaultComparisonUnit) {
        this(defaultComparisonUnit, TimeZones.getDefault().toZoneId());
    }

    public TemporalComparator(ZoneId defaultZoneId) {
        this(ChronoUnit.SECONDS, defaultZoneId);
    }

    /**
     *
     * @param defaultComparisonUnit the fallback comparison unit if all other temporal comparisons are not applicable.
     */
    public TemporalComparator(TemporalUnit defaultComparisonUnit, ZoneId defaultZoneId) {
        this.defaultComparisonUnit = defaultComparisonUnit;
        this.defaultZoneId = defaultZoneId;
    }

    @Override
    public int compare(Temporal o1, Temporal o2) {
        if (o1 instanceof Instant) {
            Instant i1 = (Instant) o1;
            if (o2 instanceof Instant) {
                return compare(i1, (Instant) o2);
            } else if (o2 instanceof OffsetDateTime) {
                return compare(i1, (OffsetDateTime) o2);
            } else if (o2 instanceof LocalDateTime) {
                return compare(i1, (LocalDateTime) o2);
            } else if (o2 instanceof LocalDate) {
                return compare(i1, (LocalDate) o2);
            }
        } else if (o1 instanceof OffsetDateTime) {
            OffsetDateTime l1 = (OffsetDateTime) o1;
            if (o2 instanceof Instant) {
                return compare(l1, (Instant) o2);
            } else if (o2 instanceof LocalDateTime) {
                return compare(l1, (LocalDateTime) o2);
            } else if (o2 instanceof LocalDate) {
                return compare(l1, (LocalDate) o2);
            }
        } else if (o1 instanceof LocalDateTime) {
            LocalDateTime l1 = (LocalDateTime) o1;
            if (o2 instanceof Instant) {
                return compare(l1, (Instant) o2);
            } else if (o2 instanceof OffsetDateTime) {
                return compare(l1, (OffsetDateTime) o2);
            } else if (o2 instanceof LocalDateTime) {
                return compare(l1, (LocalDateTime) o2);
            } else if (o2 instanceof LocalDate) {
                return compare(l1, (LocalDate) o2);
            }
        } else if (o1 instanceof LocalDate) {
            LocalDate l1 = (LocalDate) o1;
            if (o2 instanceof Instant) {
                return compare(l1, (Instant) o2);
            } else if (o2 instanceof OffsetDateTime) {
                return compare(l1, (OffsetDateTime) o2);
            } else if (o2 instanceof LocalDateTime) {
                return compare(l1, (LocalDateTime) o2);
            } else if (o2 instanceof LocalDate) {
                return compare(l1, (LocalDate) o2);
            }
        }
        // sort ascending by default..
        long diff = defaultComparisonUnit.between(o2, o1);
        return diff > 0 ? Integer.MAX_VALUE : diff < 0 ? Integer.MIN_VALUE : 0;
    }

    public int compare(Instant o1, Instant o2) {
        return o1.compareTo(o2);
    }

    public int compare(OffsetDateTime o1, OffsetDateTime o2) {
        return o1.compareTo(o2);
    }

    public int compare(LocalDateTime o1, LocalDateTime o2) {
        return o1.compareTo(o2);
    }

    public int compare(LocalDate o1, LocalDate o2) {
        return o1.compareTo(o2);
    }

    public int compare(Instant o1, LocalDateTime o2) {
        return o1.compareTo(ZonedDateTime.of(o2, defaultZoneId).toInstant());
    }

    public int compare(LocalDateTime o1, Instant o2) {
        return ZonedDateTime.of(o1, defaultZoneId).toInstant().compareTo(o2);
    }

    public int compare(Instant o1, OffsetDateTime o2) {
        return o1.compareTo(o2.toInstant());
    }

    public int compare(OffsetDateTime o1, Instant o2) {
        return o1.toInstant().compareTo(o2);
    }

    public int compare(OffsetDateTime o1, LocalDateTime o2) {
        return o1.compareTo(OffsetDateTime.of(o2, defaultZoneId.getRules().getOffset(o2)));
    }

    public int compare(LocalDateTime o1, OffsetDateTime o2) {
        return OffsetDateTime.of(o1, defaultZoneId.getRules().getOffset(o1)).compareTo(o2);
    }

    public int compare(Instant o1, LocalDate o2) {
        return compare(o1, o2.atStartOfDay());
    }

    public int compare(LocalDate o1, Instant o2) {
        return compare(o1.atStartOfDay(), o2);
    }

    public int compare(OffsetDateTime o1, LocalDate o2) {
        return compare(o1, o2.atStartOfDay());
    }

    public int compare(LocalDate o1, OffsetDateTime o2) {
        return compare(o1.atStartOfDay(), o2);
    }

    public int compare(LocalDateTime o1, LocalDate o2) {
        return compare(o1, o2.atStartOfDay());
    }

    public int compare(LocalDate o1, LocalDateTime o2) {
        return compare(o1.atStartOfDay(), o2);
    }
}
