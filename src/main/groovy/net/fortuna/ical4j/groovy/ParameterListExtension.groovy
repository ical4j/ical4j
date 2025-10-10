package net.fortuna.ical4j.groovy

import net.fortuna.ical4j.model.Parameter
import net.fortuna.ical4j.model.ParameterList

/**
 * A Groovy extension for the ParameterList class to provide additional functionality.
 * This class allows for adding parameters and lists of parameters using the left shift operator.
 */
class ParameterListExtension {
    
    static ParameterList leftShift(ParameterList self, Parameter parameter) {
        (ParameterList) self.add(parameter)
    }

    static ParameterList leftShift(ParameterList self, ParameterList list) {
        (ParameterList) self.addAll(list.all)
    }
}
