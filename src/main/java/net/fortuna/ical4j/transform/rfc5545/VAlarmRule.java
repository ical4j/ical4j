package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Description;

/**
 * 
 * 
 * @author daniel grigore
 * @author corneliu dobrota
 */
public class VAlarmRule implements Rfc5545ComponentRule<VAlarm> {

    @Override
    public void applyTo(VAlarm element) {
        Action action = element.getAction();
        if (action == null || !"DISPLAY".equals(action.getValue()) || element.getDescription() != null
                && element.getDescription().getValue() != null) {
            return;
        }
        Description description = new Description("display");
        element.getProperties().add(description);
    }

    @Override
    public Class<VAlarm> getSupportedType() {
        return VAlarm.class;
    }
}
