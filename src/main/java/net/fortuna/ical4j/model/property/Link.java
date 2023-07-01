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

/**
 * <pre>
 *     Purpose:
 *     This property provides a reference to external information related to a component.
 * Value type:
 *     URI, UID, or XML-REFERENCE
 * Property Parameters:
 *     The VALUE parameter is required. Non-standard, link relation type, format type, label, and language parameters can also be specified on this property. The LABEL parameter is defined in [RFC7986].
 * Conformance:
 *     This property can be specified zero or more times in any iCalendar component.
 * Description:
 *     When used in a component, the value of this property points to additional information related to the component. For example, it may reference the originating web server.
 * Format Definition:
 *
 *     This property is defined by the following notation:
 *
 *    link           = "LINK" linkparam ":"
 *                       ( uri /  ; for VALUE=XML-REFERENCE
 *                         uri /  ; for VALUE=URI
 *                         text ) ; for VALUE=UID
 *                     CRLF
 *
 *    linkparam      = (";" "VALUE" "=" ("XML-REFERENCE" /
 *                                 "URI" /
 *                                 "UID"))
 *                     1*(";" linkrelparam)
 *                     1*(";" fmttypeparam)
 *                     1*(";" labelparam)
 *                     1*(";" languageparam)
 *                     *(";" other-param)
 *                     ; the elements herein may appear in any order,
 *                     ; and the order is not significant.
 * </pre>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9253.html#name-link">rfc9253</a>
 */

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.net.URI;
import java.net.URISyntaxException;

public class Link extends Property {

    private static final String PROPERTY_NAME = "LINK";

    private URI uri;

    private String value;

    public Link() {
        super(PROPERTY_NAME);
    }

    public Link(URI uri) {
        super(PROPERTY_NAME, new Factory());
        this.uri = uri;
    }

    public Link(String value) {
        super(PROPERTY_NAME, new Factory());
        this.value = value;
    }

    public Link(ParameterList aList, String value) {
        super(PROPERTY_NAME, aList);
        setValue(value);
    }

    public URI getUri() {
        return uri;
    }

    /**
     *
     * @return the text value of the property
     * @deprecated use {@link Link#getValue()}
     */
    @Deprecated
    public String getText() {
        return value;
    }

    @Override
    public String getValue() {
        if (Value.XML_REFERENCE.equals(getRequiredParameter(Parameter.VALUE)) ||
                Value.URI.equals(getRequiredParameter(Parameter.VALUE))) {
            return Uris.decode(Strings.valueOf(getUri()));
        } else { // if (Value.UID.equals(getParameter(Parameter.VALUE))) {
            return value;
        }
    }

    @Override
    public void setValue(String aValue) throws URISyntaxException {
        if (Value.TEXT.equals(getRequiredParameter(Parameter.VALUE))) {
            this.value = aValue;
            this.uri = null;
        } else {
            this.uri = Uris.create(aValue);
            this.value = null;
        }
    }

    @Override
    public ValidationResult validate() throws ValidationException {
        return null;
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Link> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(PROPERTY_NAME);
        }

        @Override
        public Link createProperty(final ParameterList parameters, final String value) {
            return new Link(parameters, value);
        }

        @Override
        public Link createProperty() {
            return new Link();
        }
    }

    @Override
    protected PropertyFactory<?> newFactory() {
        return new Factory();
    }
}
