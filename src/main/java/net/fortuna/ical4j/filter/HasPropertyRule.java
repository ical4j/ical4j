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
package net.fortuna.ical4j.filter;

import java.util.Iterator;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;

/**
 * $Id$
 *
 * Created on 5/02/2006
 *
 * A rule that matches any component containing the specified property. Note that this rule ignores any parameters
 * matching only on the value of the property.
 * @author Ben Fortuna
 */
public class HasPropertyRule extends ComponentRule {

    private Property property;

    private boolean matchEquals;

    /**
     * Constructs a new instance with the specified property. Ignores any parameters matching only on the value of the
     * property.
     * @param property a property instance to check for
     */
    public HasPropertyRule(final Property property) {
        this(property, false);
    }

    /**
     * Constructs a new instance with the specified property.
     * @param property the property to match
     * @param matchEquals if true, matches must contain an identical property (as indicated by
     * <code>Property.equals()</code>
     */
    public HasPropertyRule(final Property property, final boolean matchEquals) {
        this.property = property;
        this.matchEquals = matchEquals;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean match(final Component component) {
        boolean match = false;
        final PropertyList properties = component.getProperties(property.getName());
        for (final Iterator i = properties.iterator(); i.hasNext();) {
            final Property p = (Property) i.next();
            if (matchEquals && property.equals(p)) {
                match = true;
            }
            else if (property.getValue().equals(p.getValue())) {
                match = true;
            }
        }
        return match;
    }
}
