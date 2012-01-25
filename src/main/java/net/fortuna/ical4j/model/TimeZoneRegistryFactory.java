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

import net.fortuna.ical4j.util.Configurator;

/**
 * $Id$
 *
 * Created on 18/09/2005
 *
 * Provides access to the configured <code>TimeZoneRegistry</code> instance.
 * Alternative factory implementations may be specified via the following
 * system property:
 * <pre>net.fortuna.ical4j.timezone.registry=&lt;factory_class_name&gt;</pre>
 * @author Ben Fortuna
 */
public abstract class TimeZoneRegistryFactory {
    
    /**
     * The system property used to specify an alternate
     * <code>TimeZoneRegistryFactory</code> implementation.
     */
    public static final String KEY_FACTORY_CLASS = "net.fortuna.ical4j.timezone.registry";

    private static TimeZoneRegistryFactory instance;
    static {
        try {
            final Class factoryClass = Class.forName(Configurator.getProperty(KEY_FACTORY_CLASS));
            instance = (TimeZoneRegistryFactory) factoryClass.newInstance();
        }
        catch (Exception e) {
            instance = new DefaultTimeZoneRegistryFactory();
        }
    }
    
    /**
     * @return a shared factory instance
     */
    public static TimeZoneRegistryFactory getInstance() {
        return instance;
    }
    
    /**
     * Returns a new instance of the configured <code>TimeZoneRegistry</code>.
     * @return a timezone registry instance
     */
    public abstract TimeZoneRegistry createRegistry();
}
