package groovy.runtime.metaclass.net.fortuna.ical4j.model

/**
 * A MetaClass for ParameterList that allows dynamic property access.
 * This class extends the DelegatingMetaClass to provide custom behavior
 * for property retrieval and setting.
 */
class PropertyListMetaClass extends DelegatingMetaClass {

    PropertyListMetaClass(MetaClass delegate) {
        super(delegate)
    }

    PropertyListMetaClass(Class theClass) {
        super(theClass)
    }

    @Override
    Object getProperty(Object object, String property) {
        if (hasProperty(object, property)) {
            return super.getProperty(object, property)
        }
        return object.get(property)
    }
}
