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

import net.fortuna.ical4j.model.Encodable;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.validate.ValidationEntry;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

/**
 * $Id$
 * 
 * Created: [15/06/2004]
 *
 * Defines an extension property.
 * @author benfortuna
 */
public class XProperty extends Property implements Encodable {

    private static final long serialVersionUID = 2331763266954894541L;

    private String value;

    /**
     * Constructs an uninitialised non-standard property.
     * @param name a non-standard property name
     */
    public XProperty(final String name) {
        super(name);
    }

    /**
     * @param aName a non-standard property name
     * @param aValue a property value
     */
    public XProperty(final String aName, final String aValue) {
        super(aName);
        setValue(aValue);
    }

    /**
     * @param aName a non-standard property name
     * @param aList a list of parameters
     * @param aValue a property value
     */
    public XProperty(final String aName, final ParameterList aList,
            final String aValue) {
        super(aName, aList);
        setValue(aValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setValue(final String aValue) {
        this.value = aValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationResult validate() throws ValidationException {
        var result = new ValidationResult();
        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
                && !getName().startsWith(EXPERIMENTAL_PREFIX)) {
            
            result.getEntries().add(new ValidationEntry(
                    "Invalid name ["
                            + getName()
                            + "]. Experimental properties must have the following prefix: "
                            + EXPERIMENTAL_PREFIX, ValidationEntry.Severity.ERROR, getName()));
        }
        return result;
    }

    @Override
    protected PropertyFactory<XProperty> newFactory() {
        throw new UnsupportedOperationException("Factory not supported for custom properties");
    }
}
