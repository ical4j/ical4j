package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.transform.PublishTransformer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * Created by fortuna on 19/07/2017.
 */
public class OrganizerAgentImpl extends AbstractUserAgent implements PublishAgent, RequestAgent, AddAgent, CancelAgent,
    DeclineCounterAgent {

    public OrganizerAgentImpl(Property role) {
        super(role);
    }

    @Override
    public Calendar publish(Calendar object) throws ParseException, IOException, URISyntaxException {
        PublishTransformer transformer = new PublishTransformer();
        return transformer.transform(object);
    }

    @Override
    public Calendar add(Calendar object) throws Exception {
        return null;
    }

    @Override
    public Calendar cancel(Calendar object) throws Exception {
        return null;
    }

    @Override
    public Calendar declineCounter(Calendar object) throws Exception {
        return null;
    }

    @Override
    public Calendar request(Calendar object) throws Exception {
        return null;
    }
}
