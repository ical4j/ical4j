/**
 * Copyright (c) 2010, Ben Fortuna
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

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;

import java.net.URISyntaxException;

/**
 * $Id: Rsvp.java,v 1.16 2010/03/06 12:57:25 fortuna Exp $ [18-Apr-2004]
 *
 * Defines an Order parameter.
 * @author benfortuna
 */
public class Order extends Parameter {

    private static final long serialVersionUID = -5381653882942018012L;

    private Integer order;

    /**
     * @param aValue a string representation
     */
    public Order(final String aValue) {
        this(Integer.valueOf(aValue));
    }

    /**
     * @param aValue an Integer value
     */
    public Order(final Integer aValue) {
        super(ORDER, new Factory());
        this.order = aValue;
    }

    /**
     * @return Returns the order.
     */
    public final Integer getOrder() {
        return order;
    }

    /**
     * {@inheritDoc}
     */
    public final String getValue() {
        return getOrder().toString();
    }

    public static class Factory extends Content.Factory implements
            ParameterFactory {
        private static final long serialVersionUID = 1L;
    
        public Factory() {
          super(ORDER);
        }
    
        public Parameter createParameter(final String value) throws URISyntaxException {
            return new Order(value);
        }
    }
}
