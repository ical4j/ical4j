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
 * Defines an ATTENDEE iCalendar component property.
 *
 * @author benf
 */
public class Attendee extends Property {

    private static final long serialVersionUID = 8430929418723298803L;

    private URI calAddress;

    /**
     * Default constructor.
     */
    public Attendee() {
        super(ATTENDEE, new Factory());
    }

    /**
     * @param aValue a value string for this component
     * @throws URISyntaxException where the specified value string is not a valid uri
     */
    public Attendee(final String aValue) throws URISyntaxException {
        super(ATTENDEE, new Factory());
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     * @throws URISyntaxException where the specified value string is not a valid uri
     */
    public Attendee(final ParameterList aList, final String aValue)
            throws URISyntaxException {
        super(ATTENDEE, aList, new Factory());
        setValue(aValue);
    }

    /**
     * @param aUri a URI
     */
    public Attendee(final URI aUri) {
        super(ATTENDEE, new Factory());
        calAddress = aUri;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aUri  a URI
     */
    public Attendee(final ParameterList aList, final URI aUri) {
        super(ATTENDEE, aList, new Factory());
        calAddress = aUri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setValue(final String aValue) throws URISyntaxException {
        calAddress = Uris.create(aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate() throws ValidationException {
        PropertyValidator.ATTENDEE.validate(this);
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
    @Override
    public final String getValue() {
        return Uris.decode(Strings.valueOf(getCalAddress()));
    }

    /**
     * @param calAddress The calAddress to set.
     */
    public final void setCalAddress(final URI calAddress) {
        this.calAddress = calAddress;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Property copy() {
        // URI are immutable
        return new Attendee(new ParameterList(getParameters(), false), calAddress);
    }

    public static class Factory extends Content.Factory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(ATTENDEE);
        }

        @Override
        public Property createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Attendee(parameters, value);
        }

        @Override
        public Property createProperty() {
            return new Attendee();
        }
    }

}
