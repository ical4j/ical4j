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
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;

import static net.fortuna.ical4j.model.Property.*;

/**
 * $Id$ [May 1 2017]
 *
 * Defines an iCalendar VRESOURCE component.
 *
 * <pre>
 Component name:  VRESOURCE

 Purpose:  This component provides a typed reference to external
 information about a resource or optionally a plain text typed
 value.  Typically a resource is anything that might be required or
 used by a calendar entity and possibly has a directory entry.

 Conformance:  This component can be specified multiple times in a
 "VEVENT", "VTODO", "VJOURNAL", "VFREEBUSY" or "PARTICIPANT"
 calendar component.

 Description:  When used in a component this component provides
 information about resources used for the event such as rooms,
 projectors, conferencing capabilities.

 The RESOURCE-TYPE value registry provides a place in which
 resource types may be registered.

 STRUCTURED-DATA properties if present may refer to representations
 of the resource - such as a vCard.

 Format Definition:

 This component is defined by the following notation:

 resourcec    = "BEGIN" ":" "VRESOURCE" CRLF
                resprop
                "END" ":" "VRESOURCE" CRLF

 resprop      = ; the elements herein may appear in any order,
                ; and the order is not significant.

                uid

                (name)
                (description)
                (geo)
                (restype)

                *sdataprop
                *iana-prop

 The NAME property is defined in [RFC7986]

 * </pre>
 *
 * @author Mike Douglass
 */
public class VResource extends Component {
    private static final long serialVersionUID = -8193965477414653802L;

    /**
     * Default constructor.
     */
    public VResource() {
        super(VRESOURCE);
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VResource(final PropertyList<Property> properties) {
        super(VRESOURCE, properties);
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VResource(final PropertyList<Property> properties,
                     final ComponentList<Component> components) {
        super(VRESOURCE, properties);
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse) throws ValidationException {
        ComponentValidator.VRESOURCE.validate(this);
        if (recurse) {
            validateProperties();
        }
    }

    /**
     * Returns the optional description property.
     * @return the DESCRIPTION property or null if not specified
     */
    public final Description getDescription() {
        return getProperty(DESCRIPTION);
    }

    /**
     * @return the optional geo property for a vresource
     */
    public final Geo getGeo() {
        return getProperty(LAST_MODIFIED);
    }

    /**
     * Returns the optional ResourceType property.
     * @return the ResourceType property or null if not specified
     */
    public ResourceType getResourceType() {
        return getProperty(RESOURCE_TYPE);
    }

    /**
     * @return the optional name property for a vresource
     */
    public final Name getNameProp() {
        return getProperty(NAME);
    }

    /**
     * Returns the UID property of this component if available.
     * @return a Uid instance, or null if no UID property exists
     */
    public final Uid getUid() {
        return getProperty(UID);
    }

    /**
     * @return the optional structured data properties
     */
    public final PropertyList<StructuredData> getStructuredData() {
        return getProperties(STRUCTURED_DATA);
    }

    public static class Factory extends Content.Factory
            implements ComponentFactory<VResource> {

        public Factory() {
            super(VRESOURCE);
        }

        @Override
        public VResource createComponent() {
            return new VResource();
        }

        @Override
        public VResource createComponent(final PropertyList<Property> properties) {
            return new VResource(properties);
        }
    }
}
