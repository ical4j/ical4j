/*
 * $Id$ [Apr 5, 2004]
 *
 * Copyright (c) 2004, Ben Fortuna
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
package net.fortuna.ical4j.model.component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.util.PropertyValidator;

/**
 * Defines an iCalendar VFREEBUSY component.
 * 
 * <pre>
 *   4.6.4 Free/Busy Component
 *   
 *      Component Name: VFREEBUSY
 *   
 *      Purpose: Provide a grouping of component properties that describe
 *      either a request for free/busy time, describe a response to a request
 *      for free/busy time or describe a published set of busy time.
 *   
 *      Formal Definition: A "VFREEBUSY" calendar component is defined by the
 *      following notation:
 *   
 *        freebusyc  = "BEGIN" ":" "VFREEBUSY" CRLF
 *                     fbprop
 *                     "END" ":" "VFREEBUSY" CRLF
 *   
 *        fbprop     = *(
 *   
 *                   ; the following are optional,
 *                   ; but MUST NOT occur more than once
 *   
 *                   contact / dtstart / dtend / duration / dtstamp /
 *                   organizer / uid / url /
 *   
 *                   ; the following are optional,
 *                   ; and MAY occur more than once
 *   
 *                   attendee / comment / freebusy / rstatus / x-prop
 *   
 *                   )
 * </pre>
 *
 * If you want to check that a time slot (identified by startDate and endDate)
 * is not marked as busy in a calendar you could do something like this:
 *
 * <pre><code>
 *  for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
 *      Component component = (Component) i.next();
 *      if (component instanceof VFreeBusy) {
 *          for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
 *              Property property = (Property) j.next();
 *              if (property instanceof FreeBusy
 *                  && FbType.BUSY.equals(property.getParameters().getParameter(Parameter.FBTYPE)) {
 *                      for (Iterator k = ((FreeBusy) property).getPeriods().iterator(); k.hasNext();) {
 *                          Period period = (Period) k.next();
 *                          if (startDate.after(period.getStart()) && startDate.before(period.getEnd())) {
 *                              // conflict..
 *                              return false;
 *                          }
 *                          else if (endDate.after(period.getStart()) && endDate.before(period.getEnd())) {
 *                              // conflict..
 *                              return false;
 *                          }
 *                      }
 *              }
 *          }
 *      }
 *  }
 * </code></pre>
 *
 * @author Ben Fortuna
 */
public class VFreeBusy extends Component {

    /**
     * Default constructor.
     */
    public VFreeBusy() {
        super(VFREEBUSY);
    }

    /**
     * Constructor.
     * @param properties a list of properties
     */
    public VFreeBusy(final PropertyList properties) {
        super(VFREEBUSY, properties);
    }
    
    /**
     * Constructs a new VFreeBusy instance with the specified
     * start and end boundaries. This constructor should be used
     * for requesting Free/Busy time for a specified period.
     * @param startDate the starting boundary for the VFreeBusy
     * @param endDate the ending boundary for the VFreeBusy
     */
    public VFreeBusy(final Date startDate, final Date endDate) {
        this();
        getProperties().add(createFbStart(startDate));
        getProperties().add(createFbEnd(endDate));
        getProperties().add(new DtStamp(new Date()));
    }
    
    /**
     * Constructs a new VFREEBUSY instance initialising busy
     * time according to the specified list of components. This constructor
     * should be used for publishing busy time for a set period based on the
     * specified list of components.
     * @param components a component list used to initialise busy time
     */
    public VFreeBusy(final Date startDate, final Date endDate, final ComponentList components) {
        this(startDate, endDate);
        for (Iterator i = components.iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            FreeBusy fb = createFreeBusy(startDate, endDate, component);
            if (fb != null && !fb.getPeriods().isEmpty()) {
                getProperties().add(fb);
            }
        }
    }
    
