/*
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Copyright (c) 2004, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
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
package net.fortuna.ical4j.model.property;

import java.text.ParseException;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;

/**
 * Defines a CREATED iCalendar component property.
 *
 * <pre>
 * 4.8.7.1 Date/Time Created
 * 
 *    Property Name: CREATED
 * 
 *    Purpose: This property specifies the date and time that the calendar
 *    information was created by the calendar user agent in the calendar
 *    store.
 * 
 *         Note: This is analogous to the creation date and time for a file
 *         in the file system.
 * 
 *    Value Type: DATE-TIME
 * 
 *    Property Parameters: Non-standard property parameters can be
 *    specified on this property.
 * 
 *    Conformance: The property can be specified once in "VEVENT", "VTODO"
 *    or "VJOURNAL" calendar components.
 * 
 *    Description: The date and time is a UTC value.
 * 
 *    Format Definition: The property is defined by the following notation:
 * 
 *      created    = "CREATED" creaparam ":" date-time CRLF
 * 
 *      creaparam  = *(";" xparam)
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Created extends Property {
    
    private static final long serialVersionUID = -8658935097721652961L;

    /**
     * The date and time is a UTC value.
     */
    private DateTime dateTime;

    /**
     * Default constructor.
     */
    public Created() {
        super(CREATED);
        dateTime = new DateTime();
    }
    
    /**
     * @param aValue
     *            a value string for this component
     * @throws ParseException
     *             where the specified value string is not a valid
     *             date-time/date representation
     */
    public Created(final String aValue)
            throws ParseException {
        super(CREATED);
        setValue(aValue);
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param aValue
     *            a value string for this component
     * @throws ParseException
     *             where the specified value string is not a valid
     *             date-time/date representation
     */
    public Created(final ParameterList aList, final String aValue)
            throws ParseException {
        super(CREATED, aList);
        setValue(aValue);
    }

    /**
     * @param aDate
     *            a date
     */
    public Created(final DateTime aDate) {
        super(CREATED);
        dateTime = aDate;
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param aDate
     *            a date
     */
    public Created(final ParameterList aList, final DateTime aDate) {
        super(CREATED, aList);
        dateTime = aDate;
    }

    /**
     * @return Returns the date-time.
     */
    public final DateTime getDateTime() {
        return dateTime;
    }
    
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public final void setValue(final String aValue) throws ParseException {
        dateTime = new DateTime(aValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public final String getValue() {
        return getDateTime().toString();
    }
}