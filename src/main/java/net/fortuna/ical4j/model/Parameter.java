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
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.util.Strings;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.net.URISyntaxException;
import java.util.Comparator;

/**
 * Defines an iCalendar parameter. Subclasses of this class provide additional validation and typed values for specific
 * iCalendar parameters.
 * <p/>
 * Note that subclasses must provide a reference to the factory used to create the
 * parameter to support parameter cloning (copy). If no factory is specified an
 * {@link UnsupportedOperationException} will be thrown by the {@link #copy()} method.
 *
 * @author Ben Fortuna
 *         <p/>
 *         $Id$ [Apr 5, 2004]
 */
public abstract class Parameter extends Content implements Comparable<Parameter> {

    private static final long serialVersionUID = -2058497904769713528L;

    /**
     * Region abbreviation.
     */
    public static final String ABBREV = "ABBREV";

    /**
     * Alternate text representation.
     */
    public static final String ALTREP = "ALTREP";

    /**
     * Common name.
     */
    public static final String CN = "CN";

    /**
     * Calendar user type.
     */
    public static final String CUTYPE = "CUTYPE";

    /**
     * Delegator.
     */
    public static final String DELEGATED_FROM = "DELEGATED-FROM";

    /**
     * Delegatee.
     */
    public static final String DELEGATED_TO = "DELEGATED-TO";

    /**
     * Derived.
     */
    public static final String DERIVED = "DERIVED";

    /**
     * Directory entry.
     */
    public static final String DIR = "DIR";

    /**
     * Display format.
     */
    public static final String DISPLAY = "DISPLAY";
    
    /**
     * Email address.
     */
    public static final String EMAIL = "EMAIL";
    
    /**
     * Inline encoding.
     */
    public static final String ENCODING = "ENCODING";

    /**
     * Feature specification.
     */
    public static final String FEATURE = "FEATURE";

    /**
     * Format type.
     */
    public static final String FMTTYPE = "FMTTYPE";

    /**
     * Free/busy time type.
     */
    public static final String FBTYPE = "FBTYPE";

    /**
     * Label.
     */
    public static final String LABEL = "LABEL";

    /**
     * Language for text.
     */
    public static final String LANGUAGE = "LANGUAGE";

    /**
     * Group or list membership.
     */
    public static final String MEMBER = "MEMBER";

    /**
     * Order.
     */
    public static final String ORDER = "ORDER";

    /**
     * Participation status.
     */
    public static final String PARTSTAT = "PARTSTAT";

    /**
     * Recurrence identifier range.
     */
    public static final String RANGE = "RANGE";

    /**
     * Alarm trigger relationship.
     */
    public static final String RELATED = "RELATED";

    /**
     * Relationship type.
     */
    public static final String RELTYPE = "RELTYPE";

    /**
     * Participation role.
     */
    public static final String ROLE = "ROLE";

    /**
     * RSVP expectation.
     */
    public static final String RSVP = "RSVP";

    /**
     * Schedule agent.
     */
    public static final String SCHEDULE_AGENT = "SCHEDULE-AGENT";

    /**
     * Schedule status.
     */
    public static final String SCHEDULE_STATUS = "SCHEDULE-STATUS";

    /**
     * Schema for structured data.
     */
    public static final String SCHEMA = "SCHEMA";

    /**
     * Sent by.
     */
    public static final String SENT_BY = "SENT-BY";

    /**
     * Type.
     */
    public static final String TYPE = "TYPE";

    /**
     * Reference to time zone object.
     */
    public static final String TZID = "TZID";

    /**
     * Property value data type.
     */
    public static final String VALUE = "VALUE";

    /**
     * Reference to vvenue component.
     */
    public static final String VVENUE = "VVENUE";

    /**
     * Prefix to all experimental parameters.
     */
    public static final String EXPERIMENTAL_PREFIX = "X-";

    private String name;

    private final ParameterFactory factory;

    /**
     * @param aName   the parameter identifier
     * @param factory the factory used to create the parameter
     */
    public Parameter(final String aName, ParameterFactory factory) {
        this.name = aName;
        this.factory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getName());
        b.append('=');
        String value;
        if (this instanceof Encodable) {
            try {
                value = ParameterCodec.INSTANCE.encode(getValue());
            } catch (EncoderException e) {
                value = getValue();
            }
        } else {
            value = getValue();
        }
        b.append(Strings.valueOf(value));
        return b.toString();
    }

    /**
     * @return Returns the name.
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof Parameter) {
            final Parameter p = (Parameter) arg0;
            return new EqualsBuilder().append(getName(), p.getName())
                    .append(getValue(), p.getValue()).isEquals();
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        // as parameter name is case-insensitive generate hash for uppercase..
        return new HashCodeBuilder().append(getName().toUpperCase()).append(
                getValue()).toHashCode();
    }

    /**
     * Deep copy of parameter.
     *
     * @return new parameter
     * @throws URISyntaxException where an invalid URI is encountered
     */
    public <T extends Parameter> T copy() throws URISyntaxException {
        if (factory == null) {
            throw new UnsupportedOperationException("No factory specified");
        }
        return (T) factory.createParameter(getValue());
    }

    @Override
    public int compareTo(Parameter o) {
        if (this.equals(o)) {
            return 0;
        }
        return Comparator.comparing(Parameter::getName)
                .thenComparing(Parameter::getValue)
                .compare(this, o);
    }
}
