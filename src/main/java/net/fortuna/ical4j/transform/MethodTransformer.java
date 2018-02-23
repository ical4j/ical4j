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
package net.fortuna.ical4j.transform;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.property.MethodUpdate;
import net.fortuna.ical4j.transform.property.SequenceIncrement;
import net.fortuna.ical4j.transform.property.UidUpdate;
import net.fortuna.ical4j.util.SimpleHostInfo;
import net.fortuna.ical4j.util.UidGenerator;

/**
 * $Id$
 *
 * Created: 26/09/2004
 *
 * Transforms a calendar for publishing.
 * @author benfortuna
 */
public class MethodTransformer implements Transformer<Calendar> {

    private final Method method;

    private final boolean incrementSequence;

    private final boolean sameUid;

    public MethodTransformer(Method method) {
        this(method, false, false);
    }

    public MethodTransformer(Method method, boolean incrementSequence, boolean sameUid) {
        this.method = method;
        this.incrementSequence = incrementSequence;
        this.sameUid = sameUid;
    }

    @Override
    public final Calendar transform(Calendar object) {
        MethodUpdate methodUpdate = new MethodUpdate(method);
        methodUpdate.transform(object);

        if (incrementSequence) {
            UidUpdate uidUpdate = new UidUpdate(new UidGenerator(new SimpleHostInfo("host"), "1"));
            SequenceIncrement sequenceIncrement = new SequenceIncrement();
            // if a calendar component has already been published previously
            // update the sequence number..
            Property uid = null;
            for (CalendarComponent component : object.getComponents()) {
                uidUpdate.transform(component);
                if (uid == null) {
                    uid = component.getProperty(Property.UID);
                } else if (sameUid && !uid.equals(component.getProperty(Property.UID))) {
                    throw new IllegalArgumentException("All components must share the same non-null UID");
                }
                sequenceIncrement.transform(component);
            }
        }
        return object;
    }
}
