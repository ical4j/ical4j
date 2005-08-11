/*
 * $Id$
 *
 * Created on 11/08/2005
 *
 * Copyright (c) 2005, Ben Fortuna
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

import java.text.ParseException;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.StringUtils;
import net.fortuna.ical4j.util.TimeZoneUtils;

/**
 * Base class for properties with a list of dates as a value.
 * @author Ben Fortuna
 */
public abstract class DateListProperty extends Property {

    private DateList dates;

    /**
     * @param name
     */
    public DateListProperty(final String name) {
        super(name);
        dates = new DateList(Value.DATE_TIME);
    }

    /**
     * @param name
     * @param parameters
     */
    public DateListProperty(final String name, final ParameterList parameters) {
        super(name, parameters);
    }

    /**
     * @param name
     * @param dates
     */
    public DateListProperty(final String name, final DateList dates) {
        super(name);
        this.dates = dates;
    }

    /**
     * @param name
     * @param dates
     */
    public DateListProperty(final String name, final ParameterList parameters, final DateList dates) {
        super(name, parameters);
        this.dates = dates;
    }

    /**
     * @return Returns the dates.
     */
    public final DateList getDates() {
        return dates;
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public void setValue(final String aValue) throws ParseException {
        dates = new DateList(aValue, (Value) getParameters().getParameter(Parameter.VALUE));
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public String getValue() {
        return StringUtils.valueOf(dates);
    }
    
    /**
     * @param vTimeZone
     */
    public final void setVTimeZone(final VTimeZone vTimeZone) {
        if (TimeZoneUtils.isUtc(vTimeZone.getTimeZone())) {
            getParameters().remove(getParameters().getParameter(Parameter.TZID));
            dates.setUtc(true);
        }
        else {
            getParameters().add(vTimeZone.getTzIdParam());
            dates.setTimeZone(vTimeZone.getTimeZone());
        }
    }
}
