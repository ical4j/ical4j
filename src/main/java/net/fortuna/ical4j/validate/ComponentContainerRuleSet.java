/*
 *  Copyright (c) 2022, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentContainerRuleSet extends AbstractValidationRuleSet<ComponentContainer<? extends Component>> {

    @SafeVarargs
    public ComponentContainerRuleSet(ValidationRule<ComponentContainer<? extends Component>>... rules) {
        super(rules);
    }

    @Override
    public List<ValidationEntry> apply(String context, ComponentContainer<? extends Component> target) {
        List<ValidationEntry> results = new ArrayList<>();
        for (ValidationRule<? super ComponentContainer<? extends Component>> rule: rules) {
            List<String> matches = Collections.emptyList();
            if (rule.getPredicate().test(target)) {
                // only consider the specified instances in the total count..
                int total = rule.getInstances().stream().mapToInt(s -> target.getComponents(s).size()).sum();
                switch (rule.getType()) {
                    case None:
                        matches = matches(rule.getInstances(), s -> target.getComponents(s) != null);
                        break;
                    case One:
                        matches = matches(rule.getInstances(), s -> target.getComponents(s).size() != 1);
                        break;
                    case OneOrLess:
                        matches = matches(rule.getInstances(), s -> target.getComponents(s).size() > 1);
                        break;
                    case OneOrMore:
                        matches = matches(rule.getInstances(), s -> target.getComponents(s).size() < 1);
                        break;
                    case OneExclusive:
                        if (rule.getInstances().stream().anyMatch(s -> target.getComponents(s).size() > 0
                                && target.getComponents(s).size() != total)) {
                            matches = rule.getInstances();
                        }
                        break;
                    case AllOrNone:
                        if (total > 0 && total != rule.getInstances().size()) {
                            results.add(new ValidationEntry(rule, context));
                        }
                        break;
                }
                if (!matches.isEmpty()) {
                    results.add(new ValidationEntry(rule, context, matches.toArray(new String[0])));
                }
            }
        }
        return results;
    }
}
