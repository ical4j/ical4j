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

import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;

/**
 * Defines a PRIORITY iCalendar component property.
 *
 * @author benf
 */
public class Priority extends Property {

    public static final Priority UNDEFINED = new ImmutablePriority(0);

    public static final Priority HIGH = new ImmutablePriority(1);

    public static final Priority MEDIUM = new ImmutablePriority(5);

    public static final Priority LOW = new ImmutablePriority(9);
    
    /**
     * @author Ben Fortuna
     * An immutable instance of Priority.
     */
    private static class ImmutablePriority extends Priority {
        
        /**
         * @param value
         */
        private ImmutablePriority(final int level) {
            super(new ParameterList(true), level);
        }
        
        /* (non-Javadoc)
         * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
         */
        public final void setValue(final String aValue) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
        }
        
        /* (non-Javadoc)
         * @see net.fortuna.ical4j.model.property.Priority#setLevel(int)
         */
        public final void setLevel(final int level) {
            throw new UnsupportedOperationException("Cannot modify constant instances");
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
     * @param aList
     *            a list of parameters for this component
     * @param aValue
     *            a value string for this component
     */
    public Priority(final ParameterList aList, final String aValue) {
        super(PRIORITY, aList);
        level = Integer.parseInt(aValue);
    }

    /**
     * @param aLevel
     *            an int representation of a priority level
     */
    public Priority(final int aLevel) {
        super(PRIORITY);
        level = aLevel;
    }

    /**
     * @param aList
     *            a list of parameters for this component
     * @param aLevel
     *            an int representation of a priority level
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
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Property#setValue(java.lang.String)
     */
    public void setValue(final String aValue) {
        level = Integer.parseInt(aValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.fortuna.ical4j.model.Property#getValue()
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
}