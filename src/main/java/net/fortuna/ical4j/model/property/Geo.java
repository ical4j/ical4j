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

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.ValidationException;

/**
 * $Id$
 *
 * Created: [Apr 6, 2004]
 *
 * Defines a GEO iCalendar component property.
 * 
 * <pre>
 *      4.8.1.6 Geographic Position
 *      
 *         Property Name: GEO
 *      
 *         Purpose: This property specifies information related to the global
 *         position for the activity specified by a calendar component.
 *      
 *         Value Type: FLOAT. The value MUST be two SEMICOLON separated FLOAT
 *         values.
 *      
 *         Property Parameters: Non-standard property parameters can be
 *         specified on this property.
 *      
 *         Conformance: This property can be specified in  &quot;VEVENT&quot; or &quot;VTODO&quot;
 *         calendar components.
 *      
 *         Description: The property value specifies latitude and longitude, in
 *         that order (i.e., &quot;LAT LON&quot; ordering). The longitude represents the
 *         location east or west of the prime meridian as a positive or negative
 *         real number, respectively. The longitude and latitude values MAY be
 *         specified up to six decimal places, which will allow for accuracy to
 *         within one meter of geographical position. Receiving applications
 *         MUST accept values of this precision and MAY truncate values of
 *         greater precision.
 *      
 *         Values for latitude and longitude shall be expressed as decimal
 *         fractions of degrees. Whole degrees of latitude shall be represented
 *         by a two-digit decimal number ranging from 0 through 90. Whole
 *         degrees of longitude shall be represented by a decimal number ranging
 *         from 0 through 180. When a decimal fraction of a degree is specified,
 *         it shall be separated from the whole number of degrees by a decimal
 *         point.
 *      
 *         Latitudes north of the equator shall be specified by a plus sign (+),
 *         or by the absence of a minus sign (-), preceding the digits
 *         designating degrees. Latitudes south of the Equator shall be
 *         designated by a minus sign (-) preceding the digits designating
 *         degrees. A point on the Equator shall be assigned to the Northern
 *         Hemisphere.
 *      
 *         Longitudes east of the prime meridian shall be specified by a plus
 *         sign (+), or by the absence of a minus sign (-), preceding the digits
 *         designating degrees. Longitudes west of the meridian shall be
 *         designated by minus sign (-) preceding the digits designating
 *         degrees. A point on the prime meridian shall be assigned to the
 *         Eastern Hemisphere. A point on the 180th meridian shall be assigned
 *         to the Western Hemisphere. One exception to this last convention is
 *         permitted. For the special condition of describing a band of latitude
 *         around the earth, the East Bounding Coordinate data element shall be
 *         assigned the value +180 (180) degrees.
 *      
 *         Any spatial address with a latitude of +90 (90) or -90 degrees will
 *         specify the position at the North or South Pole, respectively. The
 *         component for longitude may have any legal value.
 *      
 *         With the exception of the special condition described above, this
 *         form is specified in Department of Commerce, 1986, Representation of
 *         geographic point locations for information interchange (Federal
 *         Information Processing Standard 70-1):  Washington,  Department of
 *         Commerce, National Institute of Standards and Technology.
 *      
 *         The simple formula for converting degrees-minutes-seconds into
 *         decimal degrees is:
 *      
 *           decimal = degrees + minutes/60 + seconds/3600.
 *      
 *         Format Definition: The property is defined by the following notation:
 *      
 *           geo        = &quot;GEO&quot; geoparam &quot;:&quot; geovalue CRLF
 *      
 *           geoparam   = *(&quot;;&quot; xparam)
 *      
 *           geovalue   = float &quot;;&quot; float
 *           ;Latitude and Longitude components
 *      
 *         Example: The following is an example of this property:
 *      
 *           GEO:37.386013;-122.082932
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Geo extends Property {

    private static final long serialVersionUID = -902100715801867636L;

    private BigDecimal latitude;

    private BigDecimal longitude;

    /**
     * Default constructor.
     */
    public Geo() {
        super(GEO, PropertyFactoryImpl.getInstance());
        latitude = BigDecimal.valueOf(0);
        longitude = BigDecimal.valueOf(0);
    }

    /**
     * Creates a new instance by parsing the specified string representation.
     * @param value a geo value
     */
    public Geo(final String value) {
        super(GEO, PropertyFactoryImpl.getInstance());
        setValue(value);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Geo(final ParameterList aList, final String aValue) {
        super(GEO, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param latitude a latitudinal value
     * @param longitude a longitudinal value
     */
    public Geo(final BigDecimal latitude, final BigDecimal longitude) {
        super(GEO, PropertyFactoryImpl.getInstance());
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @param aList a list of parameters for this component
     * @param latitude a latitudinal value
     * @param longitude a longitudinal value
     */
    public Geo(final ParameterList aList, final BigDecimal latitude,
            final BigDecimal longitude) {
        super(GEO, aList, PropertyFactoryImpl.getInstance());
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return Returns the latitude.
     */
    public final BigDecimal getLatitude() {
        return latitude;
    }

    /**
     * @return Returns the longitude.
     */
    public final BigDecimal getLongitude() {
        return longitude;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        final String latitudeString = aValue.substring(0, aValue.indexOf(';'));
        if (StringUtils.isNotBlank(latitudeString)) {
            latitude = new BigDecimal(latitudeString);
        }
        else {
            latitude = BigDecimal.valueOf(0);
        }
        
        final String longitudeString = aValue.substring(aValue.indexOf(';') + 1);
        if (StringUtils.isNotBlank(longitudeString)) {
            longitude = new BigDecimal(longitudeString);
        }
        else {
            longitude = BigDecimal.valueOf(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return String.valueOf(getLatitude()) + ";"
                + String.valueOf(getLongitude());
    }

    /**
     * @param latitude The latitude to set.
     */
    public final void setLatitude(final BigDecimal latitude) {
        this.latitude = latitude;
    }

    /**
     * @param longitude The longitude to set.
     */
    public final void setLongitude(final BigDecimal longitude) {
        this.longitude = longitude;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
