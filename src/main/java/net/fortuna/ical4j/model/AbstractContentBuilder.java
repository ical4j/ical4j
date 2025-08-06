package net.fortuna.ical4j.model;

/**
 * Abstract base class for content builders in the iCal4j library.
 * This class provides common functionality for handling content names and experimental features.
 * It can be extended to create specific content builders for different iCal4j components.
 */
public abstract class AbstractContentBuilder {

    private final boolean allowIllegalNames;

    /**
     * Default constructor.
     */
    public AbstractContentBuilder() {
        this(true);
    }

    /**
     *
     * @param allowIllegalNames store unrecognised names as experimental content
     */
    public AbstractContentBuilder(boolean allowIllegalNames) {
        this.allowIllegalNames = allowIllegalNames;
    }

    /**
     * @param name
     * @return
     */
    protected boolean isExperimentalName(final String name) {
        return name.toUpperCase().startsWith(Component.EXPERIMENTAL_PREFIX)
                && name.length() > Component.EXPERIMENTAL_PREFIX.length();
    }

    /**
     * @return true if non-standard names are allowed, otherwise false
     */
    protected boolean allowIllegalNames() {
        return allowIllegalNames;
    }
}
