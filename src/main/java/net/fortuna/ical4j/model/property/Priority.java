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

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a PRIORITY iCalendar component property.
 * <p/>
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

    public static final int VALUE_UNDEFINED = 0;
    public static final int VALUE_HIGH = 1;
    public static final int VALUE_MEDIUM = 5;
    public static final int VALUE_LOW = 9;

    /**
     * Undefined priority.
     */
    public static final Priority UNDEFINED = new ImmutablePriority(VALUE_UNDEFINED);

    /**
     * High priority.
     */
    public static final Priority HIGH = new ImmutablePriority(VALUE_HIGH);

    /**
     * Medium priority.
     */
    public static final Priority MEDIUM = new ImmutablePriority(VALUE_MEDIUM);

    /**
     * Low priority.
     */
    public static final Priority LOW = new ImmutablePriority(VALUE_LOW);

    /**
     * @author Ben Fortuna An immutable instance of Priority.
     */
    private static final class ImmutablePriority extends Priority implements ImmutableContent {

        private static final long serialVersionUID = 5884973714694108418L;

        private ImmutablePriority(final int level) {
            super(level);
        }

        @Override
        public void setValue(final String aValue) {
            throwException();
        }

        @Override
        public void setLevel(final int level) {
            throwException();
        }

        @Override
        public ImmutablePriority add(Parameter parameter) {
            throwException();
            return null;
        }

        @Override
        public ImmutablePriority remove(Parameter parameter) {
            throwException();
            return null;
        }

        @Override
        public ImmutablePriority removeAll(String... parameterName) {
            throwException();
            return null;
        }

        @Override
        public ImmutablePriority replace(Parameter parameter) {
            throwException();
            return null;
        }
    }

    private int level;

    /**
     * Default constructor.
     */
    public Priority() {
        super(PRIORITY);
        level = UNDEFINED.getLevel();
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Priority(final ParameterList aList, final String aValue) {
        super(PRIORITY, aList);
        try {
            level = Integer.parseInt(aValue);
        } catch (NumberFormatException e) {
            if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
                level = UNDEFINED.level;
            } else {
                throw e;
            }
        }
    }

    /**
     * @param aLevel an int representation of a priority level
     */
    public Priority(final int aLevel) {
        super(PRIORITY);
        level = aLevel;
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aLevel an int representation of a priority level
     */
    public Priority(final ParameterList aList, final int aLevel) {
        super(PRIORITY, aList);
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
    @Override
    public void setValue(final String aValue) {
        level = Integer.parseInt(aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return String.valueOf(getLevel());
    }

    /**
     * @param level The level to set.
     */
    public void setLevel(final int level) {
        this.level = level;
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return PropertyValidator.PRIORITY.validate(this);
    }

    @Override
    protected PropertyFactory<Priority> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Priority> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(PRIORITY);
        }

        @Override
        public Priority createProperty(final ParameterList parameters, final String value) {

            if (parameters.getAll().isEmpty()) {
                int level = Integer.parseInt(value);
                switch (level) {
                    case VALUE_UNDEFINED: return UNDEFINED;
                    case VALUE_HIGH: return HIGH;
                    case VALUE_MEDIUM: return MEDIUM;
                    case VALUE_LOW: return LOW;
                }
            }
            return new Priority(parameters, value);
        }

        @Override
        public Priority createProperty() {
            return new Priority();
        }
    }

}
