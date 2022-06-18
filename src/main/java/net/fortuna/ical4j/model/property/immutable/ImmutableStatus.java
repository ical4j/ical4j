/*
 *  Copyright (c) 2022, Ben Fortuna
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

package net.fortuna.ical4j.model.property.immutable;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.ImmutableProperty;
import net.fortuna.ical4j.model.property.Status;

/**
 * @author Ben Fortuna An immutable instance of Status.
 */
public final class ImmutableStatus extends Status implements ImmutableProperty {

    /**
     * Tentative VEVENT status.
     */
    public static final Status VEVENT_TENTATIVE = new ImmutableStatus(VALUE_TENTATIVE);
    /**
     * Confirmed VEVENT status.
     */
    public static final Status VEVENT_CONFIRMED = new ImmutableStatus(VALUE_CONFIRMED);
    /**
     * Cancelled VEVENT status.
     */
    public static final Status VEVENT_CANCELLED = new ImmutableStatus(VALUE_CANCELLED);
    /**
     * Tentative VTODO status.
     */
    public static final Status VTODO_NEEDS_ACTION = new ImmutableStatus(VALUE_NEEDS_ACTION);
    /**
     * Completed VTODO status.
     */
    public static final Status VTODO_COMPLETED = new ImmutableStatus(VALUE_COMPLETED);
    /**
     * In-process VTODO status.
     */
    public static final Status VTODO_IN_PROCESS = new ImmutableStatus(VALUE_IN_PROCESS);
    /**
     * Cancelled VTODO status.
     */
    public static final Status VTODO_CANCELLED = new ImmutableStatus(VALUE_CANCELLED);
    /**
     * Draft VJOURNAL status.
     */
    public static final Status VJOURNAL_DRAFT = new ImmutableStatus(VALUE_DRAFT);
    /**
     * Final VJOURNAL status.
     */
    public static final Status VJOURNAL_FINAL = new ImmutableStatus(VALUE_FINAL);
    /**
     * Cancelled VJOURNAL status.
     */
    public static final Status VJOURNAL_CANCELLED = new ImmutableStatus(VALUE_CANCELLED);
    private static final long serialVersionUID = 7771868877237685612L;

    public ImmutableStatus(final String value) {
        super(value);
    }

    @Override
    public <T extends Property> T add(Parameter parameter) {
        return ImmutableProperty.super.add(parameter);
    }

    @Override
    public <T extends Property> T remove(Parameter parameter) {
        return ImmutableProperty.super.remove(parameter);
    }

    @Override
    public <T extends Property> T removeAll(String... parameterName) {
        return ImmutableProperty.super.removeAll(parameterName);
    }

    @Override
    public <T extends Property> T replace(Parameter parameter) {
        return ImmutableProperty.super.replace(parameter);
    }

    @Override
    public void setValue(final String aValue) {
        ImmutableProperty.super.setValue(aValue);
    }
}
