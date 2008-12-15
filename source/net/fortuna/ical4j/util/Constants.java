/*
 * Copyright (c) 2005, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
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
     * @param property
     * @return a Property instance
     */
    public static Property forProperty(final Property property) {
        if (Action.AUDIO.equals(property)) {
            return Action.AUDIO;
        }
        else if (Action.DISPLAY.equals(property)) {
            return Action.DISPLAY;
        }
        else if (Action.EMAIL.equals(property)) {
            return Action.EMAIL;
        }
        else if (Action.PROCEDURE.equals(property)) {
            return Action.PROCEDURE;
        }
        else if (CalScale.GREGORIAN.equals(property)) {
            return CalScale.GREGORIAN;
        }
        else if (Clazz.CONFIDENTIAL.equals(property)) {
            return Clazz.CONFIDENTIAL;
        }
        else if (Clazz.PRIVATE.equals(property)) {
            return Clazz.PRIVATE;
        }
        else if (Clazz.PUBLIC.equals(property)) {
            return Clazz.PUBLIC;
        }
        else if (Method.ADD.equals(property)) {
            return Method.ADD;
        }
        else if (Method.CANCEL.equals(property)) {
            return Method.CANCEL;
        }
        else if (Method.COUNTER.equals(property)) {
            return Method.COUNTER;
        }
        else if (Method.DECLINE_COUNTER.equals(property)) {
            return Method.DECLINE_COUNTER;
        }
        else if (Method.PUBLISH.equals(property)) {
            return Method.PUBLISH;
        }
        else if (Method.REFRESH.equals(property)) {
            return Method.REFRESH;
        }
        else if (Method.REPLY.equals(property)) {
            return Method.REPLY;
        }
        else if (Method.REQUEST.equals(property)) {
            return Method.REQUEST;
        }
        else if (Priority.HIGH.equals(property)) {
            return Priority.HIGH;
        }
        else if (Priority.LOW.equals(property)) {
            return Priority.LOW;
        }
        else if (Priority.MEDIUM.equals(property)) {
            return Priority.MEDIUM;
        }
        else if (Priority.UNDEFINED.equals(property)) {
            return Priority.UNDEFINED;
        }
        else if (Status.VEVENT_CANCELLED.equals(property)) {
            return Status.VEVENT_CANCELLED;
        }
        else if (Status.VEVENT_CONFIRMED.equals(property)) {
            return Status.VEVENT_CONFIRMED;
        }
        else if (Status.VEVENT_TENTATIVE.equals(property)) {
            return Status.VEVENT_TENTATIVE;
        }
        else if (Status.VJOURNAL_CANCELLED.equals(property)) {
            return Status.VJOURNAL_CANCELLED;
        }
        else if (Status.VJOURNAL_DRAFT.equals(property)) {
            return Status.VJOURNAL_DRAFT;
        }
        else if (Status.VJOURNAL_FINAL.equals(property)) {
            return Status.VJOURNAL_FINAL;
        }
        else if (Status.VTODO_CANCELLED.equals(property)) {
            return Status.VTODO_CANCELLED;
        }
        else if (Status.VTODO_COMPLETED.equals(property)) {
            return Status.VTODO_COMPLETED;
        }
        else if (Status.VTODO_IN_PROCESS.equals(property)) {
            return Status.VTODO_IN_PROCESS;
        }
        else if (Status.VTODO_NEEDS_ACTION.equals(property)) {
            return Status.VTODO_NEEDS_ACTION;
        }
        else if (Transp.OPAQUE.equals(property)) {
            return Transp.OPAQUE;
        }
        else if (Transp.TRANSPARENT.equals(property)) {
            return Transp.TRANSPARENT;
        }
        else if (Version.VERSION_2_0.equals(property)) {
            return Version.VERSION_2_0;
        }
        return property;
    }
}
