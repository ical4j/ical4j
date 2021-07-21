package net.fortuna.ical4j.groovy

import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.PropertyList

class PropertyListExtension {
    
    static PropertyList leftShift(PropertyList self, Property property) {
        self.add(property)
        self
    }

    static PropertyList leftShift(PropertyList self, PropertyList list) {
        list.each {self.add(it)}
        self
    }
}
