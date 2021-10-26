/*
 *  Copyright (c) 2021, Ben Fortuna
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

package net.fortuna.ical4j.validate.component;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAvailability;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import net.fortuna.ical4j.validate.Validator;

public class VAvailabilityValidator implements Validator<VAvailability> {

    @Override
    public void validate(VAvailability target) throws ValidationException {
        ValidationResult result = new ValidationResult();

        ComponentValidator.VAVAILABILITY.validate(target);

        // validate that getAvailable() only contains Available components
//        final Iterator<Available> iterator = getAvailable().iterator();
//        while (iterator.hasNext()) {
//            final Component component = (Component) iterator.next();
//
//            if (!(component instanceof Available)) {
//                throw new ValidationException("Component ["
//                        + component.getName() + "] may not occur in VAVAILABILITY");
//            }
//        }

        /*       If specified, the "DTSTART" and "DTEND" properties in
         *      "VAVAILABILITY" components and "AVAILABLE" sub-components MUST be
         *      "DATE-TIME" values specified as either date with UTC time or date
         *      with local time and a time zone reference.
         */
        final DtStart start = target.getProperty(Property.DTSTART);
        if (Value.DATE.equals(start.getParameter(Parameter.VALUE))) {
            result.getErrors().add("Property [" + Property.DTSTART + "] must be a " + Value.DATE_TIME);
        }

        /* Must be DATE_TIME */
        final DtEnd end = target.getProperty(Property.DTEND);
        if (end != null && Value.DATE.equals(end.getParameter(Parameter.VALUE))) {
            result.getErrors().add("Property [" + Property.DTEND + "] must be a " + Value.DATE_TIME);
        }

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
    }
}
