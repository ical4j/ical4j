package groovy.runtime.metaclass.net.fortuna.ical4j.model

/**
 * A MetaClass for ParameterList that allows dynamic property access.
 * This class extends the DelegatingMetaClass to provide custom behavior
 * for property retrieval and setting.
 */
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
        return object.get(property)
    }

    @Override
    void setProperty(Object object, String property, Object newValue) {
        if (hasProperty(object, property)) {
            super.setProperty(object, property, newValue)
        }
        object.replace(newValue)
    }
}
