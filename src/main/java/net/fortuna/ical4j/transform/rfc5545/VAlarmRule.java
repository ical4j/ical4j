package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Description;

import java.util.Optional;

/**
 * 
 * 
 * @author daniel grigore
 * @author corneliu dobrota
 */
public class VAlarmRule implements Rfc5545ComponentRule<VAlarm> {

    @Override
    public void applyTo(VAlarm element) {
        Optional<Action> action = element.getProperty(Property.ACTION);
        Optional<Description> description = element.getProperty(Property.DESCRIPTION);
        if (!action.isPresent() || !"DISPLAY".equals(action.get().getValue()) || description.isPresent()
                && description.get().getValue() != null) {
            return;
        }
        element.getProperties().add(new Description("display"));
    }

    @Override
    public Class<VAlarm> getSupportedType() {
        return VAlarm.class;
    }
}
