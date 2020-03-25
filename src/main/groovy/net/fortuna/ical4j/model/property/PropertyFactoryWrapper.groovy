package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.Parameter
import net.fortuna.ical4j.model.PropertyFactory

class PropertyFactoryWrapper extends AbstractFactory {

    Class propertyClass

    PropertyFactory factory

    PropertyFactoryWrapper(Class propClass, PropertyFactory factory) {
        this.propertyClass = propClass
        this.factory = factory
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, propertyClass)) {
            return value
        }
        List<Parameter> parameters = (List<Parameter>) attributes.remove('parameters')
        if (parameters == null) {
            parameters = []
        }
        String propValue = attributes.remove('value')
        if (propValue != null) {
            return factory.createProperty(parameters, propValue)
        }
        else {
            return factory.createProperty(parameters, (String) value)
        }
    }

    void setChild(FactoryBuilderSupport build, Object parent, Object child) {
        if (child instanceof Parameter) {
            parent.parameters.add(child)
        }
    }
}
