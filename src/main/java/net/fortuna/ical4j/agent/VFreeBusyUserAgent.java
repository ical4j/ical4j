package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.util.UidGenerator;

public class VFreeBusyUserAgent extends AbstractUserAgent<VFreeBusy> {

    public VFreeBusyUserAgent(Organizer organizer, UidGenerator uidGenerator) {
        super(organizer, uidGenerator);
    }

    /**
     * 3.3.1.  PUBLISH

     The "PUBLISH" method in a "VFREEBUSY" calendar component is used to
     publish busy time data.  The method may be sent from one CU to any
     other.  The purpose of the method is to provide a way to send
     unsolicited busy time data.  That is, the busy time data is not being
     sent as a "REPLY" to the receipt of a "REQUEST" method.

     The "ORGANIZER" property MUST be specified in the busy time
     information.  The value is the CU address of the originator of the
     busy time information.

     The busy time information within the iCalendar object MAY be grouped
     into more than one "VFREEBUSY" calendar component.  This capability
     allows busy time periods to be grouped according to some common
     periodicity, such as a calendar week, month, or year.  In this case,
     each "VFREEBUSY" calendar component MUST include the "ORGANIZER",
     "DTSTART", and "DTEND" properties in order to specify the source of
     the busy time information and the date and time interval over which
     the busy time information covers.

     * @param component
     * @return
     */
    @Override
    public Calendar publish(VFreeBusy... component) {
        return wrap(Method.PUBLISH, component);
    }

    @Override
    public Calendar request(VFreeBusy... component) {
        return wrap(Method.REQUEST, component);
    }

    @Override
    public Calendar reply(VFreeBusy... component) {
        return wrap(Method.REPLY, component);
    }

    @Override
    public Calendar add(VFreeBusy component) {
        throw new UnsupportedOperationException("Method [ADD] not supported by VFREEBUSY");
    }

    @Override
    public Calendar cancel(VFreeBusy... component) {
        throw new UnsupportedOperationException("Method [CANCEL] not supported by VFREEBUSY");
    }

    @Override
    public Calendar refresh(VFreeBusy component) {
        throw new UnsupportedOperationException("Method [REFRESH] not supported by VFREEBUSY");
    }

    @Override
    public Calendar counter(VFreeBusy component) {
        throw new UnsupportedOperationException("Method [COUNTER] not supported by VFREEBUSY");
    }

    @Override
    public Calendar declineCounter(VFreeBusy... component) {
        throw new UnsupportedOperationException("Method [DECLINECOUNTER] not supported by VFREEBUSY");
    }
}
