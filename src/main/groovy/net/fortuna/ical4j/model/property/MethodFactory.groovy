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
package net.fortuna.ical4j.model.property

import net.fortuna.ical4j.model.Parameter
import net.fortuna.ical4j.model.ParameterList

/**
 * $Id$
 *
 * Created on: 02/08/2009
 *
 * @author fortuna
 *
 */
public class MethodFactory extends AbstractPropertyFactory{

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        Method instance
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, Method.class)) {
            instance = (Method) value
        }
        else {
            String instanceValue = attributes.remove('value')
            if (instanceValue != null) {
                if (Method.ADD.getValue().equals(instanceValue)) {
                    instance = Method.ADD
                }
                else if (Method.CANCEL.getValue().equals(instanceValue)) {
                    instance = Method.CANCEL
                }
                else if (Method.COUNTER.getValue().equals(instanceValue)) {
                    instance = Method.COUNTER
                }
                else if (Method.DECLINE_COUNTER.getValue().equals(instanceValue)) {
                    instance = Method.DECLINE_COUNTER
                }
                else if (Method.PUBLISH.getValue().equals(instanceValue)) {
                    instance = Method.PUBLISH
                }
                else if (Method.REFRESH.getValue().equals(instanceValue)) {
                    instance = Method.REFRESH
                }
                else if (Method.REPLY.getValue().equals(instanceValue)) {
                    instance = Method.REPLY
                }
                else if (Method.REQUEST.getValue().equals(instanceValue)) {
                    instance = Method.REQUEST
                }
                else {
                    attributes.put('value', instanceValue)
                    instance = super.newInstance(builder, name, value, attributes)
                }
            }
            else {
                if (Method.ADD.getValue().equals(value)) {
                    instance = Method.ADD
                }
                else if (Method.CANCEL.getValue().equals(value)) {
                    instance = Method.CANCEL
                }
                else if (Method.COUNTER.getValue().equals(value)) {
                    instance = Method.COUNTER
                }
                else if (Method.DECLINE_COUNTER.getValue().equals(value)) {
                    instance = Method.DECLINE_COUNTER
                }
                else if (Method.PUBLISH.getValue().equals(value)) {
                    instance = Method.PUBLISH
                }
                else if (Method.REFRESH.getValue().equals(value)) {
                    instance = Method.REFRESH
                }
                else if (Method.REPLY.getValue().equals(value)) {
                    instance = Method.REPLY
                }
                else if (Method.REQUEST.getValue().equals(value)) {
                    instance = Method.REQUEST
                }
                else {
                    instance = super.newInstance(builder, name, value, attributes)
                }
            }
        }
        return instance
    }
    
    protected Object newInstance(ParameterList parameters, String value) {
        return new Method(parameters, value)
    }
}
