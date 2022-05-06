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
package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.Property;

import static net.fortuna.ical4j.model.property.immutable.ImmutableAction.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableCalScale.GREGORIAN;
import static net.fortuna.ical4j.model.property.immutable.ImmutableClazz.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableMethod.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutablePriority.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableStatus.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableTransp.OPAQUE;
import static net.fortuna.ical4j.model.property.immutable.ImmutableTransp.TRANSPARENT;
import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0;

/**
 * $Id$
 *
 * Created on 5/07/2005
 *
 * Provides some convenience methods for working with constant
 * parameters and properties.
 * @author Ben Fortuna
 */
public final class Constants {

    /**
     * Constructor made private to enforce static nature.
     */
    private Constants() {
    }
    
    /**
     * Returns a constant equivalent to the specified property
     * if one is applicable. Otherwise will return the specified
     * property.
     * @param property a property instance
     * @return an equivalent constant property, or the specified property if no equivalent
     * constant exists
     */
    public static Property forProperty(final Property property) {
        Property retVal = property;
        if (AUDIO.equals(property)) {
            retVal = AUDIO;
        }
        else if (DISPLAY.equals(property)) {
            retVal = DISPLAY;
        }
        else if (EMAIL.equals(property)) {
            retVal = EMAIL;
        }
        else if (PROCEDURE.equals(property)) {
            retVal = PROCEDURE;
        }
        else if (GREGORIAN.equals(property)) {
            retVal = GREGORIAN;
        }
        else if (CONFIDENTIAL.equals(property)) {
            retVal = CONFIDENTIAL;
        }
        else if (PRIVATE.equals(property)) {
            retVal = PRIVATE;
        }
        else if (PUBLIC.equals(property)) {
            retVal = PUBLIC;
        }
        else if (ADD.equals(property)) {
            retVal = ADD;
        }
        else if (CANCEL.equals(property)) {
            retVal = CANCEL;
        }
        else if (COUNTER.equals(property)) {
            retVal = COUNTER;
        }
        else if (DECLINE_COUNTER.equals(property)) {
            retVal = DECLINE_COUNTER;
        }
        else if (PUBLISH.equals(property)) {
            retVal = PUBLISH;
        }
        else if (REFRESH.equals(property)) {
            retVal = REFRESH;
        }
        else if (REPLY.equals(property)) {
            retVal = REPLY;
        }
        else if (REQUEST.equals(property)) {
            retVal = REQUEST;
        }
        else if (HIGH.equals(property)) {
            retVal = HIGH;
        }
        else if (LOW.equals(property)) {
            retVal = LOW;
        }
        else if (MEDIUM.equals(property)) {
            retVal = MEDIUM;
        }
        else if (UNDEFINED.equals(property)) {
            retVal = UNDEFINED;
        }
        else if (VEVENT_CANCELLED.equals(property)) {
            retVal = VEVENT_CANCELLED;
        }
        else if (VEVENT_CONFIRMED.equals(property)) {
            retVal = VEVENT_CONFIRMED;
        }
        else if (VEVENT_TENTATIVE.equals(property)) {
            retVal = VEVENT_TENTATIVE;
        }
        else if (VJOURNAL_CANCELLED.equals(property)) {
            retVal = VJOURNAL_CANCELLED;
        }
        else if (VJOURNAL_DRAFT.equals(property)) {
            retVal = VJOURNAL_DRAFT;
        }
        else if (VJOURNAL_FINAL.equals(property)) {
            retVal = VJOURNAL_FINAL;
        }
        else if (VTODO_CANCELLED.equals(property)) {
            retVal = VTODO_CANCELLED;
        }
        else if (VTODO_COMPLETED.equals(property)) {
            retVal = VTODO_COMPLETED;
        }
        else if (VTODO_IN_PROCESS.equals(property)) {
            retVal = VTODO_IN_PROCESS;
        }
        else if (VTODO_NEEDS_ACTION.equals(property)) {
            retVal = VTODO_NEEDS_ACTION;
        }
        else if (OPAQUE.equals(property)) {
            retVal = OPAQUE;
        }
        else if (TRANSPARENT.equals(property)) {
            retVal = TRANSPARENT;
        }
        else if (VERSION_2_0.equals(property)) {
            retVal = VERSION_2_0;
        }
        return retVal;
    }
}
