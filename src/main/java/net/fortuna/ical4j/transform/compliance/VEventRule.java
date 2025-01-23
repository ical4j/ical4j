/*
 *  Copyright (c) 2024, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.transform.compliance;

import net.fortuna.ical4j.model.ChangeManagementPropertyModifiers;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;

import java.time.Instant;
import java.time.Period;
import java.time.temporal.Temporal;
import java.util.Optional;

import static net.fortuna.ical4j.model.Property.*;

/**
 * 
 * @author daniel grigore
 * @author corneliu dobrota
 * @author stefan popescu
 *
 */
public class VEventRule implements Rfc5545ComponentRule<VEvent> {

    @Override
    public VEvent apply(VEvent element) {
        Optional<DtStart<Temporal>> start = element.getProperty(DTSTART);
        Optional<DtEnd<Temporal>> end = element.getProperty(DTEND);
        Optional<Duration> duration = element.getProperty(DURATION);
        
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
        
        if (element.getProperty(DTSTAMP).isEmpty()) {
            element.with(ChangeManagementPropertyModifiers.DTSTAMP, Instant.now());
        }     
        return element;
    }

    @Override
    public Class<VEvent> getSupportedType() {
        return VEvent.class;
    }
}
