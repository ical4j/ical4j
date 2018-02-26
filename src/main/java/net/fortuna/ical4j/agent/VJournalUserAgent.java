package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.util.UidGenerator;

public class VJournalUserAgent extends AbstractUserAgent<VJournal> {

    public VJournalUserAgent(ProdId prodId, Organizer organizer, UidGenerator uidGenerator) {
        super(prodId, organizer, uidGenerator);
    }

    /**
     * <pre>
     * 3.5.1.  PUBLISH
     *
     *     The "PUBLISH" method in a "VJOURNAL" calendar component has no
     *     associated response.  It is simply a posting of an iCalendar object
     *     that may be added to a calendar.  It MUST have an "Organizer".  It
     *     MUST NOT have "Attendees".  The expected usage is for encapsulating
     *     an arbitrary journal entry as an iCalendar object.  The "Organizer"
     *     MAY subsequently update (with another "PUBLISH" method) or cancel
     *     (with a "CANCEL" method) a previously published journal entry.
     * </pre>
     */
    @Override
    public Calendar publish(VJournal... component) {
        Calendar published = wrap(Method.PUBLISH, component);
        published.validate();
        return published;
    }

    /**
     * Not applicable for this agent implementation.
     * @throws UnsupportedOperationException
     */
    @Override
    public Calendar request(VJournal... component) {
        throw new UnsupportedOperationException("Method [REQUEST] not supported by VJOURNAL");
    }

    /**
     * Not applicable for this agent implementation.
     * @throws UnsupportedOperationException
     */
    @Override
    public Calendar delegate(Calendar request) {
        throw new UnsupportedOperationException("REQUEST delegation not supported by VJOURNAL");
    }

    /**
     * Not applicable for this agent implementation.
     * @throws UnsupportedOperationException
     */
    @Override
    public Calendar reply(Calendar request) {
        throw new UnsupportedOperationException("Method [REPLY] not supported by VJOURNAL");
    }

    /**
     * <pre>
     * 3.5.2.  ADD
     *
     *     The "ADD" method allows the "Organizer" to add one or more new
     *     instances to an existing "VJOURNAL" using a single iTIP message
     *     without having to send the entire "VJOURNAL" with all the existing
     *     instance data, as it would have to do if the "REQUEST" method were
     *     used.
     *
     *     The "UID" must be that of the existing journal entry.  If the "UID"
     *     property value in the "ADD" is not found on the recipient's calendar,
     *     then the recipient MAY treat the "ADD" as a "PUBLISH".
     *
     *     When handling an "ADD" message, the "Attendee" treats each component
     *     in the "ADD" message as if it were referenced via an "RDATE" in the
     *     main component.  There is no response to the "Organizer".
     * </pre>
     */
    @Override
    public Calendar add(VJournal component) {
        Calendar add = wrap(Method.ADD, component);
        add.validate();
        return add;
    }

    /**
     * <pre>
     * 3.5.3.  CANCEL
     *
     *     The "CANCEL" method in a "VJOURNAL" calendar component is used to
     *     send a cancellation notice of an existing journal entry.  The message
     *     is sent by the "Organizer" of a journal entry.  For a recurring
     *     journal entry, either the whole journal entry or instances of a
     *     journal entry may be cancelled.  To cancel the complete range of a
     *     recurring journal entry, the "UID" property value for the journal
     *     entry MUST be specified and a "RECURRENCE-ID" property MUST NOT be
     *     specified in the "CANCEL" method.  In order to cancel an individual
     *     instance of the journal entry, the "RECURRENCE-ID" property value for
     *     the journal entry MUST be specified in the "CANCEL" method.
     *
     *     There are two options for canceling a sequence of instances of a
     *     recurring "VJOURNAL" calendar component:
     *
     *     a.  The "RECURRENCE-ID" property for an instance in the sequence MUST
     *     be specified with the "RANGE" property parameter value of
     *     "THISANDFUTURE" to indicate cancellation of the specified
     *     "VJOURNAL" calendar component and all instances after.
     *
     *     b.  Individual recurrence instances may be cancelled by specifying
     *     multiple "VJOURNAL" components each with a "RECURRENCE-ID"
     *     property corresponding to one of the instances to be cancelled.
     *
     *     When a "VJOURNAL" is cancelled, the "SEQUENCE" property value MUST be
     *     incremented as described in Section 2.1.4.
     * </pre>
     */
    @Override
    public Calendar cancel(VJournal... component) {
        Calendar cancel = wrap(Method.CANCEL, component);
        cancel.validate();
        return cancel;
    }

    /**
     * Not applicable for this agent implementation.
     * @throws UnsupportedOperationException
     */
    @Override
    public Calendar refresh(VJournal component) {
        throw new UnsupportedOperationException("Method [REFRESH] not supported by VJOURNAL");
    }

    /**
     * Not applicable for this agent implementation.
     * @throws UnsupportedOperationException
     */
    @Override
    public Calendar counter(Calendar request) {
        throw new UnsupportedOperationException("Method [COUNTER] not supported by VJOURNAL");
    }

    /**
     * Not applicable for this agent implementation.
     * @throws UnsupportedOperationException
     */
    @Override
    public Calendar declineCounter(Calendar counter) {
        throw new UnsupportedOperationException("Method [DECLINECOUNTER] not supported by VJOURNAL");
    }
}
