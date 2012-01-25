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
package net.fortuna.ical4j.model.parameter

/**
 * @author fortuna
 *
 */
public class PartStatFactory extends AbstractParameterFactory {


    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        PartStat partStat
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, PartStat.class)) {
            partStat = (PartStat) value
        }
        else if (PartStat.ACCEPTED.getValue().equals(value)) {
            partStat = PartStat.ACCEPTED
        }
        else if (PartStat.COMPLETED.getValue().equals(value)) {
            partStat = PartStat.COMPLETED
        }
        else if (PartStat.DECLINED.getValue().equals(value)) {
            partStat = PartStat.DECLINED
        }
        else if (PartStat.DECLINED.getValue().equals(value)) {
            partStat = PartStat.DECLINED
        }
        else if (PartStat.DELEGATED.getValue().equals(value)) {
            partStat = PartStat.DELEGATED
        }
        else if (PartStat.IN_PROCESS.getValue().equals(value)) {
            partStat = PartStat.IN_PROCESS
        }
        else if (PartStat.NEEDS_ACTION.getValue().equals(value)) {
            partStat = PartStat.NEEDS_ACTION
        }
        else if (PartStat.TENTATIVE.getValue().equals(value)) {
            partStat = PartStat.TENTATIVE
        }
        else {
            partStat = new PartStat(value)
        }
        return partStat
    }
}
