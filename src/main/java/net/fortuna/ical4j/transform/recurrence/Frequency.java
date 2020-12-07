package net.fortuna.ical4j.transform.recurrence;

/**
 * Represents the possible expansion rules used to generate recurrences.
 *
 * From RFC5545:
 *
 * <pre>
 *       The FREQ rule part identifies the type of recurrence rule.  This
 *       rule part MUST be specified in the recurrence rule.  Valid values
 *       include SECONDLY, to specify repeating events based on an interval
 *       of a second or more; MINUTELY, to specify repeating events based
 *       on an interval of a minute or more; HOURLY, to specify repeating
 *       events based on an interval of an hour or more; DAILY, to specify
 *       repeating events based on an interval of a day or more; WEEKLY, to
 *       specify repeating events based on an interval of a week or more;
 *       MONTHLY, to specify repeating events based on an interval of a
 *       month or more; and YEARLY, to specify repeating events based on an
 *       interval of a year or more.
 * </pre>
 *
 * See https://tools.ietf.org/html/rfc5545#section-3.3.10 for more details.
 */
public enum Frequency {
    SECONDLY, MINUTELY, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY
}
