package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;

import java.time.Period;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;

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

        Optional<DtStart<?>> start = element.getProperty(Property.DTSTART);
        Optional<DtEnd<Temporal>> end = element.getProperty(Property.DTEND);
        Optional<Duration> duration = element.getProperty(Property.DURATION);
        
        /*
         *     ; Either 'dtend' or 'duration' MAY appear in
         *     ; a 'eventprop', but 'dtend' and 'duration'
         *     ; MUST NOT occur in the same 'eventprop'.
         */
        if (end.isPresent() && duration.isPresent() && end.get().getValue() != null) {
            element.remove(duration.get());
        }
        
        /*
         *      If the event is allDay, start and end must not be equal,
         *      so we add 1 day to the end date
         */  
        if (start.isPresent() && end.isPresent()){
            Optional<Parameter> startType = start.get().getParameter(Parameter.VALUE);
            Optional<Parameter> endType = end.get().getParameter(Parameter.VALUE);
            if (startType.isPresent() && endType.isPresent() &&
                    startType.get().getValue().equals(Value.DATE.getValue()) &&
                    endType.get().getValue().equals(Value.DATE.getValue()) &&
                    start.get().getValue().equals(end.get().getValue())){

                end.get().setDate(end.get().getDate().plus(Period.ofDays(1)));
            }
        }
        
        List<?> dtStamps = element.getProperties(Property.DTSTAMP);
        if (dtStamps.isEmpty()) {
            element.add(new DtStamp());
        }     
        
    }

    @Override
    public Class<VEvent> getSupportedType() {
        return VEvent.class;
    }
}
