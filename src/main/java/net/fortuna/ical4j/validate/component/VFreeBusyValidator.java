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

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import net.fortuna.ical4j.validate.Validator;

public class VFreeBusyValidator implements Validator<VFreeBusy> {

    @Override
    public void validate(VFreeBusy target) throws ValidationException {
        ComponentValidator.VFREEBUSY.validate(target);
        ValidationResult result = new ValidationResult();

        // DtEnd value must be later in time that DtStart..
        final DtStart dtStart = target.getProperty(Property.DTSTART);

        // 4.8.2.4 Date/Time Start:
        //
        //    Within the "VFREEBUSY" calendar component, this property defines the
        //    start date and time for the free or busy time information. The time
        //    MUST be specified in UTC time.
        if (dtStart != null && !dtStart.isUtc()) {
            result.getErrors().add("DTSTART must be specified in UTC time");
        }

        final DtEnd dtEnd = target.getProperty(Property.DTEND);

        // 4.8.2.2 Date/Time End
        //
        //    Within the "VFREEBUSY" calendar component, this property defines the
        //    end date and time for the free or busy time information. The time
        //    MUST be specified in the UTC time format. The value MUST be later in
        //    time than the value of the "DTSTART" property.
        if (dtEnd != null && !dtEnd.isUtc()) {
            result.getErrors().add("DTEND must be specified in UTC time");
        }

        if (dtStart != null && dtEnd != null
                && !dtStart.getDate().before(dtEnd.getDate())) {
            result.getErrors().add("Property [" + Property.DTEND
                    + "] must be later in time than [" + Property.DTSTART + "]");
        }
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
    }
}
