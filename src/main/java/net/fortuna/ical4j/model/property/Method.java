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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a METHOD iCalendar property.
 *
 * @author benf
 */
public class Method extends Property {

    private static final long serialVersionUID = 7220956532685378719L;

    public static final String VALUE_PUBLISH = "PUBLISH";
    public static final String VALUE_REQUEST = "REQUEST";
    public static final String VALUE_REPLY = "REPLY";
    public static final String VALUE_ADD = "ADD";
    public static final String VALUE_CANCEL = "CANCEL";
    public static final String VALUE_REFRESH = "REFRESH";
    public static final String VALUE_COUNTER = "COUNTER";
    public static final String VALUE_DECLINECOUNTER = "DECLINECOUNTER";

    /**
     * Used to publish a calendar entry to one or more Calendar Users. There is no interactivity between the publisher
     * and any other calendar user. An example might include a baseball team publishing its schedule to the public. [RFC
     * 2446]
     */
    public static final Method PUBLISH = new ImmutableMethod(VALUE_PUBLISH);

    /**
     * Used to schedule a calendar entry with other Calendar Users. Requests are interactive in that they require the
     * receiver to respond using the Reply methods. Meeting Requests, Busy Time requests and the assignment of VTODOs to
     * other Calendar Users are all examples. Requests are also used by the "Organizer" to update the status of a
     * calendar entry. [RFC 2446]
     */
    public static final Method REQUEST = new ImmutableMethod(VALUE_REQUEST);

    /**
     * A Reply is used in response to a Request to convey "Attendee" status to the "Organizer". Replies are commonly
     * used to respond to meeting and task requests. [RFC2446]
     */
    public static final Method REPLY = new ImmutableMethod(VALUE_REPLY);

    /**
     * Add one or more instances to an existing VEVENT, VTODO, or VJOURNAL. [RFC 2446]
     */
    public static final Method ADD = new ImmutableMethod(VALUE_ADD);

    /**
     * Cancel one or more instances of an existing VEVENT, VTODO, or VJOURNAL. [RFC 2446]
     */
    public static final Method CANCEL = new ImmutableMethod(VALUE_CANCEL);

    /**
     * The Refresh method is used by an "Attendee" to request the latest version of a calendar entry. [RFC 2446]
     */
    public static final Method REFRESH = new ImmutableMethod(VALUE_REFRESH);

    /**
     * The Counter method is used by an "Attendee" to negotiate a change in the calendar entry. Examples include the
     * request to change a proposed Event time or change the due date for a VTODO. [RFC 2446]
     */
    public static final Method COUNTER = new ImmutableMethod(VALUE_COUNTER);

    /**
     * Used by the "Organizer" to decline the proposed counter-proprosal. [RFC 2446]
     */
    public static final Method DECLINE_COUNTER = new ImmutableMethod(
            VALUE_DECLINECOUNTER);

    /**
     * @author Ben Fortuna An immutable instance of Method.
     */
    private static final class ImmutableMethod extends Method {

        private static final long serialVersionUID = 5332607957381969713L;

        private ImmutableMethod(final String value) {
            super(new ParameterList(true), value);
        }

        @Override
        public void setValue(final String aValue) {
            throw new UnsupportedOperationException(
                    "Cannot modify constant instances");
        }
    }

    private String value;

    /**
     * Default constructor.
     */
    public Method() {
        super(METHOD, new Factory());
    }

    /**
     * @param aValue a value string for this component
     */
    public Method(final String aValue) {
        super(METHOD, new Factory());
        this.value = aValue;
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Method(final ParameterList aList, final String aValue) {
        super(METHOD, aList, new Factory());
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final String aValue) {
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return value;
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return PropertyValidator.METHOD.validate(this);
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Method> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(METHOD);
        }

        @Override
        public Method createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {

            if (parameters.isEmpty()) {
                switch (value) {
                    case VALUE_PUBLISH: return PUBLISH;
                    case VALUE_REQUEST: return REQUEST;
                    case VALUE_REPLY: return REPLY;
                    case VALUE_ADD: return ADD;
                    case VALUE_CANCEL: return CANCEL;
                    case VALUE_REFRESH: return REFRESH;
                    case VALUE_COUNTER: return COUNTER;
                    case VALUE_DECLINECOUNTER: return DECLINE_COUNTER;
                }
            }
            return  new Method(parameters, value);
        }

        @Override
        public Method createProperty() {
            return new Method();
        }
    }

}
