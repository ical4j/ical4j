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
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;

/**
 * Defines a GEO iCalendar component property. NOTE: The simple formula for
 * converting degrees-minutes-seconds into decimal degrees is:
 *
 * decimal = degrees + minutes/60 + seconds/3600.
 *
 * @author benf
 */
public class Geo extends Property {
    
    private static final long serialVersionUID = -902100715801867636L;

    private float lattitude;

    private float longitude;

    /**
     * Default constructor.
     */
    public Geo() {
        super(GEO);
        lattitude = 0;
        longitude = 0;
    }
    
    /**
     * @param aList
     *            a list of parameters for this component
     * @param aValue
     *            a value string for this component
     */
    public Geo(final ParameterList aList, final String aValue) {
        super(GEO, aList);
        setValue(aValue);
    }

    /**
     * @param aLattitude
     *            a lattitudinal value
     * @param aLongitude
     *            a longitudinal value
     */
    public Geo(final float aLattitude, final float aLongitude) {
        super(GEO);
        lattitude = aLattitude;
        longitude = aLongitude;
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param aLattitude
     *            a lattitudinal value
     * @param aLongitude
     *            a longitudinal value
     */
    public Geo(final ParameterList aList, final float aLattitude,
            final float aLongitude) {
        super(GEO, aList);
        lattitude = aLattitude;
        longitude = aLongitude;
    }

    /**
     * @return Returns the lattitude.
     */
    public final float getLattitude() {
        return lattitude;
    }

    /**
     * @return Returns the longitude.
     */
    public final float getLongitude() {
        return longitude;
    }
    
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public final void setValue(final String aValue) {
        lattitude = Float.parseFloat(aValue.substring(0, aValue.indexOf(';') - 1));
        longitude =  Float.parseFloat(aValue.substring(aValue.indexOf(';')));
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public final String getValue() {
        return String.valueOf(getLattitude()) + ";"
                + String.valueOf(getLongitude());
    }
    
    /**
     * @param lattitude The lattitude to set.
     */
    public final void setLattitude(final float lattitude) {
        this.lattitude = lattitude;
    }
    
    /**
     * @param longitude The longitude to set.
     */
    public final void setLongitude(final float longitude) {
        this.longitude = longitude;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#validate()
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
