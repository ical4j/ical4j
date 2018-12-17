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
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.PropertyList;

/**
 * $Id$ [05-Apr-2004]
 * <p/>
 * Defines an iCalendar daylight savings timezone observance component.
 * <p/>
 * <pre>
 *
 *       daylightc  = &quot;BEGIN&quot; &quot;:&quot; &quot;DAYLIGHT&quot; CRLF
 *
 *                    tzprop
 *
 *                    &quot;END&quot; &quot;:&quot; &quot;DAYLIGHT&quot; CRLF
 *
 *       tzprop     = 3*(
 *
 *                  ; the following are each REQUIRED,
 *                  ; but MUST NOT occur more than once
 *
 *                  dtstart / tzoffsetto / tzoffsetfrom /
 *
 *                  ; the following are optional,
 *                  ; and MAY occur more than once
 *
 *                  comment / rdate / rrule / tzname / x-prop
 *
 *                  )
 * </pre>
 *
 * @author Ben Fortuna
 */
public class Daylight extends Observance {

    private static final long serialVersionUID = -2494710612002978763L;

    /**
     * Default constructor.
     */
    public Daylight() {
        super(DAYLIGHT);
    }

    /**
     * Constructor.
     *
     * @param properties a list of properties
     */
    public Daylight(final PropertyList properties) {
        super(DAYLIGHT, properties);
    }

    @ComponentFactory.Service
    public static class Factory extends Content.Factory implements ComponentFactory<Daylight> {

        public Factory() {
            super(DAYLIGHT);
        }

        @Override
        public Daylight createComponent() {
            return new Daylight();
        }

        @Override
        public Daylight createComponent(PropertyList properties) {
            return new Daylight(properties);
        }

        @Override
        public Daylight createComponent(PropertyList properties, ComponentList subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", DAYLIGHT));
        }
    }
}
