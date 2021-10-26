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

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.component.AvailableValidator;

/**
 * $Id$ [05-Apr-2004]
 * <p/>
 * Defines an iCalendar Available component.
 * <p/>
 * <pre>
 *
 *       availablec  = &quot;BEGIN&quot; &quot;:&quot; &quot;AVAILABLE&quot; CRLF
 *
 *                    availableprop
 *
 *                    &quot;END&quot; &quot;:&quot; &quot;AVAILABLE&quot; CRLF
 *
 * availableprop  = *(
 *
 * ; the following are REQUIRED,
 * ; but MUST NOT occur more than once
 *
 * dtstamp / dtstart / uid /
 *
 * ; either a 'dtend' or a 'duration' is required
 * ; in a 'availableprop', but 'dtend' and
 * ; 'duration' MUST NOT occur in the same
 * ; 'availableprop', and each MUST NOT occur more
 * ; than once
 *
 * dtend / duration /
 *
 * ; the following are OPTIONAL,
 * ; but MUST NOT occur more than once
 *
 * created / last-mod / recurid / rrule /
 * summary /
 *
 * ; the following are OPTIONAL,
 * ; and MAY occur more than once
 *
 * categories / comment / contact / exdate /
 * rdate / x-prop
 *
 * )
 * </pre>
 *
 * @author Ben Fortuna
 * @author Mike Douglass
 */
public class Available extends Component {

    private static final long serialVersionUID = -2494710612002978763L;

    /**
     * Default constructor.
     */
    public Available() {
        super(AVAILABLE);
    }

    /**
     * Constructor.
     *
     * @param properties a list of properties
     */
    public Available(final PropertyList<Property> properties) {
        super(AVAILABLE, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(final boolean recurse) throws ValidationException {
        new AvailableValidator().validate(this);

        if (recurse) {
            validateProperties();
        }
    }

    public static class Factory extends Content.Factory implements ComponentFactory<Available> {

        public Factory() {
            super(AVAILABLE);
        }

        @Override
        public Available createComponent() {
            return new Available();
        }

        @Override
        public Available createComponent(PropertyList properties) {
            return new Available(properties);
        }
    }
}
