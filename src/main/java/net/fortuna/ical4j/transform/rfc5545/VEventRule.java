package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtStamp;

import java.util.Calendar;
import java.util.List;

/**
 * 
 * @author daniel grigore
 * @author corneliu dobrota
 * @author stefan popescu
 *
 */
public class VEventRule implements Rfc5545ComponentRule<VEvent> {

    @Override
    public void applyTo(VEvent element) {

        Property start = element.getProperty(Property.DTSTART);
        Property end = element.getProperty(Property.DTEND);
        Property duration = element.getProperty(Property.DURATION);
        
        /*
         *     ; Either 'dtend' or 'duration' MAY appear in
         *     ; a 'eventprop', but 'dtend' and 'duration'
         *     ; MUST NOT occur in the same 'eventprop'.
         */
        if (end != null && duration != null && end.getValue() != null && duration != null) {
            element.getProperties().remove(duration);
        }
        
        /*
         *      If the event is allDay, start and end must not be equal,
         *      so we add 1 day to the end date
         */  
        if (start!=null && end!=null){
            Parameter startType = start.getParameter(Parameter.VALUE);
            Parameter endType = end.getParameter(Parameter.VALUE);
            if (startType!=null && endType!=null &&
                    startType.getValue().equals(Value.DATE.getValue()) &&
                    endType.getValue().equals(Value.DATE.getValue()) &&
                    start.getValue().equals(end.getValue())){
                if (end instanceof DateProperty) {
                    DateProperty endDate = (DateProperty) end;
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(endDate.getDate());
                    cal.add(Calendar.DATE, 1);
                    endDate.setDate(new Date(cal.getTime()));
                }
            }
        }
        
        List<?> dtStamps = element.getProperties(Property.DTSTAMP);
        if (dtStamps == null || dtStamps.isEmpty()) {
            element.getProperties().add(new DtStamp());
        }     
        
    }

    @Override
    public Class<VEvent> getSupportedType() {
        return VEvent.class;
    }
}
