/*
 * $Id$ [Apr 5, 2004]
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

/**
 * Defines an iCalendar parameter. Subclasses of this class provide additional
 * validation and typed values for specific iCalendar parameters.
 *
 * @author Ben Fortuna
 */
public abstract class Parameter extends Content {

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
     * Directory entry.
     */
    public static final String DIR = "DIR";

    /**
     * Inline encoding.
     */
    public static final String ENCODING = "ENCODING";

    /**
     * Format type.
     */
    public static final String FMTTYPE = "FMTTYPE";

    /**
     * Free/busy time type.
     */
    public static final String FBTYPE = "FBTYPE";

    /**
     * Language for text.
     */
    public static final String LANGUAGE = "LANGUAGE";

    /**
     * Group or list membership.
     */
    public static final String MEMBER = "MEMBER";

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
     * Sent by.
     */
    public static final String SENT_BY = "SENT-BY";

    /**
     * Reference to time zone object.
     */
    public static final String TZID = "TZID";

    /**
     * Property value data type.
     */
    public static final String VALUE = "VALUE";

    /**
     * Prefix to all experimental parameters.
     */
    public static final String EXPERIMENTAL_PREFIX = "X-";

    private String name;

    /**
     * Constructor.
     * @param aName name of this parameter
     */
    public Parameter(final String aName) {
        this.name = aName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer();
        b.append(getName());
        b.append('=');

        if (this instanceof Escapable) {
            b.append(Strings.escape(Strings.valueOf(getValue())));
        }
        else {
            b.append(Strings.valueOf(getValue()));
        }

        return b.toString();
    }

    /**
     * @return Returns the name.
     */
    public final String getName() {
        return name;
    }

    /**
     * @return Returns the value.
     */
    public abstract String getValue();

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof Parameter) {
            Parameter p = (Parameter) arg0;
            return getName().equals(p.getName()) && getValue().equals(p.getValue());
        }
        return super.equals(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        // as parameter name is case-insensitive generate hash for uppercase..
        return getName().toUpperCase().hashCode() + getValue().hashCode();
    }
}
