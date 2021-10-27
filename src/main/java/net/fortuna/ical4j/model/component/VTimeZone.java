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
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzUrl;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ComponentValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.ValidationRule;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.component.VTimeZoneValidator;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.fortuna.ical4j.model.Property.*;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.One;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.OneOrLess;

/**
 * $Id$ [Apr 5, 2004]
 *
 * Defines an iCalendar VTIMEZONE component.
 * 
 * <pre>
 *       4.6.5 Time Zone Component
 *  
 *          Component Name: VTIMEZONE
 *  
 *          Purpose: Provide a grouping of component properties that defines a
 *          time zone.
 *  
 *          Formal Definition: A &quot;VTIMEZONE&quot; calendar component is defined by the
 *          following notation:
 *  
 *            timezonec  = &quot;BEGIN&quot; &quot;:&quot; &quot;VTIMEZONE&quot; CRLF
 *  
 *                         2*(
 *  
 *                         ; 'tzid' is required, but MUST NOT occur more
 *                         ; than once
 *  
 *                       tzid /
 *  
 *                         ; 'last-mod' and 'tzurl' are optional,
 *                       but MUST NOT occur more than once
 *  
 *                       last-mod / tzurl /
 *  
 *                         ; one of 'standardc' or 'daylightc' MUST occur
 *                       ..; and each MAY occur more than once.
 *  
 *                       standardc / daylightc /
 *  
 *                       ; the following is optional,
 *                       ; and MAY occur more than once
 *  
 *                         x-prop
 *  
 *                         )
 *  
 *                         &quot;END&quot; &quot;:&quot; &quot;VTIMEZONE&quot; CRLF
 *  
 *            standardc  = &quot;BEGIN&quot; &quot;:&quot; &quot;STANDARD&quot; CRLF
 *  
 *                         tzprop
 *  
 *                         &quot;END&quot; &quot;:&quot; &quot;STANDARD&quot; CRLF
 *  
 *            daylightc  = &quot;BEGIN&quot; &quot;:&quot; &quot;DAYLIGHT&quot; CRLF
 *  
 *                         tzprop
 *  
 *                         &quot;END&quot; &quot;:&quot; &quot;DAYLIGHT&quot; CRLF
 *  
 *            tzprop     = 3*(
 *  
 *                       ; the following are each REQUIRED,
 *                       ; but MUST NOT occur more than once
 *  
 *                       dtstart / tzoffsetto / tzoffsetfrom /
 *  
 *                       ; the following are optional,
 *                       ; and MAY occur more than once
 *  
 *                       comment / rdate / rrule / tzname / x-prop
 *  
 *                       )
 * </pre>
 * 
 * @author Ben Fortuna
 */
public class VTimeZone extends CalendarComponent implements ComponentContainer<Observance> {

    private static final long serialVersionUID = 5629679741050917815L;

    private static final Validator<VTimeZone> itipValidator = new VTimeZoneValidator();

    private static final Validator<VTimeZone> validator = new ComponentValidator<>(
            new ValidationRule<>(One, TZID),
            new ValidationRule<>(OneOrLess, LAST_MODIFIED, TZURL)
    );

    /**
     * Default constructor.
     */
    public VTimeZone() {
        super(VTIMEZONE);
    }

    /**
     * Constructs a new instance containing the specified properties.
     * @param properties a list of properties
     */
    public VTimeZone(final PropertyList properties) {
        super(VTIMEZONE, properties);
    }

    /**
     * Constructs a new vtimezone component with no properties and the specified list of type components.
     * @param observances a list of type components
     */
    public VTimeZone(final ComponentList<Observance> observances) {
        super(VTIMEZONE);
    }

    /**
     * Constructor.
     * @param properties a list of properties
     * @param observances a list of timezone types
     */
    public VTimeZone(final PropertyList properties, final ComponentList<Observance> observances) {
        super(VTIMEZONE, properties, observances);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return BEGIN +
                ':' +
                getName() +
                Strings.LINE_SEPARATOR +
                getProperties() +
                getObservances() +
                END +
                ':' +
                getName() +
                Strings.LINE_SEPARATOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(final boolean recurse) throws ValidationException {
        new VTimeZoneValidator().validate(this);
        if (recurse) {
            validateProperties();
        }
    }

    @Override
    public void validate(Method method) throws ValidationException {
        itipValidator.validate(this);
    }

    /**
     * @return Returns the types.
     */
    public final ComponentList<Observance> getObservances() {
        return (ComponentList<Observance>) components;
    }

    @Override
    public ComponentList<Observance> getComponents() {
        return (ComponentList<Observance>) components;
    }

    @Override
    public void setComponents(ComponentList<Observance> components) {
        this.components = components;
    }

    /**
     * Returns the latest applicable timezone observance for the specified date.
     * @param date the latest possible date for a timezone observance onset
     * @return the latest applicable timezone observance for the specified date or null if there are no applicable
     * observances
     */
    public final Observance getApplicableObservance(final Temporal date) {
        return getApplicableObservance(date, getObservances().getAll());
    }

    /**
     * Returns the latest applicable timezone observance for the specified date.
     * @param date the latest possible date for a timezone observance onset
     * @param observances a list of observances to choose from
     * @return the latest applicable timezone observance for the specified date or null if there are no applicable
     * observances
     */
    public static Observance getApplicableObservance(final Temporal date, List<Observance> observances) {
        Observance latestObservance = null;
        OffsetDateTime latestOnset = null;
        for (final Observance observance : observances) {
            final OffsetDateTime onset = observance.getLatestOnset(date);
            if (latestOnset == null || (onset != null && onset.isAfter(latestOnset))) {
                latestOnset = onset;
                latestObservance = observance;
            }
        }
        return latestObservance;
    }

    /**
     * @return the mandatory timezone identifier property
     * @deprecated use {@link VTimeZone#getProperty(String)}
     */
    @Deprecated
    public final Optional<TzId> getTimeZoneId() {
        return getProperty(TZID);
    }

    /**
     * @return the optional last-modified property
     * @deprecated use {@link VTimeZone#getProperty(String)}
     */
    @Deprecated
    public final Optional<LastModified> getLastModified() {
        return getProperty(LAST_MODIFIED);
    }

    /**
     * @return the optional timezone url property
     * @deprecated use {@link VTimeZone#getProperty(String)}
     */
    @Deprecated
    public final Optional<TzUrl> getTimeZoneUrl() {
        return getProperty(TZURL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object arg0) {
        if (arg0 instanceof VTimeZone) {
            return super.equals(arg0)
                    && Objects.equals(getObservances(), ((VTimeZone) arg0)
                            .getObservances());
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getProperties())
                .append(getObservances()).toHashCode();
    }

    @Override
    protected ComponentFactory<VTimeZone> newFactory() {
        return new Factory();
    }

    public static class Factory extends Content.Factory implements ComponentFactory<VTimeZone> {

        public Factory() {
            super(VTIMEZONE);
        }

        @Override
        public VTimeZone createComponent() {
            return new VTimeZone();
        }

        @Override
        public VTimeZone createComponent(PropertyList properties) {
            return new VTimeZone(properties);
        }

        @Override @SuppressWarnings("unchecked")
        public VTimeZone createComponent(PropertyList properties, ComponentList<?> subComponents) {
            return new VTimeZone(properties, (ComponentList<Observance>) subComponents);
        }
    }
}
