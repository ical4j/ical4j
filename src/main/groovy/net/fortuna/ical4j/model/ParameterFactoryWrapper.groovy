package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.ParameterFactory

/**
 * A wrapper for a ParameterFactory that allows dynamic creation of parameters
 * based on their class type. This class extends AbstractFactory to integrate
 * with Groovy's FactoryBuilderSupport.
 */
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
        return factory.createParameter((String) value)
    }

    boolean isLeaf() {
        return true
    }
}
