package groovy.runtime.metaclass.net.fortuna.ical4j.model

/**
 * A MetaClass for ComponentList that allows dynamic property access.
 * This class extends the DelegatingMetaClass to provide custom behavior
 * for property retrieval.
 */
class ComponentListMetaClass extends DelegatingMetaClass {

    ComponentListMetaClass(MetaClass delegate) {
        super(delegate)
    }

    ComponentListMetaClass(Class theClass) {
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
