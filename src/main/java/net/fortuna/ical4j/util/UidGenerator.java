package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.property.Uid;

import java.util.function.Supplier;

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
