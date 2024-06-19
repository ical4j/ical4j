package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.property.*;

import java.util.List;
import java.util.Optional;

/**
 * Provides convenience methods for accessing descriptive properties of components and calendars.
 */
public interface DescriptivePropertyAccessor extends PropertyContainer {

    default List<Attach> getAttachments() {
        return getProperties(Property.ATTACH);
    }

    default Optional<Categories> getCategories() {
        return getProperty(Property.CATEGORIES);
    }

    default Optional<Clazz> getClassification() {
        return getProperty(Property.CLASS);
    }

    default List<Comment> getComments() {
        return getProperties(Property.COMMENT);
    }

    default Optional<Description> getDescription() {
        return getProperty(Property.DESCRIPTION);
    }

    default Optional<Geo> getGeographicPos() {
        return getProperty(Property.GEO);
    }

    default Optional<Location> getLocation() {
        return getProperty(Property.LOCATION);
    }

    default Optional<PercentComplete> getPercentComplete() {
        return getProperty(Property.PERCENT_COMPLETE);
    }

    default Optional<Priority> getPriority() {
        return getProperty(Property.PRIORITY);
    }

    // XXX: Conflicts with VRESOURCE sub-components..
//    default Optional<Resources> getResources() {
//        return getProperty(Property.RESOURCES);
//    }

    default Optional<Status> getStatus() {
        return getProperty(Property.STATUS);
    }

    default Optional<Summary> getSummary() {
        return getProperty(Property.SUMMARY);
    }
}
