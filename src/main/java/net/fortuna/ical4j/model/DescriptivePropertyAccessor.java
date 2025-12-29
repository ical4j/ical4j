package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.property.*;

import java.util.List;

/**
 * An interface for components that contain descriptive properties.
 * Provides methods to access various descriptive properties such as attachments,
 * categories, classification, comments, description, geographic position,
 * location, percent complete, priority, status, and summary.
 * Each method returns the corresponding property or a list of properties.
 * If a property is not present, it may throw a {@link ConstraintViolationException}.
 * <p>
 * This interface extends {@link PropertyContainer} to provide access to the properties.
 * It is designed to be implemented by components that require descriptive properties,
 * such as events, tasks, or other calendar-related components.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * DescriptivePropertyAccessor accessor = ...; // Obtain an instance of a class implementing this interface
 * List<Attach> attachments = accessor.getAttachments();
 * Clazz classification = accessor.getClassification();
 * Description description = accessor.getDescription();
 * Geo geographicPos = accessor.getGeographicPos();
 * Location location = accessor.getLocation();
 * PercentComplete percentComplete = accessor.getPercentComplete();
 * Priority priority = accessor.getPriority();
 * Status status = accessor.getStatus();
 * Summary summary = accessor.getSummary();
 * </pre>
 * </p>
 * <p>
 * Note: The methods in this interface may return null or throw exceptions if the corresponding properties are
 * not present in the component. It is recommended to handle these cases appropriately in the implementation.
 * </p>
 * <p>
 * This interface is part of the iCal4j library, which provides a Java API for
 * working with iCalendar data. It is designed to facilitate the manipulation and
 * retrieval of calendar-related properties in a structured manner.
 * </p>
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

    default Concept getConcept() {
        return (Concept) getProperty(Concept.PROPERTY_NAME).orElse(null);
    }
}
