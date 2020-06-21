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

import java.util.*;
import java.util.stream.Collectors;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Accessor implementation for a list of iCalendar parameters.
 * @author Ben Fortuna
 */
public class ParameterList implements ContentContainer<Parameter> {

    private final List<Parameter> parameters;

    /**
     * Default constructor. Creates an empty parameter list.
     */
    public ParameterList() {
        this(Collections.emptyList());
    }

    /**
     * Creates an unmodifiable copy of the specified parameter list.
     * @param list a parameter list to copy parameters from
     */
    public ParameterList(List<Parameter> list) {
        this.parameters = Collections.unmodifiableList(list);
    }

    @Override
    public ContentContainer<Parameter> add(Parameter content) {
        List<Parameter> copy = new ArrayList<>(parameters);
        copy.add(content);
        return new ParameterList(copy);
    }

    @Override
    public ContentContainer<Parameter> addAll(Collection<Parameter> content) {
        List<Parameter> copy = new ArrayList<>(parameters);
        copy.addAll(content);
        return new ParameterList(copy);
    }

    @Override
    public ContentContainer<Parameter> remove(Parameter content) {
        List<Parameter> copy = new ArrayList<>(parameters);
        copy.remove(content);
        return new ParameterList(copy);
    }

    @Override
    public ContentContainer<Parameter> removeAll(String... name) {
        List<String> names = Arrays.asList(name);
        List<Parameter> copy = new ArrayList<>(parameters);
        copy.removeIf(p -> names.contains(p.getName()));
        return new ParameterList(copy);
    }

    @Override
    public ContentContainer<Parameter> replace(Parameter content) {
        List<Parameter> copy = new ArrayList<>(parameters);
        copy.removeIf(p -> p.getName().equals(content.getName()));
        copy.add(content);
        return new ParameterList(copy);
    }

    @Override
    public List<Parameter> getAll() {
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        if (!parameters.isEmpty()) {
            return parameters.stream().map(Parameter::toString)
                    .collect(Collectors.joining(";", ";", ""));
        }
        return "";
    }

    /**
     * Returns the first parameter with the specified name.
     * @param aName name of the parameter
     * @return the first matching parameter or null if no matching parameters
     *
     * @deprecated use {@link ParameterList#getFirst(String)}
     */
    @Deprecated
    public final <T extends Parameter> Optional<T> getParameter(final String aName) {
        return getFirst(aName);
    }

    /**
     * Returns a list of parameters with the specified name.
     * @param name name of parameters to return
     * @return a parameter list
     *
     * @deprecated use {@link ParameterList#get(String)}
     */
    @Deprecated
    public final ParameterList getParameters(final String name) {
        final ParameterList list = new ParameterList(get(name));
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterList that = (ParameterList) o;
        return Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameters);
    }
}
