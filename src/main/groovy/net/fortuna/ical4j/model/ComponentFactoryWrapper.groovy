package net.fortuna.ical4j.model

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
            return value.copy()
        }
        List<Property> properties = (List<Property>) attributes.remove('properties')
        if (properties == null) {
            properties = []
        }
        return factory.createComponent(new PropertyList(properties))
    }

    void setChild(FactoryBuilderSupport build, Object parent, Object child) {
        if (child instanceof Property || child instanceof Component) {
            parent.add(child)
        }
    }
}
