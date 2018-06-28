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

import net.fortuna.ical4j.model.ParameterList

/**
 * $Id$
 *
 * Created on: 02/08/2009
 *
 * @author fortuna
 *
 */
class ClazzFactory extends AbstractPropertyFactory{

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        Clazz clazz
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, Clazz.class)) {
            clazz = (Clazz) value
        }
        else {
            String clazzValue = attributes.remove('value')
            if (clazzValue != null) {
                if (Clazz.CONFIDENTIAL.getValue().equals(clazzValue)) {
                    clazz = Clazz.CONFIDENTIAL
                }
                else if (Clazz.PRIVATE.getValue().equals(clazzValue)) {
                    clazz = Clazz.PRIVATE
                }
                else if (Clazz.PUBLIC.getValue().equals(clazzValue)) {
                    clazz = Clazz.PUBLIC
                }
                else {
                    attributes.put('value', clazzValue)
                    clazz = super.newInstance(builder, name, value, attributes)
                }
            }
            else {
                if (Clazz.CONFIDENTIAL.getValue().equals(value)) {
                    clazz = Clazz.CONFIDENTIAL
                }
                else if (Clazz.PRIVATE.getValue().equals(value)) {
                    clazz = Clazz.PRIVATE
                }
                else if (Clazz.PUBLIC.getValue().equals(value)) {
                    clazz = Clazz.PUBLIC
                }
                else {
                    clazz = super.newInstance(builder, name, value, attributes)
                }
            }
        }
        return clazz
    }
    
    protected Object newInstance(ParameterList parameters, String value) {
        return new Clazz(parameters, value)
    }
}
