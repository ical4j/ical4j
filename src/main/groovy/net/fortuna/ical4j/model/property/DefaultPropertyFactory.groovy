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
import net.fortuna.ical4j.model.Property

/**
 * @author fortuna
 *
 */
class DefaultPropertyFactory extends AbstractPropertyFactory {

	Class<? extends Property> klass

    Object newInstance(FactoryBuilderSupport builder, name, value, Map attributes) throws InstantiationException,
            IllegalAccessException {
        def property
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, klass)) {
            property = value.copy()
        } else if (attributes['value']) {
			property = super.newInstance(builder, name, attributes.remove('value'), attributes)
        } else {
			property = super.newInstance(builder, name, value, attributes)
        }
        property.prefix = propertyPrefix
        return property
    }
    
    protected Object newInstance(parameters, value) {
		def constructor = klass.getConstructor(ParameterList, String)
        constructor.newInstance(new ParameterList((List) parameters), value)
    }
}
