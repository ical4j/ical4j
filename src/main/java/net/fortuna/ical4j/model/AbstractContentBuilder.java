package net.fortuna.ical4j.model;

public abstract class AbstractContentBuilder {

    private final boolean allowIllegalNames;

    public AbstractContentBuilder(boolean allowIllegalNames) {
        this.allowIllegalNames = allowIllegalNames;
    }

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
        return allowIllegalNames;
    }
}
