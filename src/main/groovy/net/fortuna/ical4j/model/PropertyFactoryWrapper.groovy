package net.fortuna.ical4j.model

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
            return value.copy()
        }
        List<Parameter> parameters = (List<Parameter>) attributes.remove('parameters')
        if (parameters == null) {
            parameters = []
        }
        String propValue = attributes.remove('value')
        if (propValue != null) {
            return factory.createProperty(new ParameterList(parameters), propValue)
        }
        else if (value != null) {
            return factory.createProperty(new ParameterList(parameters), (String) value)
        } else {
            return factory.createProperty()
        }
    }

    void setChild(FactoryBuilderSupport build, Object parent, Object child) {
        if (child instanceof Parameter) {
            parent.add(child)
        }
    }
}
