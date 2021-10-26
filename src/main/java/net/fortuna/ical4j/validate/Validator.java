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
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.ComponentContainer;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyContainer;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementors apply validation rules to iCalendar content to determine a level of compliance with the published
 * specifications.
 *
 * @author fortuna
 */
public interface Validator<T> extends Serializable {

    static <T> void assertFalse(Predicate<T> predicate, String message, boolean warn, T target,
                                Object...messageParams) {

        if (predicate.test(target)) {
            if (warn) {
                LoggerFactory.getLogger(Validator.class).warn(MessageFormat.format(message, messageParams));
            } else {
                throw new ValidationException(message, messageParams);
            }
        }
    }

    /**
     * Validates the target content by applying validation rules. When content fails validation the validator
     * may throw an exception depending on the implementation.
     * @throws ValidationException indicates validation failure (implementation-specific)
     */
    void validate(T target) throws ValidationException;

    default List<String> apply(ValidationRule rule, ComponentContainer target) {
        int total = rule.getInstances().stream().mapToInt(s -> target.getComponents(s).size()).sum();
        switch (rule.getType()) {
            case None:
                return rule.getInstances().stream().filter(s -> target.getComponent(s) != null)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case One:
                return rule.getInstances().stream().filter(s -> target.getComponents(s).size() != 1)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case OneOrLess:
                return rule.getInstances().stream().filter(s -> target.getComponents(s).size() > 1)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case OneOrMore:
                return rule.getInstances().stream().filter(s -> target.getComponents(s).size() < 1)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case OneExclusive:
                for (String instance : rule.getInstances()) {
                    int count = target.getComponents(instance).size();
                    if (count > 0 && count != total) {
                        return Collections.singletonList(
                                String.format("%s %s", rule.getType().getDescription(),
                                        String.join(",", rule.getInstances())));
                    }
                }
            case AllOrNone:
                if (total > 0 && total != rule.getInstances().size()) {
                    return Collections.singletonList(
                            String.format("%s %s", rule.getType().getDescription(),
                                    String.join(",", rule.getInstances())));
                }
        }
        return Collections.emptyList();
    }

    default List<String> apply(ValidationRule rule, PropertyContainer target) {
        int total = rule.getInstances().stream().mapToInt(s -> target.getProperties(s).size()).sum();
        switch (rule.getType()) {
            case None:
                return rule.getInstances().stream().filter(s -> target.getProperty(s) != null)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case One:
                return rule.getInstances().stream().filter(s -> target.getProperties(s).size() != 1)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case OneOrLess:
                return rule.getInstances().stream().filter(s -> target.getProperties(s).size() > 1)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case OneOrMore:
                return rule.getInstances().stream().filter(s -> target.getProperties(s).size() < 1)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case OneExclusive:
                for (String instance : rule.getInstances()) {
                    int count = target.getProperties(instance).size();
                    if (count > 0 && count != total) {
                        return Collections.singletonList(
                                String.format("%s %s", rule.getType().getDescription(),
                                        String.join(",", rule.getInstances())));
                    }
                }
                break;
            case AllOrNone:
                if (total > 0 && total != rule.getInstances().size()) {
                    return Collections.singletonList(
                            String.format("%s %s", rule.getType().getDescription(),
                                    String.join(",", rule.getInstances())));
                }
                break;
        }
        return Collections.emptyList();
    }

    default List<String> apply(ValidationRule rule, Property target) {
        int total = rule.getInstances().stream().mapToInt(s -> target.getParameters(s).size()).sum();
        switch (rule.getType()) {
            case None:
                return rule.getInstances().stream().filter(s -> target.getParameter(s) != null)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case One:
                return rule.getInstances().stream().filter(s -> target.getParameters(s).size() != 1)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case OneOrLess:
                return rule.getInstances().stream().filter(s -> target.getParameters(s).size() > 1)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case OneOrMore:
                return rule.getInstances().stream().filter(s -> target.getParameters(s).size() < 1)
                        .map(s -> String.format("%s %s", rule.getType().getDescription(), s))
                        .collect(Collectors.toList());
            case OneExclusive:
                for (String instance : rule.getInstances()) {
                    int count = target.getParameters(instance).size();
                    if (count > 0 && count != total) {
                        return Collections.singletonList(
                                String.format("%s %s", rule.getType().getDescription(),
                                        String.join(",", rule.getInstances())));
                    }
                }
            case AllOrNone:
                if (total > 0 && total != rule.getInstances().size()) {
                    return Collections.singletonList(
                            String.format("%s %s", rule.getType().getDescription(),
                                    String.join(",", rule.getInstances())));
                }
        }
        return Collections.emptyList();
    }

}
