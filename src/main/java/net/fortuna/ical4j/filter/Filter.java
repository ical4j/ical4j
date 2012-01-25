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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$
 *
 * Created: Feb 1, 2006
 *
 * Performs collection filtering based on a set of rules. A filter may dictate whether at least one rule or all rules
 * are matched.
 * 
 * NOTE: Implementation of filter rules has changed in recent releases to fix behaviour. Please ensure you update
 * your code to use explicit constructors.
 * 
 * @author Ben Fortuna
 */
public class Filter {

    /**
     * Indicates that any rule may be matched to include an object in the filtered collection.
     */
    public static final int MATCH_ANY = 1;

    /**
     * Indicates that all rules must be matched to include an object in the filtered collection.
     */
    public static final int MATCH_ALL = 2;

    private List rules;

    private int type;

    /**
     * Constructor.
     * @param rule a rule that defines this filter
     * @deprecated Prior implementations of this class did not work as advertised, so
     * to avoid confusion please use constructors that explicitly specify the desired behaviour
     */
    public Filter(final Rule rule) {
        this(new Rule[] { rule }, MATCH_ANY);
    }

    /**
     * Constructor.
     * @param rules an array of rules that define this filter
     * @param type the type of matching to apply
     * @see Filter#MATCH_ALL
     * @see Filter#MATCH_ANY
     */
    public Filter(final Rule[] rules, final int type) {
        this.rules = Arrays.asList(rules);
        this.type = type;
    }

    /**
     * Filter the given collection into a new collection.
     * @param c a collection to filter
     * @return a filtered collection
     */
    public final Collection filter(final Collection c) {
        if (getRules() != null && getRules().length > 0) {
            // attempt to use the same concrete collection type
            // as is passed in..
            Collection filtered;
            try {
                filtered = (Collection) c.getClass().newInstance();
            }
            catch (Exception e) {
                filtered = new ArrayList();
            }

            if (type == MATCH_ALL) {
                filtered.addAll(matchAll(c));
            }
            else {
                filtered.addAll(matchAny(c));
            }
            return filtered;
        }
        return c;
    }

    private List matchAll(Collection c) {
        List list = new ArrayList(c);
        List temp = new ArrayList();
        for (int n = 0; n < getRules().length; n++) {
            for (final Iterator i = list.iterator(); i.hasNext();) {
                final Object o = i.next();
                if (getRules()[n].match(o)) {
                    temp.add(o);
                }
            }
            list = temp;
            temp = new ArrayList();
        }
        return list;
    }

    private List matchAny(Collection c) {
        final List matches = new ArrayList();
        for (Iterator i = c.iterator(); i.hasNext();) {
            final Object o = i.next();
            for (int n = 0; n < getRules().length; n++) {
                if (getRules()[n].match(o)) {
                    matches.add(o);
                    break;
                }
            }
        }
        return matches;
    }
    
    /**
     * Returns a filtered subset of the specified array.
     * @param objects an array to filter
     * @return a filtered array
     */
    public final Object[] filter(final Object[] objects) {
        final Collection filtered = filter(Arrays.asList(objects));
        try {
            return filtered.toArray((Object[]) Array.newInstance(objects
                    .getClass(), filtered.size()));
        }
        catch (ArrayStoreException ase) {
            Log log = LogFactory.getLog(Filter.class);
            log.warn("Error converting to array - using default approach", ase);
        }
        return filtered.toArray();
    }

    /**
     * @return Returns the rules.
     */
    public final Rule[] getRules() {
        return (Rule[]) rules.toArray(new Rule[rules.size()]);
    }

    /**
     * @param rules The rules to set.
     */
    public final void setRules(final Rule[] rules) {
        this.rules = Arrays.asList(rules);
    }
}
