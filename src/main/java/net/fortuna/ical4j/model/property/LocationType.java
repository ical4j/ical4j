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
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.property.OneOrLessParameterValidator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a LOCATION_TYPE iCalendar component property.
 *
 * @author benf
 */
public class LocationType extends Property {

    private static final long serialVersionUID = -3541686430899510312L;

    private LocationTypeList locationTypes;

    private final Validator<Property> validator = new OneOrLessParameterValidator(Parameter.LANGUAGE);
    /**
     * Default constructor.
     */
    public LocationType() {
        super(LOCATION_TYPE, new ParameterList(), new Factory());
        locationTypes = new LocationTypeList();
    }

    /**
     * @param aValue a value string for this component
     */
    public LocationType(final String aValue) {
        super(LOCATION_TYPE, new ParameterList(), new Factory());
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public LocationType(final ParameterList aList, final String aValue) {
        super(LOCATION_TYPE, aList, new Factory());
        setValue(aValue);
    }

    /**
     * @param cList a list of locationTypes
     */
    public LocationType(final LocationTypeList cList) {
        super(LOCATION_TYPE, new ParameterList(), new Factory());
        locationTypes = cList;
    }

    /**
     * @param aList a list of parameters for this component
     * @param cList a list of locationTypes
     */
    public LocationType(final ParameterList aList, final LocationTypeList cList) {
        super(LOCATION_TYPE, aList, new Factory());
        locationTypes = cList;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        locationTypes = new LocationTypeList(aValue);
    }

    /**
     * @return Returns the locationTypes.
     */
    public final LocationTypeList getLocationTypes() {
        return locationTypes;
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return getLocationTypes().toString();
    }

    @Override
    public void validate() throws ValidationException {
        validator.validate(this);
    }

    public static class Factory extends Content.Factory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(LOCATION_TYPE);
        }

        public Property createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new LocationType(parameters, value);
        }

        public Property createProperty() {
            return new LocationType();
        }
    }

}
