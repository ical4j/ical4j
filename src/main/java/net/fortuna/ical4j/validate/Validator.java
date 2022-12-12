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
import java.util.List;
import java.util.function.Predicate;

/**
 * Implementors apply validation rules to iCalendar content to determine a level of compliance with the published
 * specifications.
 *
 * @author fortuna
 */
public interface Validator<T> extends Serializable {

    @Deprecated
    static <T> void assertFalse(Predicate<T> predicate, String message, boolean warn, T target,
                                Object...messageParams) throws ValidationException {

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
     *
     * @param target the target of validation
     * @return the result of validation applied to the specified target
     * @throws ValidationException indicates validation failure (implementation-specific)
     */
    ValidationResult validate(T target) throws ValidationException;

    /**
     *
     * @param rule
     * @param context
     * @param target
     * @return
     * @deprecated use {@link ComponentContainerRuleSet#apply(String, ComponentContainer)}
     */
    @Deprecated
    default List<ValidationEntry> apply(ValidationRule rule, String context, ComponentContainer<?> target) {
        return new ComponentContainerRuleSet(rule).apply(context, target);
    }

    /**
     *
     * @param rule
     * @param context
     * @param target
     * @return
     * @deprecated use {@link PropertyContainerRuleSet#apply(String, PropertyContainer)}
     */
    @Deprecated
    default List<ValidationEntry> apply(ValidationRule rule, String context, PropertyContainer target) {
        return new PropertyContainerRuleSet<>(rule).apply(context, target);
    }

    /**
     *
     * @param rule
     * @param target
     * @return
     * @deprecated use {@link PropertyRuleSet#apply(String, Property)}
     */
    @Deprecated
    default List<ValidationEntry> apply(ValidationRule rule, Property target) {
        return new PropertyRuleSet<>(rule).apply(target.getName(), target);
    }
}
