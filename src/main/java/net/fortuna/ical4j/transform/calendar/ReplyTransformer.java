/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.transform.calendar;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Method;

import java.util.Optional;

import static net.fortuna.ical4j.model.RelationshipPropertyModifiers.ATTENDEE;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REPLY;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.REQUEST;

/**
 * $Id$
 *
 * Created: 26/09/2004
 *
 * Transforms a calendar for publishing.
 * @author benfortuna
 */
public class ReplyTransformer extends AbstractMethodTransformer {

    private final Attendee attendee;

    public ReplyTransformer(Attendee attendee) {
        super(REPLY, null, true, false);
        this.attendee = attendee;
    }

    @Override
    public Calendar apply(Calendar object) {
        Optional<Method> method = object.getProperty(Property.METHOD);
        if (method.isEmpty() || !REQUEST.equals(method.get())) {
            throw new IllegalArgumentException("Expecting REQUEST method in source");
        }
        for (CalendarComponent component : object.getComponents()) {
            component.with(ATTENDEE, attendee);
        }
        return super.apply(object);
    }
}
