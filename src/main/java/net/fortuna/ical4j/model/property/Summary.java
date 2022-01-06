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
 * Defines a SUMMARY iCalendar component property.
 * <p/>
 * <pre>
 *     4.8.1.12 Summary
 *
 *        Property Name: SUMMARY
 *
 *        Purpose: This property defines a short summary or subject for the
 *        calendar component.
 *
 *        Value Type: TEXT
 *
 *        Property Parameters: Non-standard, alternate text representation and
 *        language property parameters can be specified on this property.
 *
 *        Conformance: The property can be specified in &quot;VEVENT&quot;, &quot;VTODO&quot;,
 *        &quot;VJOURNAL&quot; or &quot;VALARM&quot; calendar components.
 *
 *        Description: This property is used in the &quot;VEVENT&quot;, &quot;VTODO&quot; and
 *        &quot;VJOURNAL&quot; calendar components to capture a short, one line summary
 *        about the activity or journal entry.
 *
 *        This property is used in the &quot;VALARM&quot; calendar component to capture
 *        the subject of an EMAIL category of alarm.
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          summary    = &quot;SUMMARY&quot; summparam &quot;:&quot; text CRLF
 *
 *          summparam  = *(
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
 *        Example: The following is an example of this property:
 *
 *          SUMMARY:Department Party
 * </pre>
 *
 * @author Ben Fortuna
 */
public class Summary extends Property implements Encodable {

    private static final long serialVersionUID = 7709437653910363024L;

    private String value;

    /**
     * Default constructor.
     */
    public Summary() {
        super(SUMMARY, new ParameterList(), new Factory());
    }

    /**
     * @param aValue a value string for this component
     */
    public Summary(final String aValue) {
        super(SUMMARY, new ParameterList(), new Factory());
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Summary(final ParameterList aList, final String aValue) {
        super(SUMMARY, aList, new Factory());
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
        PropertyValidator.SUMMARY.validate(this);
    }

    public static class Factory extends Content.Factory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(SUMMARY);
        }

        @Override
        public Property createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Summary(parameters, value);
        }

        @Override
        public Property createProperty() {
            return new Summary();
        }
    }

}
