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
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.function.Function;

/**
 * Defines an iCalendar property. Subclasses of this class provide additional validation and typed values for specific
 * iCalendar properties.
 * <p/>
 * Note that subclasses must provide a reference to the factory used to create the
 * property to support property cloning (copy). If no factory is specified an
 * {@link UnsupportedOperationException} will be thrown by the {@link #copy()} method.
 *
 * @author Ben Fortuna
 *         <p/>
 *         $Id$ [Apr 5, 2004]
 */
public abstract class Property extends Content implements Comparable<Property> {

    private static final long serialVersionUID = 7048785558435608687L;

    // iCalendar properties..

    /**
     * Product identifier property name.
     */
    public static final String PRODID = "PRODID";

    /**
     * iCalendar version property name.
     */
    public static final String VERSION = "VERSION";

    /**
     * Calendar scale property name.
     */
    public static final String CALSCALE = "CALSCALE";

    /**
     * iTIP method property name.
     */
    public static final String METHOD = "METHOD";

    // Component properties..

    /**
     * Busy type property name.
     */
    public static final String BUSYTYPE = "BUSYTYPE";

    /**
     * Classifier property name.
     */
    public static final String CLASS = "CLASS";

    /**
     * Creation date property name.
     */
    public static final String CREATED = "CREATED";

    /**
     * Description property name.
     */
    public static final String DESCRIPTION = "DESCRIPTION";

    /**
     * Start date property name.
     */
    public static final String DTSTART = "DTSTART";

    /**
     * Geographic location property name.
     */
    public static final String GEO = "GEO";

    /**
     * Last modified date property name.
     */
    public static final String LAST_MODIFIED = "LAST-MODIFIED";

    /**
     * Location property name.
     */
    public static final String LOCATION = "LOCATION";

    /**
     * Organiser property name.
     */
    public static final String ORGANIZER = "ORGANIZER";

    /**
     * Percentage complete property name.
     */
    public static final String PERCENT_COMPLETE = "PERCENT-COMPLETE";

    /**
     * Prority property name.
     */
    public static final String PRIORITY = "PRIORITY";

    /**
     * Date-stamp property name.
     */
    public static final String DTSTAMP = "DTSTAMP";

    /**
     * Sequence property name.
     */
    public static final String SEQUENCE = "SEQUENCE";

    /**
     * Status property name.
     */
    public static final String STATUS = "STATUS";

    /**
     * Summary property name.
     */
    public static final String SUMMARY = "SUMMARY";

    /**
     * Transparency property name.
     */
    public static final String TRANSP = "TRANSP";

    /**
     * Unique identifier property name.
     */
    public static final String UID = "UID";

    /**
     * Uniform resource locator property name.
     */
    public static final String URL = "URL";

    /**
     * Recurrence identifier property name.
     */
    public static final String RECURRENCE_ID = "RECURRENCE-ID";

    /**
     * Completed date property name.
     */
    public static final String COMPLETED = "COMPLETED";

    /**
     * Due date property name.
     */
    public static final String DUE = "DUE";

    /**
     * Free/busy property name.
     */
    public static final String FREEBUSY = "FREEBUSY";

    /**
     * Timezone identifier property name.
     */
    public static final String TZID = "TZID";

    /**
     * Timezone name property name.
     */
    public static final String TZNAME = "TZNAME";

    /**
     * Prior timezone offset property name.
     */
    public static final String TZOFFSETFROM = "TZOFFSETFROM";

    /**
     * New timezone offset property name.
     */
    public static final String TZOFFSETTO = "TZOFFSETTO";

    /**
     * URL for timezone definition property name.
     */
    public static final String TZURL = "TZURL";

    /**
     * Alarm action property name.
     */
    public static final String ACTION = "ACTION";

    /**
     * Repeat rule property name.
     */
    public static final String REPEAT = "REPEAT";

    /**
     * Alarm trigger property name.
     */
    public static final String TRIGGER = "TRIGGER";

