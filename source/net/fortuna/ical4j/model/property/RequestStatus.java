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
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
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

import java.util.StringTokenizer;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.ParameterValidator;

/**
 * Defines a REQUEST-STATUS iCalendar component property.
 *
 * @author benf
 */
public class RequestStatus extends Property {

    private static final String PRELIM_SUCCESS = "1";

    private static final String SUCCESS = "2";

    private static final String CLIENT_ERROR = "3";

    private static final String SCHEDULING_ERROR = "4";

    private String statusCode;

    private String description;

    private String exData;

    /**
     * Default constructor.
     */
    public RequestStatus() {
        super(REQUEST_STATUS);
    }
    
    /**
     * @param aList
     *            a list of parameters for this component
     * @param aValue
     *            a value string for this component
     */
    public RequestStatus(final ParameterList aList, final String aValue) {
        super(REQUEST_STATUS, aList);
        setValue(aValue);
    }

    /**
     * @param aStatusCode
     *            a string representation of a status code
     * @param aDescription
     *            a description
     * @param data
     *            a string representation of extension data
     */
    public RequestStatus(final String aStatusCode, final String aDescription, final String data) {
        super(REQUEST_STATUS);
        statusCode = aStatusCode;
        description = aDescription;
        exData = data;
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param aStatusCode
     *            a string representation of a status code
     * @param aDescription
     *            a description
     * @param data
     *            a string representation of extension data
     */
    public RequestStatus(final ParameterList aList, final String aStatusCode,
            final String aDescription, final String data) {
        super(REQUEST_STATUS, aList);
        statusCode = aStatusCode;
        description = aDescription;
        exData = data;
    }

    /**
     * @see net.fortuna.ical4j.model.Property#validate()
     */
    public final void validate() throws ValidationException {

        /*
         * ; the following is optional, ; but MUST NOT occur more than once
         *
         * (";" languageparm) /
         */
        ParameterValidator.getInstance().validateOneOrLess(Parameter.LANGUAGE,
                getParameters());

        /*
         * ; the following is optional, ; and MAY occur more than once
         *
         * (";" xparam)
         */
    }

    /**
     * @return Returns the description.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @return Returns the exData.
     */
    public final String getExData() {
        return exData;
    }

    /**
     * @return Returns the statusCode.
     */
    public final String getStatusCode() {
        return statusCode;
    }
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public final void setValue(final String aValue) {
        StringTokenizer t = new StringTokenizer(aValue, ";");

        if (t.hasMoreTokens()) {
            statusCode = t.nextToken();
        }

        if (t.hasMoreTokens()) {
            description = t.nextToken();
        }

        if (t.hasMoreTokens()) {
            exData = t.nextToken();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Property#getValue()
     */
    public final String getValue() {
        StringBuffer b = new StringBuffer();

        if ((getStatusCode() != null)) {
            b.append(getStatusCode());
            b.append(';');
        }

        if ((getDescription() != null)) {
            b.append(getDescription());
            b.append(';');
        }

        if ((getExData() != null)) {
            b.append(getExData());
        }

        return b.toString();
    }
    
    /**
     * @param description The description to set.
     */
    public final void setDescription(final String description) {
        this.description = description;
    }
    
    /**
     * @param exData The exData to set.
     */
    public final void setExData(final String exData) {
        this.exData = exData;
    }
    
    /**
     * @param statusCode The statusCode to set.
     */
    public final void setStatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }
}