    /**
     * Create a new DTSTART instance in accordance with VFREEBUSY requirements.
     * @param start
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    private DtStart createFbStart(final Date startDate) {
        DtStart fbStart = new DtStart(startDate);
        // dtstart MUST be specified in UTC..
        fbStart.setUtc(true);
        return fbStart;
    }
    
    /**
     * Create a new DTEND instance in accordance with VFREEBUSY requirements.
     * @param end
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    private DtEnd createFbEnd(final Date endDate) {
        DtEnd fbEnd = new DtEnd(endDate);
        // dtend MUST be specified in UTC..
        fbEnd.setUtc(true);
        return fbEnd;
    }
    
    /**
     * Create a FREEBUSY property representing the busy time for the specified
     * component. If the component is not applicable to FREEBUSY time, or if the
     * component is outside the bounds of the start and end dates, null is
     * returned. If no valid busy periods are identified in the component an
     * empty FREEBUSY property is returned (i.e. empty period list).
     * @param component a component to base the FREEBUSY property on
     * @return a FreeBusy instance or null if the component is not applicable
     */
    private FreeBusy createFreeBusy(final Date startDate, final Date endDate, final Component component) {
        // if transparent don't specify as busy time..
        Transp transparent = (Transp) component.getProperties().getProperty(Property.TRANSP);
        if (transparent != null && Transp.TRANSPARENT.equals(transparent.getValue())) {
            return null;
        }
        DtStart start = (DtStart) component.getProperties().getProperty(Property.DTSTART);
        DtEnd end = (DtEnd) component.getProperties().getProperty(Property.DTEND);
        Duration duration = (Duration) component.getProperties().getProperty(Property.DURATION);
        // if no start date specified return..
        if (start == null) {
            return null;
        }
        // if outside bounds of start/end dates return..
        if (start.getTime().after(endDate) || (end != null && end.getTime().before(startDate))) {
            return null;
        }
        // if start/end specified as anniversary-type (i.e. uses DATE values rather
        // than DATE-TIME), don't specify as busy time..
        if (start.getParameters().getParameter(Parameter.VALUE) != null
                && Value.DATE.equals(start.getParameters().getParameter(Parameter.VALUE).getValue())) {
            return null;
        }
        // populate busy time..
        FreeBusy fb = new FreeBusy();
        PropertyList rrules = component.getProperties().getProperties(Property.RRULE);
        if (!rrules.isEmpty()) {
            for (Iterator i = rrules.iterator(); i.hasNext();) {
                RRule rrule = (RRule) i.next();
                // TODO: create periods representing all rrules..
            }
        }
        else {
            // create default busy time from dtstart/dtend..
            if (end != null) {
                fb.getPeriods().add(new Period(start.getTime(), end.getTime()));
            }
            else if (duration != null) {
                fb.getPeriods().add(new Period(start.getTime(), duration.getDuration()));
            }
        }
        return fb;
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(final boolean recurse) throws ValidationException {

        /*
                ; the following are optional,
                ; but MUST NOT occur more than once

                contact / dtstart / dtend / duration / dtstamp /
                organizer / uid / url /
         */
        PropertyValidator.getInstance().validateOneOrLess(Property.CONTACT,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.DTSTART,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.DTEND,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.DURATION,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.DTSTAMP,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.ORGANIZER,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.UID,
                getProperties());
        PropertyValidator.getInstance().validateOneOrLess(Property.URL,
                getProperties());

        /*

                ; the following are optional,
                ; and MAY occur more than once

                attendee / comment / freebusy / rstatus / x-prop
         */
        
        // DtEnd value must be later in time that DtStart..
        DtStart dtStart = (DtStart) getProperties().getProperty(Property.DTSTART);
        DtEnd dtEnd = (DtEnd) getProperties().getProperty(Property.DTEND);
        if (dtStart != null && dtEnd != null && !dtStart.getTime().before(dtEnd.getTime())) {
            throw new ValidationException("Property [" + Property.DTEND
                            + "] must be later in time than [" + Property.DTSTART + "]");
        }

        if (recurse) {
            validateProperties();
        }
    }
}