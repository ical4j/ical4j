package groovy.runtime.metaclass.net.fortuna.ical4j.model

class ParameterListMetaClass extends DelegatingMetaClass {

    ParameterListMetaClass(MetaClass delegate) {
        super(delegate)
    }

    ParameterListMetaClass(Class theClass) {
        super(theClass)
    }

    @Override
    Object getProperty(Object object, String property) {
        if (hasProperty(object, property)) {
            return super.getProperty(object, property)
        }
        return object.getParameters(property)
    }

    @Override
    void setProperty(Object object, String property, Object newValue) {
        if (hasProperty(object, property)) {
            super.setProperty(object, property, newValue)
        }
        object.replace(newValue)
    }
}
