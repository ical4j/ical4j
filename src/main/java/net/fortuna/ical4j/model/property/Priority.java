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

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.ValidationException;

/**
 * $Id$
 * 
 * Created: [Apr 6, 2004]
 *
 * Defines a PRIORITY iCalendar component property.
 * 
 * <pre>
 *     4.8.1.9 Priority
 *     
 *        Property Name: PRIORITY
 *     
 *        Purpose: The property defines the relative priority for a calendar
 *        component.
 *     
 *        Value Type: INTEGER
 *     
 *        Property Parameters: Non-standard property parameters can be
 *        specified on this property.
 *     
 *        Conformance: The property can be specified in a &quot;VEVENT&quot; or &quot;VTODO&quot;
 *        calendar component.
 *     
 *        Description: The priority is specified as an integer in the range
 *        zero to nine. A value of zero (US-ASCII decimal 48) specifies an
 *        undefined priority. A value of one (US-ASCII decimal 49) is the
 *        highest priority. A value of two (US-ASCII decimal 50) is the second
 *        highest priority. Subsequent numbers specify a decreasing ordinal
 *        priority. A value of nine (US-ASCII decimal 58) is the lowest
 *        priority.
 *     
 *        A CUA with a three-level priority scheme of &quot;HIGH&quot;, &quot;MEDIUM&quot; and
 *        &quot;LOW&quot; is mapped into this property such that a property value in the
 *        range of one (US-ASCII decimal 49) to four (US-ASCII decimal 52)
 *        specifies &quot;HIGH&quot; priority. A value of five (US-ASCII decimal 53) is
 *        the normal or &quot;MEDIUM&quot; priority. A value in the range of six (US-
 *        ASCII decimal 54) to nine (US-ASCII decimal 58) is &quot;LOW&quot; priority.
 *     
 *        A CUA with a priority schema of &quot;A1&quot;, &quot;A2&quot;, &quot;A3&quot;,
 *         &quot;B1&quot;, &quot;B2&quot;, ...,
 *        &quot;C3&quot; is mapped into this property such that a property value of one
 *        (US-ASCII decimal 49) specifies &quot;A1&quot;, a property value of two (US-
 *        ASCII decimal 50) specifies &quot;A2&quot;, a property value of three (US-ASCII
 *        decimal 51) specifies &quot;A3&quot;, and so forth up to a property value of 9
 *        (US-ASCII decimal 58) specifies &quot;C3&quot;.
 *     
 *        Other integer values are reserved for future use.
 *     
 *        Within a &quot;VEVENT&quot; calendar component, this property specifies a
 *        priority for the event. This property may be useful when more than
 *        one event is scheduled for a given time period.
 *     
 *        Within a &quot;VTODO&quot; calendar component, this property specified a
 *        priority for the to-do. This property is useful in prioritizing
 *        multiple action items for a given time period.
 *     
 *        Format Definition: The property is specified by the following
 *        notation:
 *     
 *          priority   = &quot;PRIORITY&quot; prioparam &quot;:&quot; privalue CRLF
 *          ;Default is zero
 *     
 *          prioparam  = *(&quot;;&quot; xparam)
 *     
 *          privalue   = integer       ;Must be in the range [0..9]
 *             ; All other values are reserved for future use
 *     
 *        The following is an example of a property with the highest priority:
 *     
 *          PRIORITY:1
 *     
 *        The following is an example of a property with a next highest
 *        priority:
 *     
 *          PRIORITY:2
 *     
 *        Example: The following is an example of a property with no priority.
 *        This is equivalent to not specifying the &quot;PRIORITY&quot; property:
 *     
 *          PRIORITY:0
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class Priority extends Property {

    private static final long serialVersionUID = -5654367843953827397L;

    /**
     * Undefined priority.
     */
    public static final Priority UNDEFINED = new ImmutablePriority(0);

    /**
     * High priority.
     */
    public static final Priority HIGH = new ImmutablePriority(1);

    /**
     * Medium priority.
     */
    public static final Priority MEDIUM = new ImmutablePriority(5);

    /**
     * Low priority.
     */
    public static final Priority LOW = new ImmutablePriority(9);

    /**
     * @author Ben Fortuna An immutable instance of Priority.
     */
    private static final class ImmutablePriority extends Priority {

        private static final long serialVersionUID = 5884973714694108418L;

        private ImmutablePriority(final int level) {
            super(new ParameterList(true), level);
        }

        public void setValue(final String aValue) {
            throw new UnsupportedOperationException(
                    "Cannot modify constant instances");
        }

        public void setLevel(final int level) {
            throw new UnsupportedOperationException(
                    "Cannot modify constant instances");
        }
    }

    private int level;

    /**
     * Default constructor.
     */
    public Priority() {
        super(PRIORITY, PropertyFactoryImpl.getInstance());
        level = UNDEFINED.getLevel();
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Priority(final ParameterList aList, final String aValue) {
        super(PRIORITY, aList, PropertyFactoryImpl.getInstance());
        level = Integer.parseInt(aValue);
    }

    /**
     * @param aLevel an int representation of a priority level
     */
    public Priority(final int aLevel) {
        super(PRIORITY, PropertyFactoryImpl.getInstance());
        level = aLevel;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aLevel an int representation of a priority level
     */
    public Priority(final ParameterList aList, final int aLevel) {
        super(PRIORITY, aList, PropertyFactoryImpl.getInstance());
        level = aLevel;
    }

    /**
     * @return Returns the level.
     */
    public final int getLevel() {
        return level;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(final String aValue) {
        level = Integer.parseInt(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return String.valueOf(getLevel());
    }

    /**
     * @param level The level to set.
     */
    public void setLevel(final int level) {
        this.level = level;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        // TODO: Auto-generated method stub
    }
}
