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

import net.fortuna.ical4j.model.PropertyList;

/**
 * $Id$ [05-Apr-2004]
 *
 * Defines an iCalendar standard timezone observance component.
 *
 * <pre>
 *
 *       standardc  = &quot;BEGIN&quot; &quot;:&quot; &quot;STANDARD&quot; CRLF
 *
 *                    tzprop
 *
 *                    &quot;END&quot; &quot;:&quot; &quot;STANDARD&quot; CRLF
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
public class Standard extends Observance {

    private static final long serialVersionUID = -4750910013406451159L;

    /**
     * Default constructor.
     */
    public Standard() {
        super(STANDARD);
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public Standard(final PropertyList properties) {
        super(STANDARD, properties);
    }
}
