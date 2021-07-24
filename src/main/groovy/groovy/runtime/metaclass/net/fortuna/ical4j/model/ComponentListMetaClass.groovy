package groovy.runtime.metaclass.net.fortuna.ical4j.model

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
        return object.getComponents(property)
    }
}
