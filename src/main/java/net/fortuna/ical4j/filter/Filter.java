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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * $Id$
 * <p/>
 * Created: Feb 1, 2006
 * <p/>
 * Performs collection filtering based on a set of rules. A filter may dictate whether at least one rule or all rules
 * are matched.
 * <p/>
 * NOTE: Implementation of filter rules has changed in recent releases to fix behaviour. Please ensure you update
 * your code to use explicit constructors.
 *
 * @author Ben Fortuna
 */
public class Filter<T> {

    /**
     * Indicates that any rule may be matched to include an object in the filtered collection.
     */
    public static final int MATCH_ANY = 1;

    /**
     * Indicates that all rules must be matched to include an object in the filtered collection.
     */
    public static final int MATCH_ALL = 2;

    private List<Predicate<T>> rules;

    private int type;

    /**
     * @param rules one or more rules that are applied by this filter
     */
    @SafeVarargs
    public Filter(Predicate<T>... rules) {
        this(rules, MATCH_ANY);
    }

    /**
     * Constructor.
     *
     * @param rules an array of rules that define this filter
     * @param type  the type of matching to apply
     * @see Filter#MATCH_ALL
     * @see Filter#MATCH_ANY
     */
    public Filter(Predicate<T>[] rules, final int type) {
        this.rules = Arrays.asList(rules);
        this.type = type;
    }

    /**
     * Filter the given collection into a new collection.
     *
     * @param c a collection to filter
     * @return a filtered collection
     */
    @SuppressWarnings("unchecked")
    public final Collection<T> filter(final Collection<T> c) {
        if (getRules() != null && getRules().length > 0) {
            // attempt to use the same concrete collection type
            // as is passed in..
            Collection<T> filtered;
            try {
                filtered = c.getClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                filtered = new ArrayList<T>();
            }

            if (type == MATCH_ALL) {
                filtered.addAll(matchAll(c));
            } else {
                filtered.addAll(matchAny(c));
            }
            return filtered;
        }
        return c;
    }

    private List<T> matchAll(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        List<T> temp = new ArrayList<T>();
        for (int n = 0; n < getRules().length; n++) {
            for (final T o : list) {
                if (getRules()[n].test(o)) {
                    temp.add(o);
                }
            }
            list = temp;
            temp = new ArrayList<T>();
        }
        return list;
    }

    private List<T> matchAny(Collection<T> c) {
        final List<T> matches = new ArrayList<T>();
        for (T o : c) {
            for (int n = 0; n < getRules().length; n++) {
                if (getRules()[n].test(o)) {
                    matches.add(o);
                    break;
                }
            }
        }
        return matches;
    }

    /**
     * Returns a filtered subset of the specified array.
     *
     * @param objects an array to filter
     * @return a filtered array
     */
    @SuppressWarnings("unchecked")
    public final T[] filter(final T[] objects) {
        final Collection<T> filtered = filter(Arrays.asList(objects));
        try {
            return filtered.toArray((T[]) Array.newInstance(objects
                    .getClass(), filtered.size()));
        } catch (ArrayStoreException ase) {
            Logger log = LoggerFactory.getLogger(Filter.class);
            log.warn("Error converting to array - using default approach", ase);
        }
        return (T[]) filtered.toArray();
    }

    /**
     * @return Returns the rules.
     */
    @SuppressWarnings("unchecked")
    public final Predicate<T>[] getRules() {
        return rules.toArray(new Predicate[0]);
    }

    /**
     * @param rules The rules to set.
     */
    public final void setRules(final Predicate<T>[] rules) {
        this.rules = Arrays.asList(rules);
    }
}
