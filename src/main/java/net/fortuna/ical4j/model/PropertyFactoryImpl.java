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

import net.fortuna.ical4j.model.property.XProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ServiceLoader;

/**
 * A factory for creating iCalendar properties. Note that if relaxed parsing is enabled (via specifying the system
 * property: icalj.parsing.relaxed=true) illegal property names are allowed.
 *
 * @author Ben Fortuna
 *         <p/>
 *         $Id$ [05-Apr-2004]
 */
public class PropertyFactoryImpl extends AbstractContentFactory<PropertyFactory> {

    private static final long serialVersionUID = -7174232004486979641L;

    private static PropertyFactoryImpl instance = new PropertyFactoryImpl();

    /**
     * Constructor made private to prevent instantiation.
     */
    protected PropertyFactoryImpl() {
        super(ServiceLoader.load(PropertyFactory.class, PropertyFactory.class.getClassLoader()));
    }

    /**
     * @return Returns the instance.
     */
    public static PropertyFactoryImpl getInstance() {
        return instance;
    }

    @Override
    protected boolean factorySupports(PropertyFactory factory, String key) {
        return factory.supports(key);
    }

    /**
     * {@inheritDoc}
     */
    public Property createProperty(final String name) {
        final PropertyFactory factory = getFactory(name);
        if (factory != null) {
            return factory.createProperty();
        } else if (isExperimentalName(name)) {
            return new XProperty(name);
        } else if (allowIllegalNames()) {
            return new XProperty(name);
        } else {
            throw new IllegalArgumentException("Illegal property [" + name
                    + "]");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Property createProperty(final String name,
                                   final ParameterList parameters, final String value)
            throws IOException, URISyntaxException, ParseException {

        final PropertyFactory factory = getFactory(name);
        if (factory != null) {
            return factory.createProperty(parameters, value);
        } else if (isExperimentalName(name)) {
            return new XProperty(name, parameters, value);
        } else if (allowIllegalNames()) {
            return new XProperty(name, parameters, value);
        } else {
            throw new IllegalArgumentException("Illegal property [" + name
                    + "]");
        }
    }

    /**
     * @param name
     * @return
     */
    private boolean isExperimentalName(final String name) {
        return name.startsWith(Property.EXPERIMENTAL_PREFIX)
                && name.length() > Property.EXPERIMENTAL_PREFIX.length();
    }
    
    /**
     * Needed for initializing the transient member after deserializing a <code>Calendar</code>
     * 
     * @param in
     * 
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.factoryLoader = ServiceLoader.load(PropertyFactory.class, PropertyFactory.class.getClassLoader());
    }
}
