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

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import net.fortuna.ical4j.validate.property.TimeZonePropertyValidators;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a TZURL iCalendar component property.
 * <p/>
 * <pre>
 *     4.8.3.5 Time Zone URL
 *
 *        Property Name: TZURL
 *
 *        Purpose: The TZURL provides a means for a VTIMEZONE component to
 *        point to a network location that can be used to retrieve an up-to-
 *        date version of itself.
 *
 *        Value Type: URI
 *
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *
 *        Conformance: This property can be specified in a &quot;VTIMEZONE&quot; calendar
 *        component.
 *
 *        Description: The TZURL provides a means for a VTIMEZONE component to
 *        point to a network location that can be used to retrieve an up-to-
 *        date version of itself. This provides a hook to handle changes
 *        government bodies impose upon time zone definitions. Retrieval of
 *        this resource results in an iCalendar object containing a single
 *        VTIMEZONE component and a METHOD property set to PUBLISH.
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          tzurl      = &quot;TZURL&quot; tzurlparam &quot;:&quot; uri CRLF
 *
 *          tzurlparam = *(&quot;;&quot; xparam)
 *
 *        Example: The following is an example of this property:
 *
 *          TZURL:http://timezones.r.us.net/tz/US-California-Los_Angeles
 * </pre>
 *
 * @author Ben Fortuna
 */
public class TzUrl extends Property {

    private static final long serialVersionUID = 9106100107954797406L;

    private URI uri;

    /**
     * Default constructor.
     */
    public TzUrl() {
        super(TZURL);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public TzUrl(final ParameterList aList, final String aValue) {
        super(TZURL, aList);
        setValue(aValue);
    }

    /**
     * @param aUri a URI
     */
    public TzUrl(final URI aUri) {
        super(TZURL);
        uri = aUri;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aUri  a URI
     */
    public TzUrl(final ParameterList aList, final URI aUri) {
        super(TZURL, aList);
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
    @Override
    public final void setValue(final String aValue) {
        try {
            uri = Uris.create(aValue);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return Uris.decode(Strings.valueOf(getUri()));
    }

    /**
     * @param uri The uri to set.
     */
    public final void setUri(final URI uri) {
        this.uri = uri;
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return TimeZonePropertyValidators.TZURL.validate(this);
    }

    @Override
    protected PropertyFactory<TzUrl> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<TzUrl> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(TZURL);
        }

        @Override
        public TzUrl createProperty(final ParameterList parameters, final String value) {
            return new TzUrl(parameters, value);
        }

        @Override
        public TzUrl createProperty() {
            return new TzUrl();
        }
    }

}
