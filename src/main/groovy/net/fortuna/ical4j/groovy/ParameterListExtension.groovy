package net.fortuna.ical4j.groovy

import net.fortuna.ical4j.model.Parameter
import net.fortuna.ical4j.model.ParameterList

class ParameterListExtension {
    
    static ParameterList leftShift(ParameterList self, Parameter parameter) {
        self.add(parameter)
        self
    }

    static ParameterList leftShift(ParameterList self, ParameterList list) {
        list.each {self.add(it)}
        self
    }
}
