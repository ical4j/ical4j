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
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * 6.1.  Location Type
 *
 *    Property name:  LOCATION-TYPE
 *
 *    Purpose:  To specify the type(s) of a location.
 *
 *    Value type:  The value type for this property is TEXT.  The allowable
 *       values are defined below.
 *
 *    Description:  This property MAY be specified in VLOCATION components
 *       and provides a way to differentiate multiple locations.  For
 *       example, it allows event producers to provide location information
 *       for the venue and the parking.
 *
 *    Format Definition:
 *
 *    This property is defined by the following notation:
 *
 *       loctype      = "LOCATION-TYPE" loctypeparam ":"
 *                      text *("," text)
 *                      CRLF
 *
 *       loctypeparam   = *(";" other-param)
 *
 *       Multiple values may be used if the location has multiple purposes,
 *       for example a hotel and a restaurant.
 *
 *       Values for this parameter are taken from the values defined in
 *       [RFC4589] section 3.  New location types SHOULD be registered in
 *       the manner laid down in section 5 of that specification.
 * </pre>
 *
 * @author Mike Douglass, Ben Fortuna
 * @see <a href="https://tools.ietf.org/html/draft-ietf-calext-eventpub-extensions-18#section-6.1">Event Publishing Extensions to iCalendar</a>
 */
public class LocationType extends Property {

    private static final long serialVersionUID = -3541686430899510312L;

    private List<String> locationTypes;

    /**
     * Default constructor.
     */
    public LocationType() {
        super(LOCATION_TYPE);
        locationTypes = new ArrayList<>();
    }

    /**
     * @param aValue a value string for this component
     */
    public LocationType(final String aValue) {
        super(LOCATION_TYPE);
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public LocationType(final ParameterList aList, final String aValue) {
        super(LOCATION_TYPE, aList);
        setValue(aValue);
    }

    /**
     * @param cList a list of locationTypes
     */
    public LocationType(final List<String> cList) {
        super(LOCATION_TYPE);
        locationTypes = cList;
    }

    /**
     * @param aList a list of parameters for this component
     * @param cList a list of locationTypes
     */
    public LocationType(final ParameterList aList, final List<String> cList) {
        super(LOCATION_TYPE, aList);
        locationTypes = cList;
    }

    public LocationType(net.fortuna.ical4j.model.LocationType... locationTypes) {
        super(LOCATION_TYPE, new ParameterList());
        this.locationTypes = Arrays.stream(locationTypes).map(net.fortuna.ical4j.model.LocationType::toString)
                .collect(Collectors.toList());
    }

    public LocationType(ParameterList params, net.fortuna.ical4j.model.LocationType... locationTypes) {
        super(LOCATION_TYPE, params);
        this.locationTypes = Arrays.stream(locationTypes).map(net.fortuna.ical4j.model.LocationType::toString)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setValue(final String aValue) {
        locationTypes = Collections.singletonList(aValue);
    }

    /**
     * @return Returns the locationTypes.
     */
    public final List<String> getLocationTypes() {
        return locationTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return String.join(",", locationTypes);
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return PropertyValidator.LOCATION_TYPE.validate(this);
    }

    @Override
    protected PropertyFactory<LocationType> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<LocationType> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(LOCATION_TYPE);
        }

        @Override
        public LocationType createProperty(final ParameterList parameters, final String value) {
            return new LocationType(parameters, value);
        }

        @Override
        public LocationType createProperty() {
            return new LocationType();
        }
    }

}
