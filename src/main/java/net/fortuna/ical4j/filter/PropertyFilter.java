/**
 * Copyright (c) 2004-2021, Ben Fortuna
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

import net.fortuna.ical4j.filter.expression.BinaryExpression;
import net.fortuna.ical4j.filter.expression.UnaryExpression;
import net.fortuna.ical4j.filter.predicate.ParameterEqualToRule;
import net.fortuna.ical4j.filter.predicate.ParameterExistsRule;
import net.fortuna.ical4j.filter.predicate.ParameterStartsWithRule;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PropertyFilter extends AbstractFilter<Property> {

    public PropertyFilter() {
    }

    public PropertyFilter(Supplier<List<PropertyFactory<?>>> propertyFactorySupplier, Supplier<List<ParameterFactory<?>>> parameterFactorySupplier) {
        super(propertyFactorySupplier, parameterFactorySupplier);
    }

    public Predicate<Property> predicate(UnaryExpression expression) {
        switch (expression.operator) {
            case not:
                return predicate(expression.operand).negate();
            case notExists:
                return new ParameterExistsRule(parameter(expression)).negate();
            case exists:
                return new ParameterExistsRule(parameter(expression));
        }
        throw new IllegalArgumentException("Not a valid filter");
    }

    public Predicate<Property> predicate(BinaryExpression expression) {
        switch (expression.operator) {
            case equalTo:
                return new ParameterEqualToRule<>(parameter(expression));
            case startsWith:
                return new ParameterStartsWithRule<>(parameter(expression), literal(expression));
            case and:
                return predicate(expression.left).and(predicate(expression.right));
            case or:
                return predicate(expression.left).or(predicate(expression.right));
        }
        throw new IllegalArgumentException("Not a valid filter");
    }
}
