package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.CalendarComponent;

/**
 * Created by fortuna on 19/07/2017.
 *
 * <pre>
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
 */
public interface UserAgent<T extends CalendarComponent> {

    /**
     * Publish one or more calendar components.
     *
     * @param component one or more component objects
     * @return a calendar object validated to conform to iTIP method PUBLISH
     */
    Calendar publish(T... component);

    /**
     * Request attendance to one or more calendar components.
     *
     * @param component one or more component objects
     * @return a calendar object validated to conform to iTIP method REQUEST
     */
    Calendar request(T... component);

    /**
     * <pre>
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
     * </pre>
     *
     * @param request a calendar implement the iTIP REQUEST method
     * @return a calendar delegating the request via an iTIP REQUEST method
     */
    Calendar delegate(Calendar request);

    /**
     * Reply to a calendar request.
     *
     * @param request a calendar request
     * @return a calendar object validated to conform to iTIP method REPLY
     */
    Calendar reply(Calendar request);

    /**
     * Add a calendar component.
     *
     * @param component a calendar component to add
     * @return a calendar object validated to conform to iTIP method ADD
     */
    Calendar add(T component);

    /**
     * Cancel one or more calendar components.
     *
     * @param component one or more component objects
     * @return a calendar object validated to conform to iTIP method CANCEL
     */
    Calendar cancel(T... component);

    /**
     * Refresh a calendar component.
     *
     * @param component a calendar component to refresh
     * @return a calendar object validated to conform to iTIP method REFRESH
     */
    Calendar refresh(T component);

    /**
     * Counter a calendar request.
     *
     * @param request a calendar request to counter
     * @return a calendar object validated to conform to iTIP method COUNTER
     */
    Calendar counter(Calendar request);

    /**
     * Decline a counter to a request.
     *
     * @param counter a counter to a request
     * @return a calendar object validated to conform to iTIP method DECLINECOUNTER
     */
    Calendar declineCounter(Calendar counter);
}
