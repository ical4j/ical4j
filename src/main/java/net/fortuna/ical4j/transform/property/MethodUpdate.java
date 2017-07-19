package net.fortuna.ical4j.transform.property;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.Transformer;

/**
 * Created by fortuna on 19/07/2017.
 */
public class MethodUpdate implements Transformer<Calendar> {

    private final Method method;

    public MethodUpdate(Method method) {
        this.method = method;
    }

    @Override
    public Calendar transform(Calendar object) throws Exception {
        PropertyList calProps = object.getProperties();

        Property method = calProps.getProperty(Property.METHOD);
        if (method != null) {
            calProps.remove(method);
        }
        calProps.add(Method.PUBLISH);

        return object;
    }
}
