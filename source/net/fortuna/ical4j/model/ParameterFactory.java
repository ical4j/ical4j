/*
 * $Id$ [05-Apr-2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import java.net.URISyntaxException;

import net.fortuna.ical4j.model.parameter.AltRep;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.parameter.DelegatedFrom;
import net.fortuna.ical4j.model.parameter.DelegatedTo;
import net.fortuna.ical4j.model.parameter.Dir;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.FbType;
import net.fortuna.ical4j.model.parameter.FmtType;
import net.fortuna.ical4j.model.parameter.Language;
import net.fortuna.ical4j.model.parameter.Member;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Range;
import net.fortuna.ical4j.model.parameter.RelType;
import net.fortuna.ical4j.model.parameter.Related;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.parameter.SentBy;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.parameter.XParameter;

/**
 * A factory for creating iCalendar parameters.
 *
 * @author benfortuna
 */
public final class ParameterFactory {

    private static ParameterFactory instance = new ParameterFactory();

    /**
     * Constructor made private to prevent instantiation.
     */
    private ParameterFactory() {
    }

    /**
     * @return Returns the instance.
     */
    public static ParameterFactory getInstance() {
        return instance;
    }

    /**
     * Creates a parameter.
     *
     * @param name
     *            name of the parameter
     * @param value
     *            a parameter value
     * @return a component
     * @throws URISyntaxException thrown when the specified string
     * is not a valid representation of a URI for selected parameters
     */
    public Parameter createParameter(final String name, final String value)
            throws URISyntaxException {

        if (Parameter.ALTREP.equals(name)) {
            return new AltRep(value);
        }
        else if (Parameter.CN.equals(name)) {
            return new Cn(value);
        }
        else if (Parameter.CUTYPE.equals(name)) {
            return new CuType(value);
        }
        else if (Parameter.DELEGATED_FROM.equals(name)) {
            return new DelegatedFrom(value);
        }
        else if (Parameter.DELEGATED_TO.equals(name)) {
            return new DelegatedTo(value);
        }
        else if (Parameter.DIR.equals(name)) {
            return new Dir(value);
        }
        else if (Parameter.ENCODING.equals(name)) {
            return new Encoding(value);
        }
        else if (Parameter.FMTTYPE.equals(name)) {
            return new FmtType(value);
        }
        else if (Parameter.FBTYPE.equals(name)) {
            return new FbType(value);
        }
        else if (Parameter.LANGUAGE.equals(name)) {
            return new Language(value);
        }
        else if (Parameter.MEMBER.equals(name)) {
            return new Member(value);
        }
        else if (Parameter.PARTSTAT.equals(name)) {
            return new PartStat(value);
        }
        else if (Parameter.RANGE.equals(name)) {
            return new Range(value);
        }
        else if (Parameter.RELATED.equals(name)) {
            return new Related(value);
        }
        else if (Parameter.RELTYPE.equals(name)) {
            return new RelType(value);
        }
        else if (Parameter.ROLE.equals(name)) {
            return new Role(value);
        }
        else if (Parameter.RSVP.equals(name)) {
            return new Rsvp(value);
        }
        else if (Parameter.SENT_BY.equals(name)) {
            return new SentBy(value);
        }
        else if (Parameter.TZID.equals(name)) {
            return new TzId(value);
        }
        else if (Parameter.VALUE.equals(name)) {
            return new Value(value);
        }
        // assume experimental parameter..
        else {
            return new XParameter(name, value);
        }
    }
}