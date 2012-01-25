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

import java.net.URI;
import java.net.URISyntaxException;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a URL iCalendar component property.
 * 
 * <pre>
 *     4.8.4.6 Uniform Resource Locator
 *     
 *        Property Name: URL
 *     
 *        Purpose: This property defines a Uniform Resource Locator (URL)
 *        associated with the iCalendar object.
 *     
 *        Value Type: URI
 *     
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *     
 *        Conformance: This property can be specified once in the &quot;VEVENT&quot;,
 *        &quot;VTODO&quot;, &quot;VJOURNAL&quot; or &quot;VFREEBUSY&quot; calendar components.
 *     
 *        Description: This property may be used in a calendar component to
 *        convey a location where a more dynamic rendition of the calendar
 *        information associated with the calendar component can be found. This
 *        memo does not attempt to standardize the form of the URI, nor the
 *        format of the resource pointed to by the property value. If the URL
 *        property and Content-Location MIME header are both specified, they
 *        MUST point to the same resource.
 *     
 *        Format Definition: The property is defined by the following notation:
 *     
 *          url        = &quot;URL&quot; urlparam &quot;:&quot; uri CRLF
 *     
 *          urlparam   = *(&quot;;&quot; xparam)
 *     
 *        Example: The following is an example of this property:
 *     
 *          URL:http://abc.com/pub/calendars/jsmith/mytime.ics
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Url extends Property {

    private static final long serialVersionUID = 1092576402256525737L;

    private URI uri;

    /**
     * Default constructor.
     */
    public Url() {
        super(URL, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     * @throws URISyntaxException where the specified value string is not a valid uri
     */
    public Url(final ParameterList aList, final String aValue)
            throws URISyntaxException {
        super(URL, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param aUri a URI
     */
    public Url(final URI aUri) {
        super(URL, PropertyFactoryImpl.getInstance());
        uri = aUri;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aUri a URI
     */
    public Url(final ParameterList aList, final URI aUri) {
        super(URL, aList, PropertyFactoryImpl.getInstance());
        uri = aUri;
    }

    /**
     * @return Returns the uri.
     */
    public final URI getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) throws URISyntaxException {
        uri = Uris.create(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return Uris.decode(Strings.valueOf(getUri()));
    }

    /**
     * @param uri The uri to set.
     */
    public final void setUri(final URI uri) {
        this.uri = uri;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
