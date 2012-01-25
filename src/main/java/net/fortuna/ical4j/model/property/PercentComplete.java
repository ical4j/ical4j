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
 * Defines a PERCENT-COMPLETE iCalendar component property.
 * @author benf
 */
public class PercentComplete extends Property {

    private static final long serialVersionUID = 7788138484983240112L;

    private int percentage;

    /**
     * Default constructor.
     */
    public PercentComplete() {
        super(PERCENT_COMPLETE, PropertyFactoryImpl.getInstance());
    }

    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public PercentComplete(final ParameterList aList, final String aValue) {
        super(PERCENT_COMPLETE, aList, PropertyFactoryImpl.getInstance());
        setValue(aValue);
    }

    /**
     * @param aPercentage an int representation of a percentage
     */
    public PercentComplete(final int aPercentage) {
        super(PERCENT_COMPLETE, PropertyFactoryImpl.getInstance());
        percentage = aPercentage;
    }

    /**
     * @param aList a list of parameters for this component
     * @param aPercentage an int representation of a percentage
     */
    public PercentComplete(final ParameterList aList, final int aPercentage) {
        super(PERCENT_COMPLETE, aList, PropertyFactoryImpl.getInstance());
        percentage = aPercentage;
    }

    /**
     * @return Returns the percentage.
     */
    public final int getPercentage() {
        return percentage;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        percentage = Integer.parseInt(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return String.valueOf(getPercentage());
    }

    /**
     * @param percentage The percentage to set.
     */
    public final void setPercentage(final int percentage) {
        this.percentage = percentage;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate() throws ValidationException {
        if (percentage < 0 || percentage > 100) {
            throw new ValidationException(getName() + " with invalid value: " + percentage);
        }
    }
}
