/*
 * $Id$
 *
 * Created on 18/09/2005
 *
 * Copyright (c) 2005, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.VTimeZone;

/**
 * The default implementation of a <code>TimeZoneRegistry</code>. This
 * implementation will search the classpath for applicable VTimeZone definitions
 * used to back the provided TimeZone instances.
 * @author Ben Fortuna
 */
public class TimeZoneRegistryImpl implements TimeZoneRegistry {

    private static Log log = LogFactory.getLog(TimeZoneRegistryImpl.class);
    
    private static TimeZoneRegistryImpl instance = new TimeZoneRegistryImpl();
    
    private Map timezones;
    
    /**
     * Default constructor.
     */
    public TimeZoneRegistryImpl() {
        timezones = new HashMap();
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.TimeZoneRegistry#register(net.fortuna.ical4j.model.TimeZone)
     */
    public final void register(final TimeZone timezone) {
        timezones.put(timezone.getID(), timezone);
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.TimeZoneRegistry#getTimeZone(java.lang.String)
     */
    public final TimeZone getTimeZone(final String id) {
        TimeZone timezone = (TimeZone) timezones.get(id);
        if (timezone == null) {
            try {
                VTimeZone vTimeZone = loadVTimeZone(id);
                timezone = new TimeZone(vTimeZone);
                register(timezone);
            }
            catch (Exception e) {
                log.warn("Error occurred loading VTimeZone", e);
            }
        }
        return timezone;
    }

    /**
     * Loads an existing VTimeZone from the classpath corresponding to the
     * specified Java timezone.
     */
    private static VTimeZone loadVTimeZone(final String id) throws IOException,
            ParserException {
        String resource = "/" + id + ".ics";

        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar = builder.build(VTimeZone.class
                .getResourceAsStream(resource));

        return (VTimeZone) calendar.getComponents().getComponent(
                Component.VTIMEZONE);
    }

    /**
     * @return Returns the instance.
     */
    public static final TimeZoneRegistryImpl getInstance() {
        return instance;
    }
}
