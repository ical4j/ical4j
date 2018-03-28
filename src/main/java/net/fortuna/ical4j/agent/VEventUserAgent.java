package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.transform.RequestTransformer;
import net.fortuna.ical4j.util.UidGenerator;

public class VEventUserAgent extends AbstractUserAgent<VEvent> {

    private final RequestTransformer delegateTransformer;

    public VEventUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator) {
        super(prodId, organizer, uidGenerator);
        delegateTransformer = new RequestTransformer(uidGenerator);
    }

    /**
     * <pre>
     * 3.2.1.  PUBLISH
     *
     *     The "PUBLISH" method in a "VEVENT" calendar component is an
     *     unsolicited posting of an iCalendar object.  Any CU may add published
     *     components to their calendar.  The "Organizer" MUST be present in a
     *     published iCalendar component.  "Attendees" MUST NOT be present.  Its
     *     expected usage is for encapsulating an arbitrary event as an
     *     iCalendar object.  The "Organizer" may subsequently update (with
     *     another "PUBLISH" method), add instances to (with an "ADD" method),
     *     or cancel (with a "CANCEL" method) a previously published "VEVENT"
     *     calendar component.
     * </pre>
     */
    @Override
    public Calendar publish(VEvent... component) {
        Calendar published = wrap(Method.PUBLISH, component);
        published.validate();
        return published;
    }

    /**
     * <pre>
     * 3.2.2.  REQUEST
     *
     *     The "REQUEST" method in a "VEVENT" component provides the following
     *     scheduling functions:
     *
     *     o  Invite "Attendees" to an event.
     *
     *     o  Reschedule an existing event.
     *
     *     o  Response to a "REFRESH" request.
     *
     *     o  Update the details of an existing event, without rescheduling it.
     *
     *     o  Update the status of "Attendees" of an existing event, without
     *     rescheduling it.
     *
     *     o  Reconfirm an existing event, without rescheduling it.
     *
     *     o  Forward a "VEVENT" to another uninvited CU.
     *
     *     o  For an existing "VEVENT" calendar component, delegate the role of
     *     "Attendee" to another CU.
     *
     *     o  For an existing "VEVENT" calendar component, change the role of
     *     "Organizer" to another CU.
     *
     *     The "Organizer" originates the "REQUEST".  The recipients of the
     *     "REQUEST" method are the CUs invited to the event, the "Attendees".
     *     "Attendees" use the "REPLY" method to convey attendance status to the
     *     "Organizer".
     *
     *     The "UID" and "SEQUENCE" properties are used to distinguish the
     *     various uses of the "REQUEST" method.  If the "UID" property value in
     *     the "REQUEST" is not found on the recipient's calendar, then the
     *     "REQUEST" is for a new "VEVENT" calendar component.  If the "UID"
     *     property value is found on the recipient's calendar, then the
     *     "REQUEST" is for a rescheduling, an update, or a reconfirmation of
     *     the "VEVENT" calendar component.
     *
     *     For the "REQUEST" method, multiple "VEVENT" components in a single
     *     iCalendar object are only permitted for components with the same
     *     "UID" property.  That is, a series of recurring events may have
     *     instance-specific information.  In this case, multiple "VEVENT"
     *     components are needed to express the entire series.
     * </pre>
     */
    @Override
    public Calendar request(VEvent... component) {
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
     * 3.2.3.  REPLY
     *
     *     The "REPLY" method in a "VEVENT" calendar component is used to
     *     respond (e.g., accept or decline) to a "REQUEST" or to reply to a
     *     delegation "REQUEST".  When used to provide a delegation response,
     *     the "Delegator" SHOULD include the calendar address of the "Delegate"
     *     on the "DELEGATED-TO" property parameter of the "Delegator's"
     *     "ATTENDEE" property.  The "Delegate" SHOULD include the calendar
     *     address of the "Delegator" on the "DELEGATED-FROM" property parameter
     *     of the "Delegate's" "ATTENDEE" property.
     *
     *     The "REPLY" method is also used when processing of a "REQUEST" fails.
     *     Depending on the value of the "REQUEST-STATUS" property, no
     *     scheduling action may have been performed.
     *
     *     The "Organizer" of an event may receive the "REPLY" method from a CU
     *     not in the original "REQUEST".  For example, a "REPLY" may be
     *     received from a "Delegate" to an event.  In addition, the "REPLY"
     *     method may be received from an unknown CU (a "Party Crasher").  This
     *     uninvited "Attendee" may be accepted, or the "Organizer" may cancel
     *     the event for the uninvited "Attendee" by sending a "CANCEL" method
     *     to the uninvited "Attendee".
     *
     *     An "Attendee" MAY include a message to the "Organizer" using the
     *     "COMMENT" property.  For example, if the user indicates tentative
     *     acceptance and wants to let the "Organizer" know why, the reason can
     *     be expressed in the "COMMENT" property value.
     *
     *     The "Organizer" may also receive a "REPLY" from one CU on behalf of
     *     another.  Like the scenario enumerated above for the "Organizer",
     *     "Attendees" may have another CU respond on their behalf.  This is
     *     done using the "SENT-BY" parameter.
     *
     *     The optional properties listed in the table below (those listed as
     *     "0+" or "0 or 1") MUST NOT be changed from those of the original
     *     request.  If property changes are desired, the "COUNTER" message must
     *     be used.
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
     * 3.2.4.  ADD
     *
     *     The "ADD" method allows the "Organizer" to add one or more new
     *     instances to an existing "VEVENT" using a single iTIP message without
     *     having to send the entire "VEVENT" with all the existing instance
     *     data, as it would have to do if the "REQUEST" method were used.
     *
     *     The "UID" must be that of the existing event.  If the "UID" property
     *     value in the "ADD" is not found on the recipient's calendar, then the
     *     recipient SHOULD send a "REFRESH" to the "Organizer" in order to be
     *     updated with the latest version of the "VEVENT".  If an "Attendee"
     *     implementation does not support the "ADD" method, it should respond
     *     with a "REQUEST-STATUS" value of 3.14 and ask for a "REFRESH".
     *
     *     When handling an "ADD" message, the "Attendee" treats each component
     *     in the "ADD" message as if it were referenced via an "RDATE" in the
     *     main component.
     * </pre>
     */
    @Override
    public Calendar add(VEvent component) {
        Calendar add = wrap(Method.ADD, component);
        add.validate();
        return add;
    }

    /**
     * <pre>
     * 3.2.5.  CANCEL
     *
     *     The "CANCEL" method in a "VEVENT" calendar component is used to send
     *     a cancellation notice of an existing event request to the affected
     *     "Attendees".  The message is sent by the "Organizer" of the event.
     *     For a recurring event, either the whole event or instances of an
     *     event may be cancelled.  To cancel the complete range of a recurring
     *     event, the "UID" property value for the event MUST be specified and a
     *     "RECURRENCE-ID" MUST NOT be specified in the "CANCEL" method.  In
     *     order to cancel an individual instance of the event, the
     *     "RECURRENCE-ID" property value for the event MUST be specified in the
     *     "CANCEL" method.
     *
     *     There are two options for canceling a sequence of instances of a
     *     recurring "VEVENT" calendar component:
     *
     *     a.  The "RECURRENCE-ID" property for an instance in the sequence MUST
     *     be specified with the "RANGE" property parameter value of
     *     "THISANDFUTURE" to indicate cancellation of the specified
     *     "VEVENT" calendar component and all instances after.
     *
     *     b.  Individual recurrence instances may be cancelled by specifying
     *     multiple "VEVENT" components each with a "RECURRENCE-ID" property
     *     corresponding to one of the instances to be cancelled.
     *
     *     The "Organizer" MUST send a "CANCEL" message to each "Attendee"
     *     affected by the cancellation.  This can be done using a single
     *     "CANCEL" message for all "Attendees" or by using multiple messages
     *     with different subsets of the affected "Attendees" in each.
     *
     *     When a "VEVENT" is cancelled, the "SEQUENCE" property value MUST be
     *     incremented as described in Section 2.1.4.
     * </pre>
     */
    @Override
    public Calendar cancel(VEvent... component) {
        Calendar cancel = wrap(Method.CANCEL, component);
        cancel.validate();
        return cancel;
    }

    /**
     * <pre>
     * 3.2.6.  REFRESH
     *
     *     The "REFRESH" method in a "VEVENT" calendar component is used by
     *     "Attendees" of an existing event to request an updated description
     *     from the event "Organizer".  The "REFRESH" method must specify the
     *     "UID" property of the event to update.  A recurrence instance of an
     *     event may be requested by specifying the "RECURRENCE-ID" property
     *     corresponding to the associated event.  The "Organizer" responds with
     *     the latest description and version of the event.
     * </pre>
     */
    @Override
    public Calendar refresh(VEvent component) {
        Calendar refresh = wrap(Method.REFRESH, component);
        refresh.validate();
        return refresh;
    }

    /**
     * <pre>
     * 3.2.7.  COUNTER
     *
     *     The "COUNTER" method for a "VEVENT" calendar component is used by an
     *     "Attendee" of an existing event to submit to the "Organizer" a
     *     counter proposal to the event.  The "Attendee" sends this message to
     *     the "Organizer" of the event.
     *
     *     The counter proposal is an iCalendar object consisting of a "VEVENT"
     *     calendar component that provides the complete description of the
     *     alternate event.
     *
     *     The "Organizer" rejects the counter proposal by sending the
     *     "Attendee" a "DECLINECOUNTER" method.  The "Organizer" accepts the
     *     counter proposal by rescheduling the event as described in
     *     Section 3.2.2.1, "Rescheduling an Event".  The "Organizer's" CUA
     *     SHOULD send a "REQUEST" message to all "Attendees" affected by any
     *     change triggered by an accepted "COUNTER".
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
     * 3.2.8.  DECLINECOUNTER
     *
     *     The "DECLINECOUNTER" method in a "VEVENT" calendar component is used
     *     by the "Organizer" of an event to reject a counter proposal submitted
     *     by an "Attendee".  The "Organizer" must send the "DECLINECOUNTER"
     *     message to the "Attendee" that sent the "COUNTER" method to the
     *     "Organizer".
     * </pre>
     */
    @Override
    public Calendar declineCounter(Calendar counter) {
        Calendar declineCounter = transform(Method.DECLINE_COUNTER, counter);
        declineCounter.validate();
        return declineCounter;
    }
}
