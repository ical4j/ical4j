package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.ComponentFactory
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.PropertyList

class ComponentFactoryWrapper extends AbstractFactory {

    Class componentClass

    ComponentFactory factory

    ComponentFactoryWrapper(Class compClass, ComponentFactory factory) {
        this.componentClass = compClass
        this.factory = factory
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, componentClass)) {
            return value
        }
        List<Property> properties = (List<Property>) attributes.remove('properties')
        if (properties == null) {
            properties = []
        }
        String compValue = attributes.remove('value')
        return factory.createComponent(new PropertyList(properties))
    }

    void setChild(FactoryBuilderSupport build, Object parent, Object child) {
        if (child instanceof Property) {
            parent.add(child)
        }
    }
}
