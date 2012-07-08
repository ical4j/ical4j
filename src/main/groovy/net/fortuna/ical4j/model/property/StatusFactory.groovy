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
class StatusFactory extends AbstractPropertyFactory {

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        Status instance
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, Status.class)) {
            instance = (Status) value
        }
        else {
            String instanceValue = attributes.remove('value')
            if (instanceValue != null) {
				switch (instanceValue) {
					case Status.VEVENT_CANCELLED.value:
						instance = Status.VEVENT_CANCELLED
						break
					case Status.VEVENT_CONFIRMED.value:
						instance = Status.VEVENT_CONFIRMED
						break
					case Status.VEVENT_TENTATIVE.value:
                    	instance = Status.VEVENT_TENTATIVE
						break
					case Status.VJOURNAL_CANCELLED.value:
                    	instance = Status.VJOURNAL_CANCELLED
						break
					case Status.VJOURNAL_DRAFT.value:
                    	instance = Status.VJOURNAL_DRAFT
						break
					case Status.VJOURNAL_FINAL.value:
                    	instance = Status.VJOURNAL_FINAL
						break
					case Status.VTODO_CANCELLED.value:
                    	instance = Status.VTODO_CANCELLED
						break
					case Status.VTODO_COMPLETED.value:
                    	instance = Status.VTODO_COMPLETED
						break
					case Status.VTODO_IN_PROCESS.value:
                    	instance = Status.VTODO_IN_PROCESS
						break
					case Status.VTODO_NEEDS_ACTION.value:
                    	instance = Status.VTODO_NEEDS_ACTION
						break
					default:
                    	attributes.put('value', instanceValue)
						instance = super.newInstance(builder, name, value, attributes)
                }
            }
            else {
				switch (value) {
                	case Status.VEVENT_CANCELLED.value:
                    	instance = Status.VEVENT_CANCELLED
						break
					case Status.VEVENT_CONFIRMED.value:
	                    instance = Status.VEVENT_CONFIRMED
						break
	                case Status.VEVENT_TENTATIVE.value:
	                    instance = Status.VEVENT_TENTATIVE
						break
	                case Status.VJOURNAL_CANCELLED.value:
	                    instance = Status.VJOURNAL_CANCELLED
						break
	                case Status.VJOURNAL_DRAFT.value:
	                    instance = Status.VJOURNAL_DRAFT
						break
	                case Status.VJOURNAL_FINAL.value:
	                    instance = Status.VJOURNAL_FINAL
						break
	                case Status.VTODO_CANCELLED.value:
	                    instance = Status.VTODO_CANCELLED
						break
	                case Status.VTODO_COMPLETED.value:
	                    instance = Status.VTODO_COMPLETED
						break
	                case Status.VTODO_IN_PROCESS.value:
	                    instance = Status.VTODO_IN_PROCESS
						break
	                case Status.VTODO_NEEDS_ACTION.value:
	                    instance = Status.VTODO_NEEDS_ACTION
						break
	                default:
	                    instance = super.newInstance(builder, name, value, attributes)
				}
            }
        }
        return instance
    }
    
    protected Object newInstance(ParameterList parameters, String value) {
        return new Status(parameters, value)
    }
}
