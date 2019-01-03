package net.fortuna.ical4j.model.parameter

import net.fortuna.ical4j.model.ParameterFactory

class ParameterFactoryWrapper extends AbstractFactory {

    Class parameterClass

    ParameterFactory factory

    ParameterFactoryWrapper(Class paramClass, ParameterFactory factory) {
        this.parameterClass = paramClass
        this.factory = factory
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, parameterClass)) {
            return value
        }
        return factory.createParameter(value)
    }

    boolean isLeaf() {
        return true
    }
}
