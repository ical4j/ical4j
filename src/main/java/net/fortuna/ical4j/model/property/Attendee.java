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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;

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
 * Defines an ATTENDEE iCalendar component property.
 * @author benf
 */
public class Attendee extends Property {

    private static final long serialVersionUID = 8430929418723298803L;

    private URI calAddress;

    /**
     * Default constructor.
     */
    public Attendee() {
        super(ATTENDEE, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aValue a value string for this component
     * @throws URISyntaxException where the specified value string is not a valid uri
     */
    public Attendee(final String aValue) throws URISyntaxException {
        super(ATTENDEE, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     * @throws URISyntaxException where the specified value string is not a valid uri
     */
    public Attendee(final ParameterList aList, final String aValue)
            throws URISyntaxException {
        super(ATTENDEE, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param aUri a URI
     */
    public Attendee(final URI aUri) {
        super(ATTENDEE, PropertyFactoryImpl.getInstance());
        calAddress = aUri;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aUri a URI
     */
    public Attendee(final ParameterList aList, final URI aUri) {
        super(ATTENDEE, aList, PropertyFactoryImpl.getInstance());
        calAddress = aUri;
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
    public final void validate() throws ValidationException {

        /*
         * ; the following are optional, ; but MUST NOT occur more than once (";" cutypeparam) / (";"memberparam) / (";"
         * roleparam) / (";" partstatparam) / (";" rsvpparam) / (";" deltoparam) / (";" delfromparam) / (";"
         * sentbyparam) / (";"cnparam) / (";" dirparam) / (";" languageparam) /
         */
        ParameterValidator.getInstance().assertOneOrLess(Parameter.CUTYPE,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.MEMBER,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.ROLE,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.PARTSTAT,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.RSVP,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(
                Parameter.DELEGATED_TO, getParameters());
        ParameterValidator.getInstance().assertOneOrLess(
                Parameter.DELEGATED_FROM, getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.SENT_BY,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.CN,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.DIR,
                getParameters());
        ParameterValidator.getInstance().assertOneOrLess(Parameter.LANGUAGE,
                getParameters());

        /* scheduleagent and schedulestatus added for CalDAV scheduling
         */
        ParameterValidator.getInstance().assertOneOrLess(Parameter.SCHEDULE_AGENT,
                                                         getParameters());
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
    public final Property copy() throws IOException, URISyntaxException, ParseException {
        // URI are immutable
        return new Attendee(new ParameterList(getParameters(), false), calAddress);  
    }
}
