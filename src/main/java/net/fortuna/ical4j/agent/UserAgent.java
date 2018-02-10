package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Property;

/**
 * Created by fortuna on 19/07/2017.
 */
public interface UserAgent {

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
    Property getRole();

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
}
