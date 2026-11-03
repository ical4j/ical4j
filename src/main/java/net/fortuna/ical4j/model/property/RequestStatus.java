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
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a REQUEST-STATUS iCalendar component property.
 *
 * @author benf
 */
public class RequestStatus extends Property {

    private static final long serialVersionUID = -3273944031884755345L;

    /**
     * Preliminary success status.
     */
    public static final String PRELIM_SUCCESS = "1";

    /**
     * Success status.
     */
    public static final String SUCCESS = "2";

    /**
     * Client error status.
     */
    public static final String CLIENT_ERROR = "3";

    /**
     * Scheduling error status.
     */
    public static final String SCHEDULING_ERROR = "4";

    @SuppressWarnings("NullAway.Init") // unset only via the no-arg constructor, see package-info
    private String statusCode;

    private @Nullable String description;

    private @Nullable String exData;

    /**
     * Default constructor.
     */
    public RequestStatus() {
        super(REQUEST_STATUS);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public RequestStatus(final ParameterList aList, final String aValue) {
        super(REQUEST_STATUS, aList);
        setValue(aValue);
    }

    /**
     * @param aStatusCode  a string representation of a status code
     * @param aDescription a description
     * @param data         a string representation of extension data
     */
    public RequestStatus(final String aStatusCode, final String aDescription,
                         final String data) {
        super(REQUEST_STATUS);
        statusCode = aStatusCode;
        description = aDescription;
        exData = data;
    }

    /**
     * @param aList        a list of parameters for this component
     * @param aStatusCode  a string representation of a status code
     * @param aDescription a description
     * @param data         a string representation of extension data
     */
    public RequestStatus(final ParameterList aList, final String aStatusCode,
                         final String aDescription, final String data) {
        super(REQUEST_STATUS, aList);
        statusCode = aStatusCode;
        description = aDescription;
        exData = data;
    }

    /**
     * @return Returns the description.
     */
    public final @Nullable String getDescription() {
        return description;
    }

    /**
     * @return Returns the exData.
     */
    public final @Nullable String getExData() {
        return exData;
    }

    /**
     * @return Returns the statusCode.
     */
    public final String getStatusCode() {
        return statusCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setValue(final String aValue) {
        var values = split(aValue);
        statusCode = values.get(0);
        if (values.size() > 1) {
            description = Strings.unescape(values.get(1));
        }
        if (values.size() > 2) {
            exData = Strings.unescape(values.get(2));
        }
    }

    /**
     * Splits a value into status code, description and extension data on unescaped semicolons. The description
     * and extension data are TEXT values (RFC 5545 section 3.8.8.3), so may contain escaped semicolons.
     */
    private static List<String> split(final String aValue) {
        final List<String> values = new ArrayList<>();
        var start = 0;
        for (var i = 0; i < aValue.length() && values.size() < 2; i++) {
            final var c = aValue.charAt(i);
            if (c == '\\') {
                i++;
            } else if (c == ';') {
                values.add(aValue.substring(start, i));
                start = i + 1;
            }
        }
        values.add(aValue.substring(start));
        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        final var b = new StringBuilder();

        if ((getStatusCode() != null)) {
            b.append(getStatusCode());
        }

        final var desc = getDescription();
        if (desc != null) {
            b.append(';').append(Strings.escape(desc));
        }

        final var data = getExData();
        if (data != null) {
            b.append(';').append(Strings.escape(data));
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

    @Override
    public ValidationResult validate() throws ValidationException {
        return PropertyValidator.REQUEST_STATUS.validate(this);
    }

    @Override
    protected PropertyFactory<RequestStatus> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<RequestStatus> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(REQUEST_STATUS);
        }

        @Override
        public RequestStatus createProperty(final ParameterList parameters, final String value) {
            return new RequestStatus(parameters, value);
        }

        @Override
        public RequestStatus createProperty() {
            return new RequestStatus();
        }
    }

}
