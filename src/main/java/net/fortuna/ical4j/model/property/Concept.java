/*
 *  Copyright (c) 2022, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * <pre>
 *     Purpose:
 *     This property defines the formal categories for a calendar component.
 * Value type:
 *     URI
 * Property Parameters:
 *     IANA and non-standard parameters can be specified on this property.
 * Conformance:
 *     This property can be specified zero or more times in any iCalendar component.
 * Description:
 *
 *     This property is used to specify formal categories or classifications of the calendar component. The values are useful in searching for a calendar component of a particular type and category.
 * This categorization is distinct from the more informal "tagging" of components provided by the existing CATEGORIES property. It is expected that the value of the CONCEPT property will reference an external resource that provides information about the categorization.In addition, a structured URI value allows for hierarchical categorization of events.Possible category resources are the various proprietary systems, for example, the Library of Congress, or an open source of categorization data.
 * Format Definition:
 *
 *     This property is defined by the following notation:
 *
 *   concept        =  "CONCEPT" conceptparam ":"
 *                         uri CRLF
 *
 *   conceptparam = *(";" other-param)
 * </pre>
 * @see  <a href="https://www.rfc-editor.org/rfc/rfc9253.html#name-concept">rfc9253</a>
 */
public class Concept extends Property {

    private static final String PROPERTY_NAME = "CONCEPT";
    
    private URI uri;

    public Concept() {
        super(PROPERTY_NAME);
    }

    public Concept(URI uri) {
        super(PROPERTY_NAME);
        this.uri = uri;
    }

    public Concept(ParameterList aList, String value) {
        super(PROPERTY_NAME, aList);
        setValue(value);
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public String getValue() {
        return Uris.decode(Strings.valueOf(getUri()));
    }

    @Override
    public void setValue(String aValue) {
        try {
            this.uri = Uris.create(aValue);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return ValidationResult.EMPTY;
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Concept> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(PROPERTY_NAME);
        }

        @Override
        public Concept createProperty(final ParameterList parameters, final String value) {
            return new Concept(parameters, value);
        }

        @Override
        public Concept createProperty() {
            return new Concept();
        }
    }

    @Override
    protected PropertyFactory<?> newFactory() {
        return new Factory();
    }
}
