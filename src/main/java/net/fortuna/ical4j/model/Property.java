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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.XProperty;

import net.fortuna.ical4j.util.Strings;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Defines an iCalendar property. Subclasses of this class provide additional validation and typed values for specific
 * iCalendar properties.
 * 
 * Note that subclasses must provide a reference to the factory used to create the
 * property to support property cloning (copy). If no factory is specified an
 * {@link UnsupportedOperationException} will be thrown by the {@link #copy()} method.
 * 
 * @author Ben Fortuna
 * 
 * $Id$ [Apr 5, 2004]
 */
public abstract class Property extends Content {

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
     * VVENUE location type property name.
     */
    public static final String LOCATION_TYPE = "LOCATION-TYPE";

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

    private String name;

    private ParameterList parameters;

    private final PropertyFactory factory;
    
    /**
     * Constructor.
     * @param aName property name
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
//        this(aName, aList, PropertyFactoryImpl.getInstance());
//    }

    /**
     * @param aName a property identifier
     * @param aList a list of initial parameters
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
     * @param property a property to copy
     * @throws URISyntaxException where the specified property contains an invalid URI value
     * @throws ParseException where the specified property has invalid data
     * @throws IOException where an error occurs reading data from the specified property
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
    public final String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getName());
        if (getParameters() != null) {
            buffer.append(getParameters());
        }
        buffer.append(':');
        boolean needsEscape = false;
        if (this instanceof XProperty) {
            Value valParam = (Value)getParameter(Parameter.VALUE);
            if (valParam == null || valParam.equals(Value.TEXT)) {
                needsEscape = true;
            }
        } else if (this instanceof Escapable) {
            needsEscape = true;
        }
        if (needsEscape) {
            buffer.append(Strings.escape(Strings.valueOf(getValue())));
        }
        else {
            buffer.append(Strings.valueOf(getValue()));
        }
        buffer.append(Strings.LINE_SEPARATOR);

        return buffer.toString();
    }

    /**
     * Indicates whether this property is a calendar property.
     * @return boolean
     */
    public boolean isCalendarProperty() {

        return PRODID.equalsIgnoreCase(getName())
                || VERSION.equalsIgnoreCase(getName())
                || CALSCALE.equalsIgnoreCase(getName())
                || METHOD.equalsIgnoreCase(getName());
    }

    /**
     * @return Returns the name.
     */
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
     * @param name name of parameters to retrieve
     * @return a parameter list containing only parameters with the specified name
     */
    public final ParameterList getParameters(final String name) {
        return getParameters().getParameters(name);
    }

    /**
     * Convenience method for retrieving a single parameter.
     * @param name name of the parameter to retrieve
     * @return the first parameter from the parameter list with the specified name
     */
    public final Parameter getParameter(final String name) {
        return getParameters().getParameter(name);
    }

    /**
     * Sets the current value of the property.
     * @param aValue a string representation of the property value
     * @throws IOException possibly thrown by setting the value of certain properties
     * @throws URISyntaxException possibly thrown by setting the value of certain properties
     * @throws ParseException possibly thrown by setting the value of certain properties
     */
    public abstract void setValue(String aValue) throws IOException,
            URISyntaxException, ParseException;

    /**
     * Perform validation on a property.
     * @throws ValidationException where the property is not in a valid state
     */
    public abstract void validate() throws ValidationException;

    /**
     * {@inheritDoc}
     */
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof Property) {
            final Property p = (Property) arg0;
            if (getName().equals(p.getName())) {
                return new EqualsBuilder().append(getValue(), p.getValue())
                    .append(getParameters(), p.getParameters()).isEquals();
            } else {
                return false;
            }
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        // as property name is case-insensitive generate hash for uppercase..
        return new HashCodeBuilder().append(getName().toUpperCase()).append(
                getValue()).append(getParameters()).toHashCode();
    }

    /**
     * Create a (deep) copy of this property.
     * @return the copy of the property
     * @throws IOException where an error occurs reading property data
     * @throws URISyntaxException where the property contains an invalid URI value
     * @throws ParseException where the property contains an invalid date value
     */
    public Property copy() throws IOException, URISyntaxException, ParseException {
        if (factory == null) {
            throw new UnsupportedOperationException("No factory specified");
        }
        // Deep copy parameter list..
        final ParameterList params = new ParameterList(getParameters(), false);
        return factory.createProperty(getName(), params, getValue());
    }
}
