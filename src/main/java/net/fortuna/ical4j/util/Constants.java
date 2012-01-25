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
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Version;

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
        if (Action.AUDIO.equals(property)) {
            retVal = Action.AUDIO;
        }
        else if (Action.DISPLAY.equals(property)) {
            retVal = Action.DISPLAY;
        }
        else if (Action.EMAIL.equals(property)) {
            retVal = Action.EMAIL;
        }
        else if (Action.PROCEDURE.equals(property)) {
            retVal = Action.PROCEDURE;
        }
        else if (CalScale.GREGORIAN.equals(property)) {
            retVal = CalScale.GREGORIAN;
        }
        else if (Clazz.CONFIDENTIAL.equals(property)) {
            retVal = Clazz.CONFIDENTIAL;
        }
        else if (Clazz.PRIVATE.equals(property)) {
            retVal = Clazz.PRIVATE;
        }
        else if (Clazz.PUBLIC.equals(property)) {
            retVal = Clazz.PUBLIC;
        }
        else if (Method.ADD.equals(property)) {
            retVal = Method.ADD;
        }
        else if (Method.CANCEL.equals(property)) {
            retVal = Method.CANCEL;
        }
        else if (Method.COUNTER.equals(property)) {
            retVal = Method.COUNTER;
        }
        else if (Method.DECLINE_COUNTER.equals(property)) {
            retVal = Method.DECLINE_COUNTER;
        }
        else if (Method.PUBLISH.equals(property)) {
            retVal = Method.PUBLISH;
        }
        else if (Method.REFRESH.equals(property)) {
            retVal = Method.REFRESH;
        }
        else if (Method.REPLY.equals(property)) {
            retVal = Method.REPLY;
        }
        else if (Method.REQUEST.equals(property)) {
            retVal = Method.REQUEST;
        }
        else if (Priority.HIGH.equals(property)) {
            retVal = Priority.HIGH;
        }
        else if (Priority.LOW.equals(property)) {
            retVal = Priority.LOW;
        }
        else if (Priority.MEDIUM.equals(property)) {
            retVal = Priority.MEDIUM;
        }
        else if (Priority.UNDEFINED.equals(property)) {
            retVal = Priority.UNDEFINED;
        }
        else if (Status.VEVENT_CANCELLED.equals(property)) {
            retVal = Status.VEVENT_CANCELLED;
        }
        else if (Status.VEVENT_CONFIRMED.equals(property)) {
            retVal = Status.VEVENT_CONFIRMED;
        }
        else if (Status.VEVENT_TENTATIVE.equals(property)) {
            retVal = Status.VEVENT_TENTATIVE;
        }
        else if (Status.VJOURNAL_CANCELLED.equals(property)) {
            retVal = Status.VJOURNAL_CANCELLED;
        }
        else if (Status.VJOURNAL_DRAFT.equals(property)) {
            retVal = Status.VJOURNAL_DRAFT;
        }
        else if (Status.VJOURNAL_FINAL.equals(property)) {
            retVal = Status.VJOURNAL_FINAL;
        }
        else if (Status.VTODO_CANCELLED.equals(property)) {
            retVal = Status.VTODO_CANCELLED;
        }
        else if (Status.VTODO_COMPLETED.equals(property)) {
            retVal = Status.VTODO_COMPLETED;
        }
        else if (Status.VTODO_IN_PROCESS.equals(property)) {
            retVal = Status.VTODO_IN_PROCESS;
        }
        else if (Status.VTODO_NEEDS_ACTION.equals(property)) {
            retVal = Status.VTODO_NEEDS_ACTION;
        }
        else if (Transp.OPAQUE.equals(property)) {
            retVal = Transp.OPAQUE;
        }
        else if (Transp.TRANSPARENT.equals(property)) {
            retVal = Transp.TRANSPARENT;
        }
        else if (Version.VERSION_2_0.equals(property)) {
            retVal = Version.VERSION_2_0;
        }
        return retVal;
    }
}
