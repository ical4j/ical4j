/*
 * $Id$ [Apr 5, 2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import net.fortuna.ical4j.util.StringUtils;

/**
 * Defines an iCalendar property. Subclasses of this class provide additional
 * validation and typed values for specific iCalendar properties.
 *
 * @author benf
 */
public abstract class Property extends Content {

    // iCalendar properties..

    /**
     * 'prodid' and 'version' are both REQUIRED, but MUST NOT occur more than
     * once
     */
    public static final String PRODID = "PRODID";

    public static final String VERSION = "VERSION";

    /**
     * 'calscale' and 'method' are optional, but MUST NOT occur more than once
     */
    public static final String CALSCALE = "CALSCALE";

    public static final String METHOD = "METHOD";

    // Component properties..

    /**
     * the following are optional, but MUST NOT occur more than once
     */
    public static final String CLASS = "CLASS";

    public static final String CREATED = "CREATED";

    public static final String DESCRIPTION = "DESCRIPTION";

    public static final String DTSTART = "DTSTART";

    public static final String GEO = "GEO";

    public static final String LAST_MODIFIED = "LAST-MODIFIED";

    public static final String LOCATION = "LOCATION";

    public static final String ORGANIZER = "ORGANIZER";

    public static final String PERCENT_COMPLETE = "PERCENT-COMPLETE";

    public static final String PRIORITY = "PRIORITY";

    public static final String DTSTAMP = "DTSTAMP";

    public static final String SEQUENCE = "SEQUENCE";

    public static final String STATUS = "STATUS";

    public static final String SUMMARY = "SUMMARY";

    public static final String TRANSP = "TRANSP";

    public static final String UID = "UID";

    public static final String URL = "URL";

    public static final String RECURRENCE_ID = "RECURRENCE-ID";

    public static final String COMPLETED = "COMPLETED";

    public static final String DUE = "DUE";

    public static final String FREEBUSY = "FREEBUSY";

    public static final String TZID = "TZID";

    public static final String TZNAME = "TZNAME";

    public static final String TZOFFSETFROM = "TZOFFSETFROM";

    public static final String TZOFFSETTO = "TZOFFSETTO";

    public static final String TZURL = "TZURL";

    public static final String ACTION = "ACTION";

    public static final String REPEAT = "REPEAT";

    public static final String TRIGGER = "TRIGGER";

    public static final String REQUEST_STATUS = "REQUEST-STATUS";

    /**
     * either 'dtend' or 'duration' may appear in a 'eventprop', but 'dtend' and
     * 'duration' MUST NOT occur in the same 'eventprop'
     */
    public static final String DTEND = "DTEND";

    public static final String DURATION = "DURATION";

    /**
     * the following are optional, and MAY occur more than once
     */
    public static final String ATTACH = "ATTACH";

    public static final String ATTENDEE = "ATTENDEE";

    public static final String CATEGORIES = "CATEGORIES";

    public static final String COMMENT = "COMMENT";

    public static final String CONTACT = "CONTACT";

    public static final String EXDATE = "EXDATE";

    public static final String EXRULE = "EXRULE";

    public static final String RELATED_TO = "RELATED-TO";

    public static final String RESOURCES = "RESOURCES";

    public static final String RDATE = "RDATE";

    public static final String RRULE = "RRULE";

    public static final String EXPERIMENTAL_PREFIX = "X-";

    private String name;

    private ParameterList parameters;

    /**
     * Constructor.
     * @param aName
     *            property name
     */
    protected Property(final String aName) {
        this(aName, new ParameterList());
    }

    /**
     * Constructor made protected to enforce the use of
     * <code>PropertyFactory</code> for property instantiation.
     * @param aName
     *            property name
     * @param aList
     *            a list of parameters
     */
    protected Property(final String aName, final ParameterList aList) {
        this.name = aName;
        this.parameters = aList;
    }
    
    /**
     * Creates a deep copy of the specified property. That is, the name,
     * parameter list, and value are duplicated from the specified property.
     * This constructor should only be called from sub-classes to ensure
     * type integrity is maintained.
     * @param property a property to copy
     * @throws URISyntaxException
     */
    protected Property(final Property property) throws IOException, URISyntaxException, ParseException {
        this.name = property.getName();
        this.parameters = new ParameterList(property.getParameters(), false);
        setValue(property.getValue());
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getName());
        buffer.append(getParameters());
        buffer.append(':');
        if (isEscapable()) {
            buffer.append(StringUtils.escape(getValue()));
        }
        else {
            buffer.append(getValue());
        }
        buffer.append("\r\n");

        return buffer.toString();
    }

    /**
     * Indicates whether this property is a
     * calendar property
     * @return boolean
     */
    public final boolean isCalendarProperty() {

        return PRODID.equals(getName()) || VERSION.equals(getName())
                || CALSCALE.equals(getName()) || METHOD.equals(getName());
    }

    /**
     * Indicates whether this property is a
     * component property
     * @return boolean
     */
    public final boolean isComponentProperty() {

        return false;
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
     * Sets the current value of the property.
     * @param aValue a string representation of the property
     * value
     * @throws IOException possibly thrown by setting the value
     * of certain properties
     * @throws URISyntaxException possibly thrown by setting the value
     * of certain properties
     * @throws ParseException possibly thrown by setting the value
     * of certain properties
     */
    public abstract void setValue(String aValue) throws IOException, URISyntaxException, ParseException;

    /**
     * @return Returns the value.
     */
    public abstract String getValue();

    /**
     * Perform validation on a property.
     *
     * @throws ValidationException
     *             where the property is not in a valid state
     */
    public void validate() throws ValidationException {
        // empty implementation..
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    /**
     * Two properties are equal if and only if their
     * name, value and parameter list are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof Property) {
            Property p = (Property) arg0;
            return getName().equals(p.getName())
                    && ((getValue() != null && getValue().equals(p.getValue()))
                            || p.getValue() == null)
                    && getParameters().equals(p.getParameters());
        }
        return super.equals(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        // as parameter name is case-insensitive generate hash for uppercase..
        return getName().toUpperCase().hashCode()
                + getValue().hashCode()
                + getParameters().hashCode();
    }
}