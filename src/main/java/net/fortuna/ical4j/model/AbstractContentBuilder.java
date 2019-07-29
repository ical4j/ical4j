package net.fortuna.ical4j.model;

import net.fortuna.ical4j.util.CompatibilityHints;

public abstract class AbstractContentBuilder {

    /**
     * @param name
     * @return
     */
    protected boolean isExperimentalName(final String name) {
        return name.startsWith(Component.EXPERIMENTAL_PREFIX)
                && name.length() > Component.EXPERIMENTAL_PREFIX.length();
    }

    /**
     * @return true if non-standard names are allowed, otherwise false
     */
    protected boolean allowIllegalNames() {
        return CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
    }
}
