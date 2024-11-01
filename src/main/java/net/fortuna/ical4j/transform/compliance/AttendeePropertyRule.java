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

import net.fortuna.ical4j.model.property.Attendee;
import org.apache.commons.lang3.StringUtils;

public class AttendeePropertyRule implements Rfc5545PropertyRule<Attendee> {

    private static final String MAILTO = "mailto";
    private static final String APOSTROPHE = "'";
    private static final int MIN_LENGTH = 3;

    @Override
    public Attendee apply(Attendee element) {
        if (element == null) {
            return element;
        }
        var calAddress = element.getCalAddress();
        if (calAddress == null) {
            return element;
        }
        var scheme = calAddress.getScheme();
        if (scheme != null && StringUtils.startsWithIgnoreCase(scheme, MAILTO)) {
            var part = calAddress.getSchemeSpecificPart();
            if (part != null && part.length() >= MIN_LENGTH && StringUtils.startsWith(part, APOSTROPHE)
                    && StringUtils.endsWith(part, APOSTROPHE)) {
                var newPart = part.substring(1, part.length() - 1);
                safelySetNewValue(element, newPart);
            }
        }
        return element;
    }

    private static void safelySetNewValue(Attendee element, String newPart) {
        element.setValue(MAILTO + ":" + newPart);
    }

    @Override
    public Class<Attendee> getSupportedType() {
        return Attendee.class;
    }

}
