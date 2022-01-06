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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a LOCATION iCalendar component property.
 * <p/>
 * <pre>
 *     4.8.1.7 Location
 *
 *        Property Name: LOCATION
 *
 *        Purpose: The property defines the intended venue for the activity
 *        defined by a calendar component.
 *
 *        Value Type: TEXT
 *
 *        Property Parameters: Non-standard, alternate text representation and
 *        language property parameters can be specified on this property.
 *
 *        Conformance: This property can be specified in &quot;VEVENT&quot; or &quot;VTODO&quot;
 *        calendar component.
 *
 *        Description: Specific venues such as conference or meeting rooms may
 *        be explicitly specified using this property. An alternate
 *        representation may be specified that is a URI that points to
 *        directory information with more structured specification of the
 *        location. For example, the alternate representation may specify
 *        either an LDAP URI pointing to an LDAP server entry or a CID URI
 *        pointing to a MIME body part containing a vCard [RFC 2426] for the
 *        location.
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          location   = &quot;LOCATION locparam &quot;:&quot; text CRLF
 *
 *          locparam   = *(
 *
 *                     ; the following are optional,
 *                     ; but MUST NOT occur more than once
 *
 *                     (&quot;;&quot; altrepparam) / (&quot;;&quot; languageparam) /
 *
 *                     ; the following is optional,
 *                     ; and MAY occur more than once
 *
 *                     (&quot;;&quot; xparam)
 *
 *                     )
 *
 *        Example: The following are some examples of this property:
 *
 *          LOCATION:Conference Room - F123, Bldg. 002
 *
 *          LOCATION;ALTREP=&quot;http://xyzcorp.com/conf-rooms/f123.vcf&quot;:
 *           Conference Room - F123, Bldg. 002
 * </pre>
 *
 * @author Ben Fortuna
 */
public class Location extends Property implements Encodable {

    private static final long serialVersionUID = 8651881536125682401L;

    private String value;

    /**
     * Default constructor.
     */
    public Location() {
        super(LOCATION, new ParameterList(), new Factory());
    }

    /**
     * @param aValue a value string for this component
     */
    public Location(final String aValue) {
        super(LOCATION, new ParameterList(), new Factory());
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Location(final ParameterList aList, final String aValue) {
        super(LOCATION, aList, new Factory());
        setValue(aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setValue(final String aValue) {
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return value;
    }

    @Override
    public void validate() throws ValidationException {
        PropertyValidator.LOCATION.validate(this);
    }

    public static class Factory extends Content.Factory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(LOCATION);
        }

        @Override
        public Property createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Location(parameters, value);
        }

        @Override
        public Property createProperty() {
            return new Location();
        }
    }

}
