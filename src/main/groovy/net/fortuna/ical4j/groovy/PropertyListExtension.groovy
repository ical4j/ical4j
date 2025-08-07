package net.fortuna.ical4j.groovy

import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.PropertyList

/**
 * A Groovy extension for the PropertyList class to provide additional functionality.
 * This class allows for adding properties and lists of properties using the left shift operator.
 */
class PropertyListExtension {
    
    static PropertyList leftShift(PropertyList self, Property property) {
        (PropertyList) self.add(property)
    }

    static PropertyList leftShift(PropertyList self, PropertyList list) {
        (PropertyList) self.addAll(list.all)
    }
}
