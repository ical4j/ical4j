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

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines a list of iCalendar parameters. A parameter list may be specified as unmodifiable at instantiation - useful
 * for constant properties that you don't want modified.
 * @author Ben Fortuna
 */
public class ParameterList implements Serializable {

    private static final long serialVersionUID = -1913059830016450169L;

    private final List parameters;

    /**
     * Default constructor. Creates a modifiable parameter list.
     */
    public ParameterList() {
        this(false);
    }

    /**
     * Constructor.
     * @param unmodifiable indicates whether the list should be mutable
     */
    public ParameterList(final boolean unmodifiable) {
        if (unmodifiable) {
            parameters = Collections.unmodifiableList(new ArrayList());
        }
        else {
            parameters = new CopyOnWriteArrayList();
        }
    }

    /**
     * Creates a deep copy of the specified parameter list. That is, copies of all parameters in the specified list are
     * added to this list.
     * @param list a parameter list to copy parameters from
     * @param unmodifiable indicates whether the list should be mutable
     * @throws URISyntaxException where a parameter in the list specifies an invalid URI value
     */
    public ParameterList(final ParameterList list, final boolean unmodifiable)
            throws URISyntaxException {
    	
        final List parameterList = new CopyOnWriteArrayList();
        for (final Iterator i = list.iterator(); i.hasNext();) {
            final Parameter parameter = (Parameter) i.next();
            parameterList.add(parameter.copy());
        }
        if (unmodifiable) {
            parameters = Collections.unmodifiableList(parameterList);
        }
        else {
        	parameters = parameterList;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        final StringBuffer buffer = new StringBuffer();
        for (final Iterator i = parameters.iterator(); i.hasNext();) {
            buffer.append(';');
            buffer.append(i.next().toString());
        }
        return buffer.toString();
    }

    /**
     * Returns the first parameter with the specified name.
     * @param aName name of the parameter
     * @return the first matching parameter or null if no matching parameters
     */
    public final Parameter getParameter(final String aName) {
        for (final Iterator i = parameters.iterator(); i.hasNext();) {
            final Parameter p = (Parameter) i.next();
            if (aName.equalsIgnoreCase(p.getName())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns a list of parameters with the specified name.
     * @param name name of parameters to return
     * @return a parameter list
     */
    public final ParameterList getParameters(final String name) {
        final ParameterList list = new ParameterList();
        for (final Iterator i = parameters.iterator(); i.hasNext();) {
            final Parameter p = (Parameter) i.next();
            if (p.getName().equalsIgnoreCase(name)) {
                list.add(p);
            }
        }
        return list;
    }

    /**
     * Add a parameter to the list. Note that this method will not remove existing parameters of the same type. To
     * achieve this use {
     * @link ParameterList#replace(Parameter) }
     * @param parameter the parameter to add
     * @return true
     * @see List#add(java.lang.Object)
     */
    public final boolean add(final Parameter parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("Trying to add null Parameter");
        }
        return parameters.add(parameter);
    }

    /**
     * Replace any parameters of the same type with the one specified.
     * @param parameter parameter to add to this list in place of all others with the same name
     * @return true if successfully added to this list
     */
    public final boolean replace(final Parameter parameter) {
        for (final Iterator i = getParameters(parameter.getName()).iterator(); i.hasNext();) {
            remove((Parameter) i.next());
        }
        return add(parameter);
    }

    /**
     * @return boolean indicates if the list is empty
     * @see List#isEmpty()
     */
    public final boolean isEmpty() {
        return parameters.isEmpty();
    }

    /**
     * @return an iterator
     * @see List#iterator()
     */
    public final Iterator iterator() {
        return parameters.iterator();
    }

    /**
     * Remove a parameter from the list.
     * @param parameter the parameter to remove
     * @return true if the list contained the specified parameter
     * @see List#remove(java.lang.Object)
     */
    public final boolean remove(final Parameter parameter) {
        return parameters.remove(parameter);
    }

    /**
     * Remove all parameters with the specified name.
     * @param paramName the name of parameters to remove
     */
    public final void removeAll(final String paramName) {
        final ParameterList params = getParameters(paramName);
        parameters.removeAll(params.parameters);
    }
    
    /**
     * @return the number of parameters in the list
     * @see List#size()
     */
    public final int size() {
        return parameters.size();
    }

    /**
     * {@inheritDoc}
     */
    public final boolean equals(final Object arg0) {
        if (arg0 instanceof ParameterList) {
            final ParameterList p = (ParameterList) arg0;
            return ObjectUtils.equals(parameters, p.parameters);
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    public final int hashCode() {
        return new HashCodeBuilder().append(parameters).toHashCode();
    }
}
