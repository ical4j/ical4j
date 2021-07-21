package groovy.runtime.metaclass.net.fortuna.ical4j.model

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
        return object.getProperties(property)
    }
}
