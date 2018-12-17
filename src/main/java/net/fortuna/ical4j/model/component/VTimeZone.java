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
import net.fortuna.ical4j.validate.PropertyValidator;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.validate.Validator;
import net.fortuna.ical4j.validate.component.VTimeZoneITIPValidator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

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
public class VTimeZone extends CalendarComponent {

    private static final long serialVersionUID = 5629679741050917815L;

    private final Validator itipValidator = new VTimeZoneITIPValidator();
    
    private ComponentList<Observance> observances;

    /**
     * Default constructor.
     */
    public VTimeZone() {
        super(VTIMEZONE);
        this.observances = new ComponentList<Observance>();
    }

    /**
     * Constructs a new instance containing the specified properties.
     * @param properties a list of properties
     */
    public VTimeZone(final PropertyList properties) {
        super(VTIMEZONE, properties);
        this.observances = new ComponentList<Observance>();
    }

    /**
     * Constructs a new vtimezone component with no properties and the specified list of type components.
     * @param observances a list of type components
     */
    public VTimeZone(final ComponentList<Observance> observances) {
        super(VTIMEZONE);
        this.observances = observances;
    }

    /**
     * Constructor.
     * @param properties a list of properties
     * @param observances a list of timezone types
     */
    public VTimeZone(final PropertyList properties,
            final ComponentList<Observance> observances) {
        super(VTIMEZONE, properties);
        this.observances = observances;
    }

    /**
     * {@inheritDoc}
     */
    public final String toString() {
        return BEGIN +
                ':' +
                getName() +
                Strings.LINE_SEPARATOR +
                getProperties() +
                observances +
                END +
                ':' +
                getName() +
                Strings.LINE_SEPARATOR;
    }

    /**
     * {@inheritDoc}
     */
    public final void validate(final boolean recurse)
            throws ValidationException {

        /*
         * ; 'tzid' is required, but MUST NOT occur more ; than once tzid /
         */
        PropertyValidator.getInstance().assertOne(Property.TZID,
                getProperties());

        /*
         * ; 'last-mod' and 'tzurl' are optional, but MUST NOT occur more than once last-mod / tzurl /
         */
        PropertyValidator.getInstance().assertOneOrLess(Property.LAST_MODIFIED,
                getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.TZURL,
                getProperties());

        /*
         * ; one of 'standardc' or 'daylightc' MUST occur ..; and each MAY occur more than once. standardc / daylightc /
         */
        if (getObservances().getComponent(Observance.STANDARD) == null
                && getObservances().getComponent(Observance.DAYLIGHT) == null) {
            throw new ValidationException("Sub-components ["
                    + Observance.STANDARD + "," + Observance.DAYLIGHT
                    + "] must be specified at least once");
        }

        for (final Observance observance : getObservances()) {
            observance.validate(recurse);
        }
        
        /*
         * ; the following is optional, ; and MAY occur more than once x-prop
         */

        if (recurse) {
            validateProperties();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Validator getValidator(Method method) {
        return itipValidator;
    }

    /**
     * @return Returns the types.
     */
    public final ComponentList<Observance> getObservances() {
        return observances;
    }

    /**
     * Returns the latest applicable timezone observance for the specified date.
     * @param date the latest possible date for a timezone observance onset
     * @return the latest applicable timezone observance for the specified date or null if there are no applicable
     * observances
     */
    public final Observance getApplicableObservance(final Date date) {
        Observance latestObservance = null;
        Date latestOnset = null;
        for (final Observance observance : getObservances()) {
            final Date onset = observance.getLatestOnset(date);
            if (latestOnset == null
                    || (onset != null && onset.after(latestOnset))) {
                latestOnset = onset;
                latestObservance = observance;
            }
        }
        return latestObservance;
    }

    /**
     * @return the mandatory timezone identifier property
     */
    public final TzId getTimeZoneId() {
        return (TzId) getProperty(Property.TZID);
    }

    /**
     * @return the optional last-modified property
     */
    public final LastModified getLastModified() {
        return (LastModified) getProperty(Property.LAST_MODIFIED);
    }

    /**
     * @return the optional timezone url property
     */
    public final TzUrl getTimeZoneUrl() {
        return (TzUrl) getProperty(Property.TZURL);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(final Object arg0) {
        if (arg0 instanceof VTimeZone) {
            return super.equals(arg0)
                    && ObjectUtils.equals(observances, ((VTimeZone) arg0)
                            .getObservances());
        }
        return super.equals(arg0);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).append(getProperties())
                .append(getObservances()).toHashCode();
    }

    /**
     * Overrides default copy method to add support for copying observance sub-components.
     * @return a copy of the instance
     * @throws ParseException where an error occurs parsing data
     * @throws IOException where an error occurs reading data
     * @throws URISyntaxException where an invalid URI is encountered
     * @see net.fortuna.ical4j.model.Component#copy()
     */
    public Component copy() throws ParseException, IOException, URISyntaxException {
        final VTimeZone copy = (VTimeZone) super.copy();
        copy.observances = new ComponentList<Observance>(observances);
        return copy;
    }

    @ComponentFactory.Service
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

        @Override
        public VTimeZone createComponent(PropertyList properties, ComponentList subComponents) {
            return new VTimeZone(properties, subComponents);
        }
    }
}
