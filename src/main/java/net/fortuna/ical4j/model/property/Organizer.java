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

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.ParameterValidator;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines an ORGANIZER iCalendar component property.
 * 
 * <pre>
 *     4.8.4.3 Organizer
 *     
 *        Property Name: ORGANIZER
 *     
 *        Purpose: The property defines the organizer for a calendar component.
 *     
 *        Value Type: CAL-ADDRESS
 *     
 *        Property Parameters: Non-standard, language, common name, directory
 *        entry reference, sent by property parameters can be specified on this
 *        property.
 *     
 *        Conformance: This property MUST be specified in an iCalendar object
 *        that specifies a group scheduled calendar entity. This property MUST
 *        be specified in an iCalendar object that specifies the publication of
 *        a calendar user's busy time. This property MUST NOT be specified in
 *        an iCalendar object that specifies only a time zone definition or
 *        that defines calendar entities that are not group scheduled entities,
 *        but are entities only on a single user's calendar.
 *     
 *        Description: The property is specified within the &quot;VEVENT&quot;, &quot;VTODO&quot;,
 *        &quot;VJOURNAL calendar components to specify the organizer of a group
 *        scheduled calendar entity. The property is specified within the
 *        &quot;VFREEBUSY&quot; calendar component to specify the calendar user
 *        requesting the free or busy time. When publishing a &quot;VFREEBUSY&quot;
 *        calendar component, the property is used to specify the calendar that
 *        the published busy time came from.
 *     
 *        The property has the property parameters CN, for specifying the
 *        common or display name associated with the &quot;Organizer&quot;, DIR, for
 *        specifying a pointer to the directory information associated with the
 *        &quot;Organizer&quot;, SENT-BY, for specifying another calendar user that is
 *        acting on behalf of the &quot;Organizer&quot;. The non-standard parameters may
 *        also be specified on this property. If the LANGUAGE property
 *        parameter is specified, the identified language applies to the CN
 *        parameter value.
 *     
 *        Format Definition: The property is defined by the following notation:
 *     
 *          organizer  = &quot;ORGANIZER&quot; orgparam &quot;:&quot;
 *                       cal-address CRLF
 *     
 *          orgparam   = *(
 *     
 *                     ; the following are optional,
 *                     ; but MUST NOT occur more than once
 *     
 *                     (&quot;;&quot; cnparam) / (&quot;;&quot; dirparam) / (&quot;;&quot; sentbyparam) /
 *                     (&quot;;&quot; languageparam) /
 *     
 *                     ; the following is optional,
 *                     ; and MAY occur more than once
 *     
 *                     (&quot;;&quot; xparam)
 *     
 *                     )
 *     
 *        Example: The following is an example of this property:
 *     
 *          ORGANIZER;CN=John Smith:MAILTO:jsmith@host1.com
 *     
 *        The following is an example of this property with a pointer to the
 *        directory information associated with the organizer:
 *     
 *          ORGANIZER;CN=JohnSmith;DIR=&quot;ldap://host.com:6666/o=3DDC%20Associ
 *           ates,c=3DUS??(cn=3DJohn%20Smith)&quot;:MAILTO:jsmith@host1.com
 *     
 *        The following is an example of this property used by another calendar
 *        user who is acting on behalf of the organizer, with responses
 *        intended to be sent back to the organizer, not the other calendar
 *        user:
 *     
 *          ORGANIZER;SENT-BY=&quot;MAILTO:jane_doe@host.com&quot;:
 *           MAILTO:jsmith@host1.com
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Organizer extends Property {

    private static final long serialVersionUID = -5216965653165090725L;

    private URI calAddress;

    /**
     * Default constructor.
     */
    public Organizer() {
        super(ORGANIZER, PropertyFactoryImpl.getInstance());
    }

    /**
     * Constructs a new instance with the specified value.
     * @param value an organizer URI
     * @throws URISyntaxException where the specified value is not a valid URI
     */
    public Organizer(String value) throws URISyntaxException {
        super(ORGANIZER, PropertyFactoryImpl.getInstance());
        setValue(value);
    }
    
    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     * @throws URISyntaxException where the specified value string is not a valid uri
     */
    public Organizer(final ParameterList aList, final String aValue)
            throws URISyntaxException {
        super(ORGANIZER, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param aUri a URI representation of a calendar address
     */
    public Organizer(final URI aUri) {
        super(ORGANIZER, PropertyFactoryImpl.getInstance());
        calAddress = aUri;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aUri a URI representation of a calendar address
     */
    public Organizer(final ParameterList aList, final URI aUri) {
        super(ORGANIZER, aList, PropertyFactoryImpl.getInstance());
        calAddress = aUri;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {

        /*
         * ; the following are optional, ; but MUST NOT occur more than once (";" cnparam) / (";" dirparam) / (";"
         * sentbyparam) / (";" languageparam) /
         */
        ParameterValidator.getInstance().assertOneOrLess(Parameter.CN,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.DIR,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.SENT_BY,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.LANGUAGE,
                getParameters());

        /* schedulestatus added for CalDAV scheduling
         */
        ParameterValidator.getInstance().assertOneOrLess(Parameter.SCHEDULE_STATUS,
                                                         getParameters());

        /*
         * ; the following is optional, ; and MAY occur more than once (";" xparam)
         */
    }

    /**
     * @return Returns the calAddress.
     */
    public final URI getCalAddress() {
        return calAddress;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) throws URISyntaxException {
        calAddress = Uris.create(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return Uris.decode(Strings.valueOf(getCalAddress()));
    }

    /**
     * @param calAddress The calAddress to set.
     */
    public final void setCalAddress(final URI calAddress) {
        this.calAddress = calAddress;
    }
}
