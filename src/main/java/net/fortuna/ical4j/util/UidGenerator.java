package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.property.Uid;

import java.util.function.Supplier;

/**
 * An interface for generating unique identifiers (UIDs).
 * Implementations of this interface should provide a method to generate
 * a unique component identifier.
 *
 * <p>UIDs are typically used in iCalendar components to uniquely identify
 * events, tasks, or other calendar objects.</p>
 *
 * @see Uid
 */
public interface UidGenerator extends Supplier<Uid> {

    /**
     * @return a unique component identifier
     */
    Uid generateUid();

    @Override
    default Uid get() {
        return generateUid();
    }
}
