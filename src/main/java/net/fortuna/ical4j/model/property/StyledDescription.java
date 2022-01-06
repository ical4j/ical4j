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
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Uris;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a DESCRIPTION iCalendar component property.
 *
 * @author benf
 */
public class StyledDescription extends Property implements Encodable {

    private static final long serialVersionUID = 7287564228220558361L;

    private String value;
    private URI uriValue;

    /**
     * Default constructor.
     */
    public StyledDescription() {
        super(STYLED_DESCRIPTION, new ParameterList(), 
                new Factory());
    }

    /**
     * @param aValue a value string for this component
     */
    public StyledDescription(final String aValue) throws URISyntaxException {
        super(STYLED_DESCRIPTION, new ParameterList(), 
                new Factory());
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public StyledDescription(final ParameterList aList, final String aValue) throws URISyntaxException {
        super(STYLED_DESCRIPTION, aList, 
                new Factory());
        setValue(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) throws URISyntaxException {
        // value can be either text or a URI - no default
        if (Value.TEXT.equals(getParameter(Parameter.VALUE))) {
            this.value = aValue;
        } else if (Value.URI.equals(getParameter(Parameter.VALUE))) {
            uriValue = Uris.create(aValue);
            this.value = aValue;
        } else {
            throw new IllegalArgumentException("No valid VALUE parameter specified");
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return value;
    }

    @Override
    public void validate() throws ValidationException {
        PropertyValidator.STYLED_DESCRIPTION.validate(this);
    }

    public static class Factory extends Content.Factory implements PropertyFactory<StyledDescription> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(STYLED_DESCRIPTION);
        }

        public StyledDescription createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new StyledDescription(parameters, value);
        }

        public StyledDescription createProperty() {
            return new StyledDescription();
        }
    }

}
