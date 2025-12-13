package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationResult;

import java.util.stream.Collectors;

/*
 * Copyright (c) 2025, Ben Fortuna
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
public class VStatus extends Component implements Prototype<VStatus> {

    public VStatus() {
        super(VSTATUS);
    }

    public VStatus(PropertyList p) {
        super(VSTATUS, p);
    }

    @Override
    public ValidationResult validate(boolean recurse) throws ValidationException {
        return ValidationResult.EMPTY;
    }

    @Override
    public VStatus copy() {
        return newFactory().createComponent(new PropertyList(getProperties().parallelStream()
                .map(Property::copy).collect(Collectors.toList())));
    }

    @Override
    protected ComponentFactory<VStatus> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements ComponentFactory<VStatus> {
        public Factory() {
            super(VSTATUS);
        }

        @Override
        public VStatus createComponent() {
            return new VStatus();
        }

        @Override
        public VStatus createComponent(PropertyList properties) {
            return new VStatus(properties);
        }
    }
}