    /**
     * Request status property name.
     */
    public static final String REQUEST_STATUS = "REQUEST-STATUS";

    /**
     * End date property name.
     */
    public static final String DTEND = "DTEND";

    /**
     * Duration property name.
     */
    public static final String DURATION = "DURATION";

    /**
     * Attachment property name.
     */
    public static final String ATTACH = "ATTACH";

    /**
     * Attendee property name.
     */
    public static final String ATTENDEE = "ATTENDEE";

    /**
     * Categories property name.
     */
    public static final String CATEGORIES = "CATEGORIES";

    /**
     * Comment property name.
     */
    public static final String COMMENT = "COMMENT";

    /**
     * Contact property name.
     */
    public static final String CONTACT = "CONTACT";

    /**
     * Exclusion date property name.
     */
    public static final String EXDATE = "EXDATE";

    /**
     * Exclusion rule property name.
     */
    public static final String EXRULE = "EXRULE";

    /**
     * Relationship property name.
     */
    public static final String RELATED_TO = "RELATED-TO";

    /**
     * Resource type property name.
     */
    public static final String RESOURCE_TYPE = "RESOURCE-TYPE";

    /**
     * Resources property name.
     */
    public static final String RESOURCES = "RESOURCES";

    /**
     * Recurrence date property name.
     */
    public static final String RDATE = "RDATE";

    /**
     * Recurrence rule property name.
     */
    public static final String RRULE = "RRULE";

    /**
     * Prefix for non-standard properties.
     */
    public static final String EXPERIMENTAL_PREFIX = "X-";

    /**
     * VVENUE country property name.
     */
    public static final String COUNTRY = "COUNTRY";

    /**
     * VVENUE extended address property name.
     */
    public static final String EXTENDED_ADDRESS = "EXTENDED-ADDRESS";

    /**
     * VVENUE locality property name.
     */
    public static final String LOCALITY = "LOCALITY";

    /**
     * VVENUE name property name.
     */
    public static final String NAME = "NAME";

    /**
     * VVENUE postal code property name.
     */
    public static final String POSTALCODE = "POSTAL-CODE";

    /**
     * VVENUE region property name.
     */
    public static final String REGION = "REGION";

    /**
     * VVENUE street address property name.
     */
    public static final String STREET_ADDRESS = "STREET-ADDRESS";

    /**
     * VVENUE telephone property name.
     */
    public static final String TEL = "TEL";

    /**
     *  Acknowledged Property taken from http://tools.ietf.org/html/draft-daboo-valarm-extensions-04
     */
    public static final String ACKNOWLEDGED = "ACKNOWLEDGED";

    public static final String PROXIMITY = "PROXIMITY";

    /* Event publication properties */

    /**
     * Participant cua property name.
     */
    public static final String CALENDAR_ADDRESS = "CALENDAR-ADDRESS";

    /**
     * Location type property name.
     */
    public static final String LOCATION_TYPE = "LOCATION-TYPE";

    /**
     * Participant type.
     */
    public static final String PARTICIPANT_TYPE = "PARTICIPANT-TYPE";

    /**
     * Structured data property name.
     */
    public static final String STRUCTURED_DATA = "STRUCTURED-DATA";

    /**
     * Styled description property name.
     */
    public static final String STYLED_DESCRIPTION = "STYLED-DESCRIPTION";

    private final String name;

    private final ParameterList parameters;

    private final PropertyFactory factory;

    /**
     * Constructor.
     *
     * @param aName   property name
     * @param factory the factory used to create the property instance
     */
    protected Property(final String aName, PropertyFactory factory) {
        this(aName, new ParameterList(), factory);
    }

    /**
     * Constructor made protected to enforce the use of <code>PropertyFactory</code> for property instantiation.
     * @param aName property name
     * @param aList a list of parameters
     */
//    protected Property(final String aName, final ParameterList aList) {
//        this(aName, aList, new Factory());
//    }

