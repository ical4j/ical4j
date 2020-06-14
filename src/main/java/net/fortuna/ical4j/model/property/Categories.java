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
import java.util.Collections;
import java.util.List;

import static net.fortuna.ical4j.model.Parameter.LANGUAGE;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a CATEGORIES iCalendar component property.
 * <pre>
 *     4.8.1.2 Categories
 *
 *        Property Name: CATEGORIES
 *
 *        Purpose: This property defines the categories for a calendar
 *        component.
 *
 *        Value Type: TEXT
 *
 *        Property Parameters: Non-standard and language property parameters
 *        can be specified on this property.
 *
 *        Conformance: The property can be specified within "VEVENT", "VTODO"
 *        or "VJOURNAL" calendar components.
 *
 *        Description: This property is used to specify categories or subtypes
 *        of the calendar component. The categories are useful in searching for
 *        a calendar component of a particular type and category. Within the
 *        "VEVENT", "VTODO" or "VJOURNAL" calendar components, more than one
 *        category can be specified as a list of categories separated by the
 *        COMMA character (US-ASCII decimal 44).
 *
 *        Format Definition: The property is defined by the following notation:
 *
 *          categories = "CATEGORIES" catparam ":" text *("," text)
 *                       CRLF
 *
 *          catparam   = *(
 *
 *                     ; the following is optional,
 *                     ; but MUST NOT occur more than once
 *
 *                     (";" languageparam ) /
 *
 *                     ; the following is optional,
 *                     ; and MAY occur more than once
 *
 *                     (";" xparam)
 *
 *                     )
 * </pre>
 *
 * @author benf
 */
public class Categories extends Property {

    private static final long serialVersionUID = -7769987073466681634L;

    private List<String> categories;

    private Validator<Property> validator = new PropertyValidator(Collections.singletonList(
            new ValidationRule(OneOrLess, LANGUAGE)));
    /**
     * Default constructor.
     */
    public Categories() {
        super(CATEGORIES, new ArrayList<>(), new Factory());
        categories = new ArrayList<>();
    }

    /**
     * @param aValue a value string for this component
     */
    public Categories(final String aValue) {
        super(CATEGORIES, new ArrayList<>(), new Factory());
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Categories(final List<Parameter> aList, final String aValue) {
        super(CATEGORIES, aList, new Factory());
        setValue(aValue);
    }

    /**
     * @param cList a list of categories
     */
    public Categories(final List<String> cList) {
        super(CATEGORIES, new ArrayList<>(), new Factory());
        categories = cList;
    }

    /**
     * @param aList a list of parameters for this component
     * @param cList a list of categories
     */
    public Categories(final List<Parameter> aList, final List<String> cList) {
        super(CATEGORIES, aList, new Factory());
        categories = cList;
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(final String aValue) {
        categories = new TextList(aValue).getTexts();
    }

    /**
     * @return Returns the categories.
     */
    public final List<String> getCategories() {
        return new ArrayList<>(categories);
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return TextList.toString(categories);
    }

    @Override
    public Property copy() {
        return new Factory().createProperty(getParameters(), getValue());
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Categories> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(CATEGORIES);
        }

        public Categories createProperty(final List<Parameter> parameters, final String value) {
            return new Categories(parameters, value);
        }

        public Categories createProperty() {
            return new Categories();
        }
    }

    @Override
    public void validate() throws ValidationException {
        validator.validate(this);
    }
}
