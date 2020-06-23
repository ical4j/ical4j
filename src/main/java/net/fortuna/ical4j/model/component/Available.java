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
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Predicate;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

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

    private final Validator<Available> validator = new ComponentValidator<>(
        new ValidationRule<>(One, DTSTART, DTSTAMP, UID),
        new ValidationRule<>(OneOrLess, CREATED, LAST_MODIFIED, RECURRENCE_ID, RRULE, SUMMARY),
        // can't have both DTEND and DURATION..
        new ValidationRule<>(None,
                (Predicate<Available> & Serializable) p->p.getProperties().getFirst(DTEND).isPresent(), DURATION),
        new ValidationRule<>(None,
                (Predicate<Available> & Serializable) p->p.getProperties().getFirst(DURATION).isPresent(), DTEND)
    );

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
    public Available(final PropertyList properties) {
        super(AVAILABLE, properties);
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse) throws ValidationException {

        validator.validate(this);
        /*
         * ; dtstamp / dtstart / uid are required, but MUST NOT occur more than once /
         */

        /*       If specified, the "DTSTART" and "DTEND" properties in
         *      "VAVAILABILITY" components and "AVAILABLE" sub-components MUST be
         *      "DATE-TIME" values specified as either date with UTC time or date
         *      with local time and a time zone reference.
         */
        try {
            if (getProperties().getRequired(DTSTART).getParameters().getFirst(Parameter.VALUE)
                    .equals(Optional.of(Value.DATE))) {
                throw new ValidationException("Property [" + DTSTART + "] must be a " + Value.DATE_TIME);
            }
        } catch (ConstraintViolationException cve) {
            throw new ValidationException("Missing required property", cve);
        }

        /*
         *                ; the following are optional,
         *                ; but MUST NOT occur more than once
         *
         *               created / last-mod / recurid / rrule /
         *               summary /
         */

        /*
         ; either a 'dtend' or a 'duration' is required
         ; in a 'availableprop', but 'dtend' and
         ; 'duration' MUST NOT occur in the same
         ; 'availableprop', and each MUST NOT occur more
         ; than once
         */
        final Optional<DtEnd<?>> end = getProperties().getFirst(DTEND);
        if (end.isPresent()) {
            /* Must be DATE_TIME */
            if (Optional.of(Value.DATE).equals(end.get().getParameters().getFirst(Parameter.VALUE))) {
                throw new ValidationException("Property [" + DTEND + "] must be a " + Value.DATE_TIME);
            }
        }

        /*
         * ; the following are optional, ; and MAY occur more than once
         *               categories / comment / contact / exdate /
         *               rdate / x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }

    @Override
    protected ComponentFactory<Available> newFactory() {
        return new Factory();
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

        @Override
        public Available createComponent(PropertyList properties, ComponentList<?> subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", AVAILABLE));
        }
    }
}
