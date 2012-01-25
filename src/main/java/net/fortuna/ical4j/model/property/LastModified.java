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

import java.text.ParseException;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactoryImpl;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a LAST-MODIFIED iCalendar component property.
 * 
 * <pre>
 *     4.8.7.3 Last Modified
 *     
 *        Property Name: LAST-MODIFIED
 *     
 *        Purpose: The property specifies the date and time that the
 *        information associated with the calendar component was last revised
 *        in the calendar store.
 *     
 *             Note: This is analogous to the modification date and time for a
 *             file in the file system.
 *     
 *        Value Type: DATE-TIME
 *     
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *     
 *        Conformance: This property can be specified in the &quot;EVENT&quot;, &quot;VTODO&quot;,
 *        &quot;VJOURNAL&quot; or &quot;VTIMEZONE&quot; calendar components.
 *     
 *        Description: The property value MUST be specified in the UTC time
 *        format.
 *     
 *        Format Definition: The property is defined by the following notation:
 *     
 *          last-mod   = &quot;LAST-MODIFIED&quot; lstparam &quot;:&quot; date-time CRLF
 *     
 *          lstparam   = *(&quot;;&quot; xparam)
 * </pre>
 * 
 * @author benf
 */
public class LastModified extends UtcProperty {

    private static final long serialVersionUID = 5288572652052836062L;

    /**
     * Default constructor.
     */
    public LastModified() {
        super(LAST_MODIFIED, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aValue a date-time value
     * @throws ParseException where the specified string is not a valid date-time
     */
    public LastModified(final String aValue) throws ParseException {
    	this(new ParameterList(), aValue);
    }
    
    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     * @throws ParseException where the specified value string is not a valid date-time/date representation
     */
    public LastModified(final ParameterList aList, final String aValue)
            throws ParseException {
        super(LAST_MODIFIED, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param aDate a date representation of a date-time value
     */
    public LastModified(final DateTime aDate) {
        super(LAST_MODIFIED, PropertyFactoryImpl.getInstance());
        // time must be in UTC..
        aDate.setUtc(true);
        setDate(aDate);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aDate a date representation of a date-time value
     */
    public LastModified(final ParameterList aList, final DateTime aDate) {
        super(LAST_MODIFIED, aList, PropertyFactoryImpl.getInstance());
        // time must be in UTC..
        aDate.setUtc(true);
        setDate(aDate);
    }
}
