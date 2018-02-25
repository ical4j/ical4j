package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.CalendarComponent;

/**
 * Created by fortuna on 19/07/2017.
 */
public interface UserAgent<T extends CalendarComponent> {

    /**
     * Identify the role of the user agent instance. Can be {@link net.fortuna.ical4j.model.property.Organizer},
     * {@link net.fortuna.ical4j.model.property.Attendee}, or some other CU.
     * <pre>
     *
     *    +-----------+-------------------------------------------------------+
     *    | Role      | Description                                           |
     *    +-----------+-------------------------------------------------------+
     *    | Organizer | The CU who initiates an exchange takes on the role of |
     *    |           | Organizer.  For example, the CU who proposes a group  |
     *    |           | meeting is the Organizer.                             |
     *    |           |                                                       |
     *    | Attendee  | CUs who are included in the scheduling message as     |
     *    |           | possible recipients of that scheduling message.  For  |
     *    |           | example, the CUs asked to participate in a group      |
     *    |           | meeting by the Organizer take on the role of          |
     *    |           | Attendee.                                             |
     *    |           |                                                       |
     *    | Other CU  | A CU that is not explicitly included in a scheduling  |
     *    |           | message, i.e., not the Organizer or an Attendee.      |
     *    +-----------+-------------------------------------------------------+
     *
     * </pre>
     * 
     * @return
     */
//    Property getRole();

    /**
     * Identifies the iTiP method employed by the user agent instance.
     *
     * <pre>
     *
     *    +----------------+--------------------------------------------------+
     *    | Method         | Description                                      |
     *    +----------------+--------------------------------------------------+
     *    | PUBLISH        | Used to publish an iCalendar object to one or    |
     *    |                | more "Calendar Users".  There is no              |
     *    |                | interactivity between the publisher and any      |
     *    |                | other "Calendar User".  An example might include |
     *    |                | a baseball team publishing its schedule to the   |
     *    |                | public.                                          |
     *    |                |                                                  |
     *    | REQUEST        | Used to schedule an iCalendar object with other  |
     *    |                | "Calendar Users".  Requests are interactive in   |
     *    |                | that they require the receiver to respond using  |
     *    |                | the reply methods.  Meeting requests, busy-time  |
     *    |                | requests, and the assignment of tasks to other   |
     *    |                | "Calendar Users" are all examples.  Requests are |
     *    |                | also used by the Organizer to update the status  |
     *    |                | of an iCalendar object.                          |
     *    |                |                                                  |
     *    | REPLY          | A reply is used in response to a request to      |
     *    |                | convey Attendee status to the Organizer.         |
     *    |                | Replies are commonly used to respond to meeting  |
     *    |                | and task requests.                               |
     *    |                |                                                  |
     *    | ADD            | Add one or more new instances to an existing     |
     *    |                | recurring iCalendar object.                      |
     *    |                |                                                  |
     *    | CANCEL         | Cancel one or more instances of an existing      |
     *    |                | iCalendar object.                                |
     *    |                |                                                  |
     *    | REFRESH        | Used by an Attendee to request the latest        |
     *    |                | version of an iCalendar object.                  |
     *    |                |                                                  |
     *    | COUNTER        | Used by an Attendee to negotiate a change in an  |
     *    |                | iCalendar object.  Examples include the request  |
     *    |                | to change a proposed event time or change the    |
     *    |                | due date for a task.                             |
     *    |                |                                                  |
     *    | DECLINECOUNTER | Used by the Organizer to decline the proposed    |
     *    |                | counter proposal.                                |
     *    +----------------+--------------------------------------------------+
     * </pre>
     *
     * @return
     */
//    Method getMethod();

    Calendar publish(T... component);

    Calendar request(T... component);

    /**
     * 3.2.2.3.  Delegating an Event to Another CU
     *
     *     Some calendar and scheduling systems allow "Attendees" to delegate
     *     their presence at an event to another "Calendar User". iTIP supports
     *     this concept using the following workflow.  Any "Attendee" may
     *     delegate their right to participate in a calendar "VEVENT" to another
     *     CU.  The implication is that the delegate participates in lieu of the
     *     original "Attendee", NOT in addition to the "Attendee".  The
     *     delegator MUST notify the "Organizer" of this action using the steps
     *     outlined below.  Implementations may support or restrict delegation
     *     as they see fit.  For instance, some implementations may restrict a
     *     delegate from delegating a "REQUEST" to another CU.
     *
     *     The "Delegator" of an event forwards the existing "REQUEST" to the
     *     "Delegate".  The "REQUEST" method MUST include an "ATTENDEE" property
     *     with the calendar address of the "Delegate".  The "Delegator" MUST
     *     also send a "REPLY" method to the "Organizer" with the "Delegator's"
     *     "ATTENDEE" property "PARTSTAT" parameter value set to "DELEGATED".
     *     In addition, the "DELEGATED-TO" parameter MUST be included with the
     *     calendar address of the "Delegate".  Also, a new "ATTENDEE" property
     *     for the "Delegate" MUST be included and must specify the calendar
     *     user address set in the "DELEGATED-TO" parameter, as above.
     *
     *     In response to the request, the "Delegate" MUST send a "REPLY" method
     *     to the "Organizer", and optionally to the "Delegator".  The "REPLY"
     *     method SHOULD include the "ATTENDEE" property with the "DELEGATED-
     *     FROM" parameter value of the "Delegator's" calendar address.
     *
     *     The "Delegator" may continue to receive updates to the event even
     *     though they will not be attending.  This is accomplished by the
     *     "Delegator" setting their "role" attribute to "NON-PARTICIPANT" in
     *     the "REPLY" to the "Organizer".
     *
     * @param request
     * @return
     */
    Calendar delegate(Calendar request);

    Calendar reply(Calendar request);

    Calendar add(T component);

    Calendar cancel(T... component);

    Calendar refresh(T component);

    Calendar counter(Calendar request);

    Calendar declineCounter(Calendar counter);
}
