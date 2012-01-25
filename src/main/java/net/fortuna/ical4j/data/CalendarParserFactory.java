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
package net.fortuna.ical4j.data;

import net.fortuna.ical4j.util.Configurator;

/**
 * <pre>
 * $Id$
 *
 * Created on 08/02/2007
 * </pre>
 *
 * Provides access to the configured {@link CalendarParser} instance. Alternative factory implementations may be
 * specified via the following system property:
 * 
 * <pre>
 * net.fortuna.ical4j.parser=&lt;factory_class_name&gt;
 * </pre>
 * 
 * @author Ben Fortuna
 */
public abstract class CalendarParserFactory {

    /**
     * The system property used to specify an alternate {@link CalendarParser} implementation.
     */
    public static final String KEY_FACTORY_CLASS = "net.fortuna.ical4j.parser";

    private static CalendarParserFactory instance;
    static {
        try {
            final Class factoryClass = Class.forName(
                    Configurator.getProperty(KEY_FACTORY_CLASS));
            instance = (CalendarParserFactory) factoryClass.newInstance();
        }
        catch (Exception e) {
            instance = new DefaultCalendarParserFactory();
        }
    }

    /**
     * @return a shared factory instance
     */
    public static CalendarParserFactory getInstance() {
        return instance;
    }

    /**
     * Returns a new instance of the configured {@link CalendarParser}.
     * @return a calendar parser instance
     */
    public abstract CalendarParser createParser();

}
