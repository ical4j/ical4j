/*
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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

/**
 * <pre>
 *      Property Name:
 *     RESOURCE-TYPE
 * Purpose:
 *     This property specifies the type of resource.
 * Value Type:
 *     The value type for this property is TEXT. The allowable values are defined below.
 * Format Definition:
 *
 *     This property is defined by the following notation:
 *
 * restypeprop   = "RESOURCE-TYPE" restypeparam ":"
 *                 restypevalue CRLF
 *
 * restypevalue  = ("ROOM"
 *                / "PROJECTOR"
 *                / "REMOTE-CONFERENCE-AUDIO"
 *                / "REMOTE-CONFERENCE-VIDEO"
 *                / iana-token)     ; Other IANA-registered
 *                ; values
 *
 * restypeparam   = *(";" other-param)
 *
 * Description:
 *
 *     This property MAY be specified in "VRESOURCE" components and provides a way to differentiate multiple resources.
 * The registered values are described below. New resource types SHOULD be registered in the manner laid down in this specification.
 * ROOM:
 *     A room for the event/meeting.
 * PROJECTOR:
 *     Projection equipment.
 * REMOTE-CONFERENCE-AUDIO:
 *     Audio remote conferencing facilities.
 * REMOTE-CONFERENCE-VIDEO:
 *     Video remote conferencing facilities.
 * </pre>
 *
 * @author benf
 * @author Mike Douglass
 */
public class ResourceType extends Property implements Encodable {

    private static final long serialVersionUID = 7753849118575885600L;

    /* Defined in eventpub draft. */
    public static final String room = "ROOM";
    /*  A room for the event/meeting. */

    public static final String projector = "PROJECTOR";
    /*  Projection equipment. */

    public static final String remoteConferenceAudio =
            "REMOTE-CONFERENCE-AUDIO";
    /*  Audio remote conferencing facilities. */

    public static final String remoteConferenceVideo =
            "REMOTE-CONFERENCE-VIDEO";
    /*  Video remote conferencing facilities. */

    private String value;

    /**
     * Default constructor.
     */
    public ResourceType() {
        this(null);
    }

    /**
     * @param aValue a value string for this component
     */
    public ResourceType(final String aValue) {
        this(new ParameterList(), aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public ResourceType(final ParameterList aList, final String aValue) {
        super(RESOURCE_TYPE, aList);
        setValue(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(final String aValue) {
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return ValidationResult.EMPTY;
    }

    @Override
    protected PropertyFactory<ResourceType> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<ResourceType> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(RESOURCE_TYPE);
        }

        public ResourceType createProperty(final ParameterList parameters, final String value) {
            return new ResourceType(parameters, value);
        }

        public ResourceType createProperty() {
            return new ResourceType();
        }
    }

}
