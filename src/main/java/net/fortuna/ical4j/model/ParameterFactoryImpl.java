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
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.ServiceLoader;

/**
 * A factory for creating iCalendar parameters.
 * <p/>
 * $Id $
 * <p/>
 * [05-Apr-2004]
 *
 * @author Ben Fortuna
 */
@Deprecated
public class ParameterFactoryImpl extends AbstractContentFactory<ParameterFactory<? extends Parameter>> {

    private static final long serialVersionUID = -4034423507432249165L;

    protected ParameterFactoryImpl() {
        super(ServiceLoader.load(ParameterFactory.class, ParameterFactory.class.getClassLoader()),
                CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING));
    }

    @Override
    protected boolean factorySupports(ParameterFactory factory, String key) {
        return factory.supports(key);
    }

    /**
     * Creates a parameter.
     *
     * @param name  name of the parameter
     * @param value a parameter value
     * @return a component
     * @throws URISyntaxException thrown when the specified string is not a valid representation of a URI for selected
     *                            parameters
     */
    public Parameter createParameter(final String name, final String value)
            throws URISyntaxException {
        final ParameterFactory factory = getFactory(name);
        Parameter parameter;
        if (factory != null) {
            parameter = factory.createParameter(value);
        } else if (isExperimentalName(name)) {
            parameter = new XParameter(name, value);
        } else if (allowIllegalNames()) {
            parameter = new XParameter(name, value);
        } else {
            throw new IllegalArgumentException(String.format("Unsupported parameter name: %s", name));
        }
        return parameter;
    }

    /**
     * @param name
     * @return
     */
    private boolean isExperimentalName(final String name) {
        return name.startsWith(Parameter.EXPERIMENTAL_PREFIX)
                && name.length() > Parameter.EXPERIMENTAL_PREFIX.length();
    }
    
    /**
     * Needed for initializing the transient member after deserializing a <code>Calendar</code>
     * 
     * @param in
     * 
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.factoryLoader = ServiceLoader.load(ParameterFactory.class, ParameterFactory.class.getClassLoader());
    }
}
