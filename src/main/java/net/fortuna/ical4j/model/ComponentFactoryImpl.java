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

import net.fortuna.ical4j.model.component.XComponent;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ServiceLoader;

/**
 * $Id$ [05-Apr-2004]
 * <p/>
 * A factory for creating iCalendar components. Note that if relaxed parsing is enabled (via specifying the system
 * property: icalj.parsing.relaxed=true) illegal component names are allowed.
 *
 * @author Ben Fortuna
 */
@Deprecated
public final class ComponentFactoryImpl extends AbstractContentFactory<ComponentFactory> {

    /**
     * Constructor made private to prevent instantiation.
     */
    public ComponentFactoryImpl() {
        super(ServiceLoader.load(ComponentFactory.class, ComponentFactory.class.getClassLoader()),
                CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING));
    }

    @Override
    protected boolean factorySupports(ComponentFactory factory, String key) {
        return factory.supports(key);
    }

    /**
     * @param name a component name
     * @return a new component instance of the specified type
     */
    public <T extends Component> T createComponent(final String name) {
        Component component;
        ComponentFactory factory = getFactory(name);
        if (factory != null) {
            component = factory.createComponent();
        } else if (isExperimentalName(name)) {
            component = new XComponent(name);
        } else if (allowIllegalNames()) {
            component = new XComponent(name);
        } else {
            throw new IllegalArgumentException("Unsupported component [" + name + "]");
        }
        return (T) component;
    }

    /**
     * Creates a component.
     *
     * @param name       name of the component
     * @param properties a list of component properties
     * @return a component
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T createComponent(final String name, final PropertyList properties) {
        Component component;
        ComponentFactory factory = getFactory(name);
        if (factory != null) {
            component = factory.createComponent(properties);
        } else if (isExperimentalName(name)) {
            component = new XComponent(name, properties);
        } else if (allowIllegalNames()) {
            component = new XComponent(name, properties);
        } else {
            throw new IllegalArgumentException("Unsupported component [" + name + "]");
        }
        return (T) component;
    }

    /**
     * Creates a component which contains sub-components. Currently the only such component is VTIMEZONE.
     *
     * @param name       name of the component
     * @param properties a list of component properties
     * @param components a list of sub-components (namely standard/daylight timezones)
     * @return a component
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T createComponent(final String name, final PropertyList properties,
                                                   final ComponentList<? extends Component> components) {
        Component component;
        ComponentFactory factory = getFactory(name);
        if (factory != null) {
            component = factory.createComponent(properties, components);
        } else {
            throw new IllegalArgumentException("Unsupported component [" + name + "]");
        }
        return (T) component;
    }

    /**
     * @param name
     * @return
     */
    private boolean isExperimentalName(final String name) {
        return name.startsWith(Component.EXPERIMENTAL_PREFIX)
                && name.length() > Component.EXPERIMENTAL_PREFIX.length();
    }

    /**
     * Needed for initializing the transient member after deserializing a <code>Calendar</code>
     * 
     * @param in
     * 
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.factoryLoader = ServiceLoader.load(ComponentFactory.class, ComponentFactory.class.getClassLoader());
    }
}
