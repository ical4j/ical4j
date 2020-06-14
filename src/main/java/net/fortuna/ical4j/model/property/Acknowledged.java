/*
 * Aknowledged.java Feb 20, 2014
 * 
 * Copyright (c) 2014 1&1 Internet AG. All rights reserved.
 * 
 * $Id$
 */
package net.fortuna.ical4j.model.property;

import net.fortuna.ical4j.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author corneliu dobrota
 *
 *
 * Property Name:  ACKNOWLEDGED

   Purpose:  This property specifies the UTC date and time at which the
      corresponding alarm was last sent or acknowledged.

   Value Type:  DATE-TIME

   Property Parameters:  IANA and non-standard property parameters can
      be specified on this property.





Daboo                   Expires December 11, 2012              [Page 10]
 
Internet-Draft              VALARM Extensions                  June 2012


   Conformance:  This property can be specified within "VALARM" calendar
      components.

   Description:  This property is used to specify when an alarm was last
      sent or acknowledged.  This allows clients to determine when a
      pending alarm has been acknowledged by a calendar user so that any
      alerts can be dismissed across multiple devices.  It also allows
      clients to track repeating alarms or alarms on recurring events or
      to-dos to ensure that the right number of missed alarms can be
      tracked.

      Clients SHOULD set this property to the current date-time value in
      UTC when a calendar user acknowledges a pending alarm.  Certain
      kinds of alarm may not provide feedback as to when the calendar
      user sees them, for example email based alerts.  For those kinds
      of alarms, the client SHOULD set this property when the alarm is
      triggered and the action successfully carried out.

      When an alarm is triggered on a client, clients can check to see
      if an "ACKNOWLEDGED" property is present.  If it is, and the value
      of that property is greater than or equal to the computed trigger
      time for the alarm, then the client SHOULD NOT trigger the alarm.
      Similarly, if an alarm has been triggered and an "alert" presented
      to a calendar user, clients can monitor the iCalendar data to
      determine whether an "ACKNOWLEDGED" is added or changed in the
      alarm component.  If the value of any "ACKNOWLEDGED" in the alarm
      changes and is greater than or equal to the trigger time of the
      alarm, then clients SHOULD dismiss or cancel any "alert" presented
      to the calendar user.

   Format Definition:  This property is defined by the following
      notation:

   acknowledged = "ACKNOWLEDGED" acknowledgedparam ":" datetime CRLF

   acknowledgedparam  = *(

                        ; the following is OPTIONAL,
                        ; and MAY occur more than once

                        (";" other-param)

                        )








Daboo                   Expires December 11, 2012              [Page 11]
 
Internet-Draft              VALARM Extensions                  June 2012


   Example:  The following is an example of this property:

   ACKNOWLEDGED:20090604T084500Z
 */
public class Acknowledged extends DateProperty<Instant> {

    private static final long serialVersionUID = 596619479148598528L;

    public Acknowledged() {
        this(Instant.now());
    }

    /**
     * @param aValue a string representation of a DTSTAMP value
     */
    public Acknowledged(final String aValue) {
        this(new ArrayList<>(), aValue);
    }
    
    /**
     * @param aList a list of parameters for this component
     * @param aValue a value string for this component
     */
    public Acknowledged(final List<Parameter> aList, final String aValue) {
        super(ACKNOWLEDGED, aList, new Factory(), CalendarDateFormat.UTC_DATE_TIME_FORMAT);
        setValue(aValue);
    }

    /**
     * @param aDate a date representing a date-time 
     */
    public Acknowledged(final Instant aDate) {
        super(ACKNOWLEDGED, new Factory(), CalendarDateFormat.UTC_DATE_TIME_FORMAT);
        setDate(aDate);
    }

    /**
     * @param aList a list of parameters for this component
     * @param aDate a date representing a date-time
     */
    public Acknowledged(final List<Parameter> aList, final Instant aDate) {
        super(ACKNOWLEDGED, aList, new Factory(), CalendarDateFormat.UTC_DATE_TIME_FORMAT);
        setDate(aDate);
    }

    @Override
    public Property copy() {
        return new Factory().createProperty(getParameters(), getValue());
    }

    public static class Factory extends Content.Factory implements PropertyFactory<Acknowledged> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(ACKNOWLEDGED);
        }

        public Acknowledged createProperty(final List<Parameter> parameters, final String value) {
            return new Acknowledged(parameters, value);
        }

        public Acknowledged createProperty() {
            return new Acknowledged();
        }
    }
}
