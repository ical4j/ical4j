package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.property.Uid;

public interface UidGenerator {

    /**
     * @return a unique component identifier
     */
    Uid generateUid();
}