    /**
     * @param aName   a property identifier
     * @param aList   a list of initial parameters
     * @param factory the factory used to create the property instance
     */
    protected Property(final String aName, final ParameterList aList, PropertyFactory factory) {
        this.name = aName;
        this.parameters = aList;
        this.factory = factory;
    }

    /**
     * Creates a deep copy of the specified property. That is, the name, parameter list, and value are duplicated from
     * the specified property. This constructor should only be called from sub-classes to ensure type integrity is
     * maintained.
     *
     * @param property a property to copy
     * @throws URISyntaxException where the specified property contains an invalid URI value
     * @throws ParseException     where the specified property has invalid data
     * @throws IOException        where an error occurs reading data from the specified property
     * @deprecated Use {@link #copy()} instead
     */
    protected Property(final Property property) throws IOException,
            URISyntaxException, ParseException {
        this(property.getName(), new ParameterList(property.getParameters(), false),
                property.factory);
        setValue(property.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(getName());
        if (getParameters() != null) {
            buffer.append(getParameters());
        }
        buffer.append(':');
        String value;

        if (this instanceof XProperty && getParameter(Parameter.VALUE) != null
                && !Value.TEXT.equals(getParameter(Parameter.VALUE))) {
            value = getValue();
        } else if (this instanceof Encodable) {
            try {
                value = PropertyCodec.INSTANCE.encode(getValue());
            } catch (EncoderException e) {
                value = getValue();
            }
        } else {
            value = getValue();
        }
        buffer.append(Strings.valueOf(value));
        buffer.append(Strings.LINE_SEPARATOR);

        return buffer.toString();
    }

    /**
     * @return Returns the name.
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * @return Returns the parameters.
     */
    public final ParameterList getParameters() {
        return parameters;
    }

    /**
     * Convenience method for retrieving a list of named parameters.
     *
     * @param name name of parameters to retrieve
     * @return a parameter list containing only parameters with the specified name
     */
    public final ParameterList getParameters(final String name) {
        return getParameters().getParameters(name);
    }

    /**
     * Convenience method for retrieving a single parameter.
     *
     * @param name name of the parameter to retrieve
     * @return the first parameter from the parameter list with the specified name
     */
    public final <T extends Parameter> T getParameter(final String name) {
        return getParameters().getParameter(name);
    }

    /**
     * Sets the current value of the property.
     *
     * @param aValue a string representation of the property value
     * @throws IOException        possibly thrown by setting the value of certain properties
     * @throws URISyntaxException possibly thrown by setting the value of certain properties
     * @throws ParseException     possibly thrown by setting the value of certain properties
     */
    public abstract void setValue(String aValue) throws IOException, URISyntaxException, ParseException;

    /**
     * Perform validation on a property.
     *
     * @throws ValidationException where the property is not in a valid state
     */
    public abstract void validate() throws ValidationException;

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof Property) {
            final Property p = (Property) arg0;
            return getName().equals(p.getName())
                    && new EqualsBuilder().append(getValue(), p.getValue()).append(getParameters(), p.getParameters()).isEquals();
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // as property name is case-insensitive generate hash for uppercase..
        return new HashCodeBuilder().append(getName().toUpperCase()).append(
                getValue()).append(getParameters()).toHashCode();
    }

    /**
     * Create a (deep) copy of this property.
     *
     * @return the copy of the property
     * @throws IOException        where an error occurs reading property data
     * @throws URISyntaxException where the property contains an invalid URI value
     * @throws ParseException     where the property contains an invalid date value
     */
    public Property copy() throws IOException, URISyntaxException, ParseException {
        if (factory == null) {
            throw new UnsupportedOperationException("No factory specified");
        }
        // Deep copy parameter list..
        final ParameterList params = new ParameterList(getParameters(), false);
        return factory.createProperty(params, getValue());
    }

    @Override
    public int compareTo(Property o) {
        if (this.equals(o)) {
            return 0;
        }
        return Comparator.comparing(Property::getName)
                .thenComparing(Property::getValue)
                .thenComparing((Function<Property, ParameterList>) Property::getParameters)
                .compare(this, o);
    }
}
