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
public class StatusFactory extends AbstractPropertyFactory{

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        Status instance
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, Status.class)) {
            instance = (Status) value
        }
        else {
            String instanceValue = attributes.remove('value')
            if (instanceValue != null) {
                if (Status.VEVENT_CANCELLED.getValue().equals(instanceValue)) {
                    instance = Status.VEVENT_CANCELLED
                }
                else if (Status.VEVENT_CONFIRMED.getValue().equals(instanceValue)) {
                    instance = Status.VEVENT_CONFIRMED
                }
                else if (Status.VEVENT_TENTATIVE.getValue().equals(instanceValue)) {
                    instance = Status.VEVENT_TENTATIVE
                }
                else if (Status.VJOURNAL_CANCELLED.getValue().equals(instanceValue)) {
                    instance = Status.VJOURNAL_CANCELLED
                }
                else if (Status.VJOURNAL_DRAFT.getValue().equals(instanceValue)) {
                    instance = Status.VJOURNAL_DRAFT
                }
                else if (Status.VJOURNAL_FINAL.getValue().equals(instanceValue)) {
                    instance = Status.VJOURNAL_FINAL
                }
                else if (Status.VTODO_CANCELLED.getValue().equals(instanceValue)) {
                    instance = Status.VTODO_CANCELLED
                }
                else if (Status.VTODO_COMPLETED.getValue().equals(instanceValue)) {
                    instance = Status.VTODO_COMPLETED
                }
                else if (Status.VTODO_IN_PROCESS.getValue().equals(instanceValue)) {
                    instance = Status.VTODO_IN_PROCESS
                }
                else if (Status.VTODO_NEEDS_ACTION.getValue().equals(instanceValue)) {
                    instance = Status.VTODO_NEEDS_ACTION
                }
                else {
                    attributes.put('value', instanceValue)
                    instance = super.newInstance(builder, name, value, attributes)
                }
            }
            else {
                if (Status.VEVENT_CANCELLED.getValue().equals(value)) {
                    instance = Status.VEVENT_CANCELLED
                }
                else if (Status.VEVENT_CONFIRMED.getValue().equals(value)) {
                    instance = Status.VEVENT_CONFIRMED
                }
                else if (Status.VEVENT_TENTATIVE.getValue().equals(value)) {
                    instance = Status.VEVENT_TENTATIVE
                }
                else if (Status.VJOURNAL_CANCELLED.getValue().equals(value)) {
                    instance = Status.VJOURNAL_CANCELLED
                }
                else if (Status.VJOURNAL_DRAFT.getValue().equals(value)) {
                    instance = Status.VJOURNAL_DRAFT
                }
                else if (Status.VJOURNAL_FINAL.getValue().equals(value)) {
                    instance = Status.VJOURNAL_FINAL
                }
                else if (Status.VTODO_CANCELLED.getValue().equals(value)) {
                    instance = Status.VTODO_CANCELLED
                }
                else if (Status.VTODO_COMPLETED.getValue().equals(value)) {
                    instance = Status.VTODO_COMPLETED
                }
                else if (Status.VTODO_IN_PROCESS.getValue().equals(value)) {
                    instance = Status.VTODO_IN_PROCESS
                }
                else if (Status.VTODO_NEEDS_ACTION.getValue().equals(value)) {
                    instance = Status.VTODO_NEEDS_ACTION
                }
                else {
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
