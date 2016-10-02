/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <p>
 * o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p>
 * o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
 * o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p>
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

import net.fortuna.ical4j.model.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

  private List<Rule<T>> rules;

  private int type;

  /**
   * Constructor.
   *
   * @param rules an array of rules that define this filter
   * @param type  the type of matching to apply
   * @see Filter#MATCH_ALL
   * @see Filter#MATCH_ANY
   */
  public Filter(final List<Rule<T>> rules, final int type) {
    this.rules = rules;
    this.type = type;
  }

  /**
   * Filter the given list into a new list.
   *
   * @param c a list to filter
   * @return a filtered list
   */
  public final List<T> filter(final List<T> c) {
    if (getRules().size() > 0) {
      // attempt to use the same concrete collection type
      // as is passed in..
      List<T> filtered;
      try {
        filtered = c.getClass().newInstance();
      } catch (Exception e) {
        filtered = new ArrayList<>();
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
    List<T> list = new ArrayList<>(c);
    List<T> temp = new ArrayList<>();
    for (Rule<T> r : getRules()) {
      for (final T o : list) {
        if (r.match(o)) {
          temp.add(o);
        }
      }
      list = temp;
      temp = new ArrayList<>();
    }
    return list;
  }

  private List<T> matchAny(Collection<T> c) {
    final List<T> matches = new ArrayList<>();
    for (T o : c) {
      for (Rule<T> r : getRules()) {
        if (r.match(o)) {
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
  public final List<T> filter(final T... objects) {
    final List<T> filtered = filter(Arrays.asList(objects));
    try {
      return filtered;
    } catch (ArrayStoreException ase) {
      Logger log = LoggerFactory.getLogger(Filter.class);
      log.warn("Error converting to array - using default approach", ase);
    }
    return filtered;
  }

  /**
   * @return Returns the rules.
   */
  public final List<Rule<T>> getRules() {
    return rules;
  }

  /**
   * @param rules The rules to set.
   */
  public final void setRules(final Rule<T>[] rules) {
    this.rules = Arrays.asList(rules);
  }
}
