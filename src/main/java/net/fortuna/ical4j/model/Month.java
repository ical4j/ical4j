package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Defines a month of the year, which may be a leap-month in some calendaring systems.
 */
public class Month implements Serializable {

    private final int monthOfYear;

    private final boolean leapMonth;

    public Month(int monthOfYear) {
        this(monthOfYear, false);
    }

    public Month(int monthOfYear, boolean leapMonth) {
        this.monthOfYear = monthOfYear;
        this.leapMonth = leapMonth;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public boolean isLeapMonth() {
        return leapMonth;
    }

    public static Month parse(String monthString) {
        if (monthString.endsWith("L")) {
            return new Month(Integer.parseInt(monthString.substring(0, monthString.length()-1)), true);
        }
        return new Month(Integer.parseInt(monthString));
    }

    public static Month valueOf(int monthOfYear) {
        return new Month(monthOfYear);
    }

    @Override
    public String toString() {
        if (leapMonth) {
            return monthOfYear + "L";
        } else {
            return String.valueOf(monthOfYear);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Month month = (Month) o;
        return monthOfYear == month.monthOfYear && leapMonth == month.leapMonth;
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthOfYear, leapMonth);
    }
}
