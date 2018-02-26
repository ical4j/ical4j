package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.transform.RequestTransformer;
import net.fortuna.ical4j.util.UidGenerator;

public class VToDoUserAgent extends AbstractUserAgent<VToDo> {

    private final RequestTransformer delegateTransformer;

    public VToDoUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator) {
        super(prodId, organizer, uidGenerator);
        delegateTransformer = new RequestTransformer(uidGenerator);
    }

    /**
     * <pre>
     * 3.4.1.  PUBLISH
     *
     *     The "PUBLISH" method in a "VTODO" calendar component has no
     *     associated response.  It is simply a posting of an iCalendar object
     *     that may be added to a calendar.  It MUST have an "Organizer".  It
     *     MUST NOT have "Attendees".  Its expected usage is for encapsulating
     *     an arbitrary "VTODO" calendar component as an iCalendar object.  The
     *     "Organizer" MAY subsequently update (with another "PUBLISH" method),
     *     add instances to (with an "ADD" method), or cancel (with a "CANCEL"
     *     method) a previously published "VTODO" calendar component.
     *
     * </pre>
     */
    @Override
    public Calendar publish(VToDo... component) {
        Calendar published = wrap(Method.PUBLISH, component);
        published.validate();
        return published;
    }

    /**
     * <pre>
     * 3.4.2.  REQUEST
     *
     *     The "REQUEST" method in a "VTODO" calendar component provides the
     *     following scheduling functions:
     *
     *     o  Assign a to-do to one or more "Calendar Users".
     *
     *     o  Reschedule an existing to-do.
     *
     *     o  Update the details of an existing to-do, without rescheduling it.
     *
     *     o  Update the completion status of "Attendees" of an existing to-do,
     *     without rescheduling it.
     *
     *     o  Reconfirm an existing to-do, without rescheduling it.
     *
     *     o  Delegate/reassign an existing to-do to another "Calendar User".
     *
     *     The assigned "Calendar Users" are identified in the "VTODO" calendar
     *     component by individual "ATTENDEE;ROLE=REQ-PARTICIPANT" property
     *     value sequences.
     *
     *     Typically, the originator of a "REQUEST" is the "Organizer" of the
     *     to-do, and the recipient of a "REQUEST" is the "Calendar User"
     *     assigned the to-do.  The "Attendee" uses the "REPLY" method to convey
     *     their acceptance and completion status to the "Organizer" of the
     *     "REQUEST".
     *
     *     The "UID", "SEQUENCE", and "DTSTAMP" properties are used to
     *     distinguish the various uses of the "REQUEST" method.  If the "UID"
     *     property value in the "REQUEST" is not found on the recipient's
     *     calendar, then the "REQUEST" is for a new to-do.  If the "UID"
     *     property value is found on the recipient's calendar, then the
     *     "REQUEST" is a rescheduling, an update, or a reconfirmation of the
     *     "VTODO" calendar object.
     *
     *     If the "Organizer" of the "REQUEST" method is not authorized to make
     *     a to-do request on the "Attendee's" calendar system, then an
     *     exception is returned in the "REQUEST-STATUS" property of a
     *     subsequent "REPLY" method, but no scheduling action is performed.
     *
     *     For the "REQUEST" method, multiple "VTODO" components in a single
     *     iCalendar object are only permitted for components with the same
     *     "UID" property.  That is, a series of recurring events may have
     *     instance-specific information.  In this case, multiple "VTODO"
     *     components are needed to express the entire series.
     * </pre>
     */
    @Override
    public Calendar request(VToDo... component) {
        Calendar request = wrap(Method.REQUEST, component);
        request.validate();
        return request;
    }

    @Override
    public Calendar delegate(Calendar request) {
        Calendar delegated = delegateTransformer.transform(request);
        delegated.validate();
        return delegated;
    }

    /**
     * <pre>
     * 3.4.3.  REPLY
     *
     *     The "REPLY" method in a "VTODO" calendar component is used to respond
     *     (e.g., accept or decline) to a request or to reply to a delegation
     *     request.  It is also used by an "Attendee" to update their completion
     *     status.  When used to provide a delegation response, the "Delegator"
     *     MUST include the calendar address of the "Delegate" in the
     *     "DELEGATED-TO" parameter of the "Delegator's" "ATTENDEE" property.
     *     The "Delegate" MUST include the calendar address of the "Delegator"
     *     on the "DELEGATED-FROM" parameter of the "Delegate's" "ATTENDEE"
     *     property.
     *
     *     The "REPLY" method MAY also be used to respond to an unsuccessful
     *     "VTODO" calendar component "REQUEST" method.  Depending on the
     *     "REQUEST-STATUS" value, no scheduling action may have been performed.
     *
     *     The "Organizer" of a "VTODO" calendar component MAY receive a "REPLY"
     *     method from a "Calendar User" not in the original "REQUEST".  For
     *     example, a "REPLY" method MAY be received from a "Delegate" of a
     *     "VTODO" calendar component.  In addition, the "REPLY" method MAY be
     *     received from an unknown "Calendar User" who has been forwarded the
     *     "REQUEST" by an original "Attendee" of the "VTODO" calendar
     *     component.  This uninvited "Attendee" MAY be accepted or the
     *     "Organizer" MAY cancel the "VTODO" calendar component for the
     *     uninvited "Attendee" by sending them a "CANCEL" method.
     * </pre>
     */
    @Override
    public Calendar reply(Calendar request) {
        Calendar reply = transform(Method.REPLY, request);
        reply.validate();
        return reply;
    }

    /**
     * <pre>
     * 3.4.4.  ADD
     *
     *     The "ADD" method allows the "Organizer" to add one or more new
     *     instances to an existing "VTODO" using a single iTIP message without
     *     having to send the entire "VTODO" with all the existing instance
     *     data, as it would have to do if the "REQUEST" method were used.
     *
     *     The "UID" must be that of the existing to-do.  If the "UID" property
     *     value in the "ADD" is not found on the recipient's calendar, then the
     *     recipient SHOULD send a "REFRESH" to the "Organizer" in order to be
     *     updated with the latest version of the "VTODO".  If an "Attendee"
     *     implementation does not support the "ADD" method, it should respond
     *     with a "REQUEST-STATUS" value of 3.14 and ask for a "REFRESH".
     *
     *     When handling an "ADD" message, the "Attendee" treats each component
     *     in the "ADD" message as if it were referenced via an "RDATE" in the
     *     main component.
     *
     *     The "SEQUENCE" property value is incremented since the sequence of
     *     to-dos has changed.
     * </pre>
     */
    @Override
    public Calendar add(VToDo component) {
        Calendar add = wrap(Method.ADD, component);
        add.validate();
        return add;
    }

    /**
     * <pre>
     * 3.4.5.  CANCEL
     *
     *     The "CANCEL" method in a "VTODO" calendar component is used to send a
     *     cancellation notice of an existing "VTODO" calendar request to the
     *     affected "Attendees".  The message is sent by the "Organizer" of a
     *     "VTODO" calendar component to the "Attendees" of the "VTODO" calendar
     *     component.  For a recurring "VTODO" calendar component, either the
     *     whole "VTODO" calendar component or instances of a "VTODO" calendar
     *     component may be cancelled.  To cancel the complete range of a
     *     recurring "VTODO" calendar component, the "UID" property value for
     *     the "VTODO" calendar component MUST be specified and a "RECURRENCE-
     *     ID" MUST NOT be specified in the "CANCEL" method.  In order to cancel
     *     an individual instance of a recurring "VTODO" calendar component, the
     *     "RECURRENCE-ID" property value for the "VTODO" calendar component
     *     MUST be specified in the "CANCEL" method.
     *
     *     There are two options for canceling a sequence of instances of a
     *     recurring "VTODO" calendar component:
     *
     *     a.  The "RECURRENCE-ID" property for an instance in the sequence MUST
     *     be specified with the "RANGE" property parameter value of
     *     "THISANDFUTURE" to indicate cancellation of the specified "VTODO"
     *     calendar component and all instances after.
     *
     *     b.  Individual recurrence instances may be cancelled by specifying
     *     multiple "VTODO" components each with a "RECURRENCE-ID" property
     *     corresponding to one of the instances to be cancelled.
     *
     *     The "Organizer" MUST send a "CANCEL" message to each "Attendee"
     *     affected by the cancellation.  This can be done by using either a
     *     single "CANCEL" message for all "Attendees" or multiple messages with
     *     different subsets of the affected "Attendees" in each.
     *
     *     When a "VTODO" is cancelled, the "SEQUENCE" property value MUST be
     *     incremented as described in Section 2.1.4.
     * </pre>
     */
    @Override
    public Calendar cancel(VToDo... component) {
        Calendar cancel = wrap(Method.CANCEL, component);
        cancel.validate();
        return cancel;
    }

    /**
     * <pre>
     * 3.4.6.  REFRESH
     *
     *     The "REFRESH" method in a "VTODO" calendar component is used by
     *     "Attendees" of an existing "VTODO" calendar component to request an
     *     updated description from the "Organizer" of the "VTODO" calendar
     *     component.  The "Organizer" of the "VTODO" calendar component MAY use
     *     this method to request an updated status from the "Attendees".  The
     *     "REFRESH" method MUST specify the "UID" property corresponding to the
     *     "VTODO" calendar component needing update.
     *
     *     A refresh of a recurrence instance of a "VTODO" calendar component
     *     may be requested by specifying the "RECURRENCE-ID" property
     *     corresponding to the associated "VTODO" calendar component.  The
     *     "Organizer" responds with the latest description and rendition of the
     *     "VTODO" calendar component.  In most cases, this will be a "REQUEST"
     *     unless the "VTODO" has been cancelled, in which case the "Organizer"
     *     MUST send a "CANCEL".  This method is intended to facilitate machine
     *     processing of requests for updates to a "VTODO" calendar component.
     * </pre>
     */
    @Override
    public Calendar refresh(VToDo component) {
        Calendar refresh = wrap(Method.REFRESH, component);
        refresh.validate();
        return refresh;
    }

    /**
     * <pre>
     *
     *     3.4.7.  COUNTER
     *
     *     The "COUNTER" method in a "VTODO" calendar component is used by an
     *     "Attendee" of an existing "VTODO" calendar component to submit to the
     *     "Organizer" a counter proposal for the "VTODO" calendar component.
     *
     *     The counter proposal is an iCalendar object consisting of a "VTODO"
     *     calendar component that provides the complete description of the
     *     alternate "VTODO" calendar component.
     *
     *     The "Organizer" rejects the counter proposal by sending the
     *     "Attendee" a "DECLINECOUNTER" method.  The "Organizer" accepts the
     *     counter proposal by rescheduling the to-do as described in
     *     Section 3.4.2.1, "REQUEST for Rescheduling a To-Do".  The
     *     "Organizer's" CUA SHOULD send a "REQUEST" message to all "Attendees"
     *     affected by any change triggered by an accepted "COUNTER".
     * </pre>
     */
    @Override
    public Calendar counter(Calendar request) {
        Calendar counter = transform(Method.COUNTER, request);
        counter.validate();
        return counter;
    }

    /**
     * <pre>
     * 3.4.8.  DECLINECOUNTER
     *
     *     The "DECLINECOUNTER" method in a "VTODO" calendar component is used
     *     by an "Organizer" of the "VTODO" calendar component to reject a
     *     counter proposal offered by one of the "Attendees".  The "Organizer"
     *     sends the message to the "Attendee" that sent the "COUNTER" method to
     *     the "Organizer".
     * </pre>
     */
    @Override
    public Calendar declineCounter(Calendar counter) {
        Calendar declineCounter = transform(Method.DECLINE_COUNTER, counter);
        declineCounter.validate();
        return declineCounter;
    }
}
