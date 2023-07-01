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
import net.fortuna.ical4j.validate.ValidationResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * $Id$
 * <p/>
 * Created: [Apr 6, 2004]
 * <p/>
 * Defines a RELATED-TO iCalendar component property.
 *
 * <pre>
 *     Purpose:
 *     This property is used to represent a relationship or reference between one calendar component and another. The definition here extends the definition in Section 3.8.4.5 of [RFC5545] by allowing URI or UID values and a GAP parameter.
 * Value Type:
 *     URI, UID, or TEXT
 * Property Parameters:
 *     Relationship type, IANA, and non-standard property parameters can be specified on this property.
 * Conformance:
 *     This property MAY be specified in any iCalendar component.
 * Description:
 *
 *     By default or when VALUE=UID is specified, the property value consists of the persistent, globally unique identifier of another calendar component. This value would be represented in a calendar component by the UID property.
 * By default, the property value points to another calendar component that has a PARENT relationship to the referencing object. The RELTYPE property parameter is used to either explicitly state the default PARENT relationship type to the referenced calendar component or to override the default PARENT relationship type and specify either a CHILD or SIBLING relationship or a temporal relationship.The PARENT relationship indicates that the calendar component is a subordinate of the referenced calendar component. The CHILD relationship indicates that the calendar component is a superior of the referenced calendar component. The SIBLING relationship indicates that the calendar component is a peer of the referenced calendar component.To preserve backwards compatibility, the value type MUST be UID when the PARENT, SIBLING, or CHILD relationships are specified.The FINISHTOSTART, FINISHTOFINISH, STARTTOFINISH, or STARTTOSTART relationships define temporal relationships, as specified in the RELTYPE parameter definition.The FIRST and NEXT define ordering relationships between calendar components.The DEPENDS-ON relationship indicates that the current calendar component depends on the referenced calendar component in some manner. For example, a task may be blocked waiting on the other, referenced, task.The REFID and CONCEPT relationships establish a reference from the current component to the referenced component.Changes to a calendar component referenced by this property can have an implicit impact on the related calendar component. For example, if a group event changes its start or end date or time, then the related, dependent events will need to have their start and end dates and times changed in a corresponding way. Similarly, if a PARENT calendar component is canceled or deleted, then there is an implied impact to the related CHILD calendar components. This property is intended only to provide information on the relationship of calendar components.Deletion of the target component, for example, the target of a FIRST, NEXT, or temporal relationship, can result in broken links.It is up to the target calendar system to maintain any property implications of these relationships.
 * Format Definition:
 *
 *     This property is defined by the following notation:
 *
 *    related    = "RELATED-TO" relparam ":"
 *                             ( text / ; for VALUE=UID
 *                               uri /  ; for VALUE=URI
 *                               text ) ; for VALUE=TEXT or default
 *                 CRLF
 *
 *    relparam   = ; the elements herein may appear in any order,
 *                 ; and the order is not significant.
 *                 [";" "VALUE" "=" ("UID" /
 *                                   "URI" /
 *                                   "TEXT")]
 *                 [";" reltypeparam]
 *                 [";" gapparam]
 *                 *(";" other-param)
 * </pre>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9253.html#name-updates-to-rfc-5545">rfc9253</a>
 *
 * @author benf
 */
public class RelatedTo extends Property implements Encodable {

    private static final long serialVersionUID = -109375299147319752L;

    private String value;

    /**
     * Default constructor.
     */
    public RelatedTo() {
        super(RELATED_TO, new ParameterList(), new Factory());
    }

    /**
     * @param aValue a value string for this component
     */
    public RelatedTo(final String aValue) {
        super(RELATED_TO, new ParameterList(), new Factory());
        setValue(aValue);
    }

    /**
     * @param aList  a list of parameters for this component
     * @param aValue a value string for this component
     */
    public RelatedTo(final ParameterList aList, final String aValue) {
        super(RELATED_TO, aList, new Factory());
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

    @Override
    public ValidationResult validate() throws ValidationException {
        return PropertyValidator.RELATED_TO.validate(this);
    }

    public static class Factory extends Content.Factory implements PropertyFactory<RelatedTo> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(RELATED_TO);
        }

        @Override
        public RelatedTo createProperty(final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new RelatedTo(parameters, value);
        }

        @Override
        public RelatedTo createProperty() {
            return new RelatedTo();
        }
    }

}
