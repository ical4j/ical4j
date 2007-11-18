/*
 * $Id$
 *
 * Created on 9/07/2005
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.ParameterValidator;
import net.fortuna.ical4j.util.Strings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for properties with a DATE or DATE-TIME value. Note that some sub-classes may only allow either a DATE or
 * a DATE-TIME value, for which additional rules/validation should be specified.
 * @author Ben Fortuna
 */
public abstract class DateProperty extends Property {

    private Log log = LogFactory.getLog(DateProperty.class);
    
    private Date date;

    protected TimeZone timezone;

    /**
     * @param aName
     * @param aList
     */
    public DateProperty(final String name, final ParameterList parameters) {
        super(name, parameters);
    }

    /**
     * @param aName
     */
    public DateProperty(final String name) {
        super(name);
    }

    /**
     * Creates a new instance of the named property with an initial timezone.
     * @param name property name
     * @param timezone initial timezone
     */
    public DateProperty(final String name, TimeZone timezone) {
        super(name);
        updateTimeZone(timezone);
    }

    /**
     * @return Returns the date.
     */
    public final Date getDate() {
        return date;
    }

    /**
     * Sets the date value of this property. The timezone and value of this
     * instance will also be updated accordingly.
     * @param date The date to set.
     */
    public final void setDate(final Date date) {
        this.date = date;
        if (date instanceof DateTime) {
            if (getParameter(Parameter.VALUE) != null) {
                getParameters().replace(Value.DATE_TIME);
            }
            updateTimeZone(((DateTime) date).getTimeZone());
        }
        else {
            if (date != null) {
                getParameters().replace(Value.DATE);
            }
            /*
            else {
                getParameters().removeAll(Parameter.VALUE);
            }
            */
            // ensure timezone is null for VALUE=DATE or null properties..
            updateTimeZone(null);
        }
    }

    /**
     * Default setValue() implementation. Allows for either DATE or DATE-TIME values.
     */
    public void setValue(final String value) throws ParseException {
        // value can be either a date-time or a date..
        if (Value.DATE.equals(getParameter(Parameter.VALUE))) {
            // ensure timezone is null for VALUE=DATE properties..
            updateTimeZone(null);
            this.date = new Date(value);
        }
        else {
            this.date = new DateTime(value, timezone);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public String getValue() {
        return Strings.valueOf(getDate());
    }

    /**
     * Publically available method to update the current timezone.
     * @param timezone
     */
    public void setTimeZone(final TimeZone timezone) {
        updateTimeZone(timezone);
    }
    
    /**
     * Updates the timezone associated with the property's value. If the specified timezone is equivalent to UTC any
     * existing TZID parameters will be removed. Note that this method is only applicable where the current date is an
     * instance of <code>DateTime</code>. For all other cases an <code>UnsupportedOperationException</code> will be
     * thrown.
     * @param vTimeZone
     */
    private void updateTimeZone(final TimeZone timezone) {
        this.timezone = timezone;
        if (timezone != null) {
            if (getDate() != null && !(getDate() instanceof DateTime)) {
                throw new UnsupportedOperationException(
                        "TimeZone is not applicable to current value");
            }
            if (getDate() != null) {
                ((DateTime) getDate()).setTimeZone(timezone);
            }

            TzId tzId = new TzId(timezone.getID());
            
            if (log.isTraceEnabled()) {
                log.trace("Replacing current timezone ["
                        + getParameter(Parameter.TZID) + "] with [" + tzId + "]");
            }
            
            getParameters().replace(tzId);
        }
        else {
            if (log.isTraceEnabled()) {
                log.trace("Clearing timezone [" + getParameter(Parameter.TZID) + "]");
            }
            
            // use setUtc() to reset timezone..
            setUtc(isUtc());
        }
    }

    /**
     * Resets the VTIMEZONE associated with the property. If utc is true, any TZID parameters are removed and the Java
     * timezone is updated to UTC time. If utc is false, TZID parameters are removed and the Java timezone is set to the
     * default timezone (i.e. represents a "floating" local time)
     * @param utc
     */
    public final void setUtc(final boolean utc) {
        if (getDate() != null && (getDate() instanceof DateTime)) {
            ((DateTime) getDate()).setUtc(utc);
        }
        getParameters().remove(getParameter(Parameter.TZID));
    }

    /**
     * Indicates whether the current date value is specified in UTC time.
     * @return
     */
    public final boolean isUtc() {
        if (getDate() instanceof DateTime) {
            return ((DateTime) getDate()).isUtc();
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#validate()
     */
    public void validate() throws ValidationException {

        ParameterValidator.getInstance().assertOneOrLess(Parameter.VALUE,
                getParameters());

        if (isUtc()) {
            ParameterValidator.getInstance().assertNone(Parameter.TZID,
                    getParameters());
        }
        else {
            ParameterValidator.getInstance().assertOneOrLess(Parameter.TZID,
                    getParameters());
        }

        Value value = (Value) getParameter(Parameter.VALUE);

        if (getDate() instanceof DateTime) {

            if (value != null && !Value.DATE_TIME.equals(value)) {
                throw new ValidationException("VALUE parameter [" + value
                        + "] is invalid for DATE-TIME instance");
            }

            DateTime dateTime = (DateTime) date;

            // ensure tzid matches date-time timezone..
            Parameter tzId = getParameter(Parameter.TZID);
            if (dateTime.getTimeZone() != null
                    && (tzId == null || !tzId.getValue().equals(
                            dateTime.getTimeZone().getID()))) {

                throw new ValidationException("TZID parameter [" + tzId
                        + "] does not match the timezone ["
                        + dateTime.getTimeZone().getID() + "]");
            }
        }
        else if (getDate() != null) {

            if (value == null) {
                throw new ValidationException("VALUE parameter [" + Value.DATE
                        + "] must be specified for DATE instance");
            }
            else if (!Value.DATE.equals(value)) {
                throw new ValidationException("VALUE parameter [" + value
                        + "] is invalid for DATE instance");
            }
        }
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#copy()
     */
    public Property copy() throws IOException, URISyntaxException, ParseException {
        Property copy = super.copy();
        
       ((DateProperty) copy).timezone = timezone;
       ((DateProperty) copy).setValue(getValue());
       
        return copy;
    }
}
