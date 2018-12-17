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

import net.fortuna.ical4j.util.CompatibilityHints;
import net.java.sezpoz.Index;
import net.java.sezpoz.IndexItem;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * $Id$
 * <p/>
 * Created on 28/01/2007
 * <p/>
 * Abstract implementation of a content factory.
 *
 * @author Ben Fortuna
 */
public abstract class AbstractContentFactory<T> implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(CalendarDateFormatFactory.class);

    private final Map<String, T> extendedFactories;

    protected transient Index<? extends Annotation, T> factoryLoader;

    /**
     * Default constructor.
     */
    public AbstractContentFactory(Class<? extends Annotation> annotation, Class<T> instanceType) {
        extendedFactories = new HashMap<String, T>();
        this.factoryLoader = Index.load(annotation, instanceType);
    }

    /**
     * Register a non-standard content factory.
     * @deprecated Define extensions in META-INF/services/net.fortuna.ical4j.model.[Type]Factory
     */
    @Deprecated
    protected final void registerExtendedFactory(String key, T factory) {
        extendedFactories.put(key, factory);
    }

    protected abstract boolean factorySupports(T factory, String key);

    /**
     * @param key a factory key
     * @return a factory associated with the specified key, giving preference to
     * standard factories
     * @throws IllegalArgumentException if the specified key is blank
     */
    protected final T getFactory(String key) {
        Validate.notBlank(key, "Invalid factory key: [%s]", key);
        T factory = null;
        for (IndexItem<? extends Annotation, T> candidate : factoryLoader) {
            T instance = null;
            try {
                instance = candidate.instance();
                if (factorySupports(instance, key)) {
                    factory = instance;
                    break;
                }
            } catch (InstantiationException e) {
                LOG.error(String.format("Unable to create factory instance [%s]", candidate.className()), e);
            }
        }
        if (factory == null) {
            factory = extendedFactories.get(key);
        }
        return factory;
    }

    /**
     * @return true if non-standard names are allowed, otherwise false
     */
    protected boolean allowIllegalNames() {
        return CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
    }
}
