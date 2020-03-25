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
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.fortuna.ical4j.model.Parameter.ALTREP;
import static net.fortuna.ical4j.model.Parameter.LANGUAGE;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a RESOURCES iCalendar component property.
 *
 * @author benf
 */
public class Resources extends Property {

    private static final long serialVersionUID = -848562477226746807L;

    private TextList resources;

    private final Validator<Property> validator = new PropertyValidator(Arrays.asList(
            new ValidationRule(OneOrLess, ALTREP, LANGUAGE)));

    /**
     * Default constructor.
     */
    public Resources() {
        super(RESOURCES, new ArrayList<>(), new Factory());
        resources = new TextList();
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Resources(final List<Parameter> aList, final String aValue) {
        super(RESOURCES, aList, new Factory());
        setValue(aValue);
    }

    /**
     * @param rList a list of resources
     */
    public Resources(final TextList rList) {
        super(RESOURCES, new ArrayList<>(), new Factory());
        resources = rList;
    }

    /**
     * @param aList a list of parameters for this component
     * @param rList a list of resources
     */
    public Resources(final List<Parameter> aList, final TextList rList) {
        super(RESOURCES, aList, new Factory());
        resources = rList;
    }

    /**
     * @return Returns the resources.
     */
    public final TextList getResources() {
        return resources;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        resources = new TextList(aValue);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return getResources().toString();
    }

    @Override
    public void validate() throws ValidationException {
        validator.validate(this);
    }

    @Override
    public Property copy() {
        return new Factory().createProperty(getParameters(), getValue());
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Resources> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(RESOURCES);
        }

        public Resources createProperty(final List<Parameter> parameters, final String value) {
            return new Resources(parameters, value);
        }

        public Resources createProperty() {
            return new Resources();
        }
    }

}
