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

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines a list of iCalendar components.
 * @author Ben Fortuna
 */
public class ComponentList<T extends Component> implements ContentCollection<T>,
        Comparable<ComponentList<? extends Component>> {

    private final List<T> components;

    /**
     * Default constructor.
     */
    public ComponentList() {
        this(Collections.emptyList());
    }

    /**
     * Create  new component list containing the components in the specified list.
     *
     * @param components
     */
    public ComponentList(List<? extends T> components) {
        this.components = Collections.unmodifiableList(components);
    }

    @Override
    public ContentCollection<T> add(@NotNull T content) {
        List<T> copy = new ArrayList<>(components);
        copy.add(content);
        return new ComponentList<>(copy);
    }

    @Override
    public ContentCollection<T> addAll(@NotNull Collection<T> content) {
        List<T> copy = new ArrayList<>(components);
        copy.addAll(content);
        return new ComponentList<>(copy);
    }

    @Override
    public ContentCollection<T> remove(T content) {
        List<T> copy = new ArrayList<>(components);
        if (copy.remove(content)) {
            return new ComponentList<>(copy);
        } else {
            return this;
        }
    }

    @Override
    public ContentCollection<T> removeAll(String... name) {
        List<String> names = Arrays.asList(name);
        return removeIf(c -> names.contains(c.getName()));
    }

    @Override
    public ContentCollection<T> removeIf(Predicate<T> filter) {
        List<T> copy = new ArrayList<>(components);
        if (copy.removeIf(filter)) {
            return new ComponentList<>(copy);
        } else {
            return this;
        }
    }

    @Override
    public ContentCollection<T> replace(@NotNull T content) {
        List<T> copy = new ArrayList<>(components);
        copy.removeIf(c -> c.getName().equalsIgnoreCase(content.getName()));
        copy.add(content);
        return new ComponentList<>(copy);
    }

    @Override
    public List<T> getAll() {
        return components;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return components.stream().map(Component::toString).collect(Collectors.joining(""));
    }

    /**
     * Returns the first component of specified name.
     * @param aName name of component to return
     * @return a component or null if no matching component found
     *
     * @deprecated use {@link ComponentList#getFirst(String)}
     */
    @Deprecated
    public final <R extends T> Optional<R> getComponent(final String aName) {
        return getFirst(aName);
    }

    /**
     * Returns a list containing all components with specified name.
     * @param name name of components to return
     * @return a list of components with the matching name
     *
     * @deprecated use {@link ContentCollection#get(String...)}
     */
    @Deprecated
	public final <C extends T> List<C> getComponents(final String... name) {
        return get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentList<?> that = (ComponentList<?>) o;
        return Objects.equals(components, that.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components);
    }

    @Override
    public int compareTo(ComponentList<? extends Component> o) {
        // test for equality
        if (components.equals(o.components)) {
            return 0;
        }
        // then test for size..
        int retval = components.size() - o.components.size();
        if (retval != 0) {
            return retval;
        } else {
            // count missing component..
            return (int) o.components.stream().filter(c -> !components.contains(c)).count();
//            // compare individual params..
//            return components.stream().filter(o.components::contains)
//                    .mapToInt(c -> c.compareTo(o.components.get(o.components.indexOf(c)))).sum();
        }
    }
}
