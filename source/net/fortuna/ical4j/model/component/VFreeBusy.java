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

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.PropertyValidator;

/**
 * Defines an iCalendar VFREEBUSY component.
 *
 * If you want to check that a time slot (identified by startDate and endDate)
 * is not marked as busy in a calendar you could do something like this:
 *
 * <code>
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
 * </code>
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

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.model.Component#validate(boolean)
     */
    public final void validate(boolean recurse) throws ValidationException {

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

        if (recurse) {
            validateProperties();
        }
    }
}