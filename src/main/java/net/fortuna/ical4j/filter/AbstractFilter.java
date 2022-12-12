/*
 *  Copyright (c) 2021, Ben Fortuna
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

package net.fortuna.ical4j.filter;

import net.fortuna.ical4j.data.DefaultParameterFactorySupplier;
import net.fortuna.ical4j.data.DefaultPropertyFactorySupplier;
import net.fortuna.ical4j.filter.expression.BinaryExpression;
import net.fortuna.ical4j.filter.expression.LiteralExpression;
import net.fortuna.ical4j.filter.expression.TargetExpression;
import net.fortuna.ical4j.filter.expression.UnaryExpression;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterBuilder;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractFilter<T> implements PredicateFactory<T> {

    protected <V> V literal(FilterExpression expression) {
        if (expression instanceof BinaryExpression && ((BinaryExpression) expression).right instanceof LiteralExpression) {
            return ((LiteralExpression<V>) ((BinaryExpression) expression).right).getValue();
        }
        throw new IllegalArgumentException("Not a valid filter");
    }

    protected FilterTarget target(FilterExpression expression) {
        if (expression instanceof UnaryExpression
                && ((UnaryExpression) expression).operand instanceof TargetExpression) {
            return ((TargetExpression) ((UnaryExpression) expression).operand).getValue();
        } else if (expression instanceof BinaryExpression
                && ((BinaryExpression) expression).left instanceof TargetExpression) {
            return ((TargetExpression) ((BinaryExpression) expression).left).getValue();
        }
        throw new IllegalArgumentException("Not a valid filter");
    }

    /**
     * Build a property instance from an expression.
     * @param expression
     * @return
     */
    protected Property property(FilterExpression expression) {
        if (expression instanceof UnaryExpression) {
            return property((UnaryExpression) expression);
        } else if (expression instanceof BinaryExpression) {
            return property((BinaryExpression) expression);
        }
        throw new IllegalArgumentException("Not a valid filter");
    }

    /**
     * Build a property instance from an expression.
     * @param expression
     * @return
     */
    protected Property property(UnaryExpression expression) {
        return property(target(expression));
    }

    /**
     * Build a property instance from an expression.
     * @param expression
     * @return
     */
    protected Property property(BinaryExpression expression) {
        // todo: support for function, integer, etc. on right side (currently only supports string)
        return property(target(expression), literal(expression));
    }

    /**
     * Build a property list from an expression.
     * @param expression
     * @return
     */
    protected List<Comparable<Property>> properties(BinaryExpression expression) {
        FilterTarget operand = target(expression);
        List<String> literal = literal(expression);
        return literal.stream().map(l -> property(operand, l)).collect(Collectors.toList());
    }

    /**
     * Build a property instance from a filter specification.
     * @param operand
     * @return
     */
    protected Property property(FilterTarget operand) {
        PropertyBuilder spec = new PropertyBuilder(new DefaultPropertyFactorySupplier().get()).name(operand.getName());
        if (operand.getValue().isPresent()) {
            spec.value(operand.getValue().get());
        } else {
            spec.value("");
        }
        operand.getAttributes().forEach(a -> spec.parameter(parameter(a)));
        try {
            return spec.build();
        } catch (ParseException | IOException | URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Build a property instance from a filter specification and value string.
     * @param operand
     * @param value
     * @return
     */
    protected Property property(FilterTarget operand, String value) {
        PropertyBuilder spec = new PropertyBuilder(new DefaultPropertyFactorySupplier().get()).name(operand.getName());
        if (value != null) {
            spec.value(value);
        } else {
            spec.value("");
        }
        operand.getAttributes().forEach(a -> spec.parameter(parameter(a)));
        try {
            return spec.build();
        } catch (ParseException | IOException | URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Build a parameter instance from an expression.
     * @param expression
     * @return
     */
    protected Parameter parameter(UnaryExpression expression) {
        FilterTarget specification = target(expression);
        if (specification.getValue().isPresent()) {
            return parameter(specification.getName(), specification.getValue().get());
        } else {
            return parameter(target(expression).getName(), null);
        }
    }

    /**
     * Build a parameter instance from an expression.
     * @param expression
     * @return
     */
    protected Parameter parameter(BinaryExpression expression) {
        return parameter(target(expression).getName(), literal(expression));
    }

    /**
     * Build a list of parameters from an expression.
     * @param expression
     * @return
     */
    protected List<Comparable<Parameter>> parameters(BinaryExpression expression) {
        // only applicable for operand expressions..
        FilterTarget specification = target(expression);
        List<String> literal = literal(expression);
        return literal.stream().map(l -> parameter(specification.getName(), l)).collect(Collectors.toList());
    }

    protected Parameter parameter(FilterTarget.Attribute a) {
        try {
            return new ParameterBuilder(new DefaultParameterFactorySupplier().get())
                    .name(a.getName()).value(a.getValue()).build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Build a parameter from a name and value string.
     * @param name
     * @param value
     * @return a parameter instance
     */
    protected Parameter parameter(String name, String value) {
        try {
            return new ParameterBuilder(new DefaultParameterFactorySupplier().get()).name(name).value(value).build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
