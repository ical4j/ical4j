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
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;

import net.fortuna.ical4j.model.AddressList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.util.Strings;

/**
 * $Id$ [18-Apr-2004]
 *
 * Defines a Delegatees parameter.
 * @author benfortuna
 */
public class DelegatedTo extends Parameter {

    private static final long serialVersionUID = 567577003350648021L;

    private AddressList delegatees;

    /**
     * @param aValue a string representation of Delegatees
     * @throws URISyntaxException when the specified string is not a valid list of cal-addresses
     */
    public DelegatedTo(final String aValue) throws URISyntaxException {
        this(new AddressList(Strings.unquote(aValue)));
    }

    /**
     * @param aList a list of addresses
     */
    public DelegatedTo(final AddressList aList) {
        super(DELEGATED_TO, ParameterFactoryImpl.getInstance());
        delegatees = aList;
    }

    /**
     * @return Returns the delegatees addresses.
     */
    public final AddressList getDelegatees() {
        return delegatees;
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return getDelegatees().toString();
    }
    
    /**
     * {@inheritDoc}
     */
    protected boolean isQuotable() {
        // override default behaviour as quoting is handled by the implementation..
        return false;
    }
}
