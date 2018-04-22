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
package net.fortuna.ical4j.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * $Id$
 *
 * Created on 4/02/2006
 *
 * Provides indexing of properties on a specific parameter.
 * @author Ben Fortuna
 */
public class IndexedPropertyList {

    private static final PropertyList<Property> EMPTY_LIST = new PropertyList<Property>();
    
    private Map<String, PropertyList<Property>> index;
    
    /**
     * Creates a new instance indexed on the parameters with the specified name.
     * @param list a list of properties
     * @param parameterName the name of parameters on which to index
     */
    public IndexedPropertyList(final PropertyList<Property> list, final String parameterName) {
        final Map<String, PropertyList<Property>> indexedProperties = new HashMap<String, PropertyList<Property>>();
        list.forEach(property -> property.getParameters(parameterName).forEach(parameter -> {
            PropertyList<Property> properties = indexedProperties.get(parameter.getValue());
            if (properties == null) {
                properties = new PropertyList<Property>();
                indexedProperties.put(parameter.getValue(), properties);
            }
            properties.add(property);
        }));
        this.index = Collections.unmodifiableMap(indexedProperties);
    }
    
    /**
     * Returns a list of properties containing a parameter with the
     * specified value.
     * @param paramValue the value of the parameter contained in the
     * returned properties
     * @return a property list
     */
    public PropertyList<Property> getProperties(final String paramValue) {
        PropertyList<Property> properties = index.get(paramValue);
        if (properties == null) {
            properties = EMPTY_LIST;
        }
        return properties;
    }
    
    /**
     * Returns the first property containing a parameter with the specified
     * value.
     * @param paramValue the value of the parameter identified in the returned
     * property
     * @return a property or null if no property is found containing a parameter
     * with the specified value
     */
    public Property getProperty(final String paramValue) {
        final PropertyList<Property> properties = getProperties(paramValue);
        if (!properties.isEmpty()) {
            return properties.iterator().next();
        }
        return null;
    }
}
