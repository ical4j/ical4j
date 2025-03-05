package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.property.*;

import java.util.List;

/**
 * Provides convenience methods for accessing descriptive properties of components and calendars.
 */
public interface DescriptivePropertyAccessor extends PropertyContainer {

    /**
     *
     * @return
     */
    default List<Attach> getAttachments() {
        return getProperties(Property.ATTACH);
    }

    /**
     *
     * @return
     */
    default List<Categories> getCategories() {
        return getProperties(Property.CATEGORIES);
    }

    /**
     *
     * @return
     * @throws ConstraintViolationException if the property is not present
     */
    default Clazz getClassification() {
        return (Clazz) getProperty(Property.CLASS).orElse(null);
    }

    /**
     *
     * @return
     */
    default List<Comment> getComments() {
        return getProperties(Property.COMMENT);
    }

    /**
     *
     * @return
     * @throws ConstraintViolationException if the property is not present
     */
    default Description getDescription() {
        return (Description) getProperty(Property.DESCRIPTION).orElse(null);
    }

    /**
     *
     * @return
     * @throws ConstraintViolationException if the property is not present
     */
    default Geo getGeographicPos() {
        return (Geo) getProperty(Property.GEO).orElse(null);
    }

    /**
     *
     * @return
     * @throws ConstraintViolationException if the property is not present
     */
    default Location getLocation() {
        return (Location) getProperty(Property.LOCATION).orElse(null);
    }

    /**
     *
     * @return
     * @throws ConstraintViolationException if the property is not present
     */
    default PercentComplete getPercentComplete() {
        return (PercentComplete) getProperty(Property.PERCENT_COMPLETE).orElse(null);
    }

    /**
     *
     * @return
     * @throws ConstraintViolationException if the property is not present
     */
    default Priority getPriority() {
        return (Priority) getProperty(Property.PRIORITY).orElse(null);
    }

    // XXX: Conflicts with VRESOURCE sub-components..
//    default Optional<Resources> getResources() {
//        return getProperty(Property.RESOURCES);
//    }

    /**
     *
     * @return
     * @throws ConstraintViolationException if the property is not present
     */
    default Status getStatus() {
        return (Status) getProperty(Property.STATUS).orElse(null);
    }

    /**
     *
     * @return
     * @throws ConstraintViolationException if the property is not present
     */
    default Summary getSummary() {
        return (Summary) getProperty(Property.SUMMARY).orElse(null);
    }
}
