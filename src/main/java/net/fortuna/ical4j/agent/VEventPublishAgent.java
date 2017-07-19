package net.fortuna.ical4j.agent;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Method;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * Created by fortuna on 19/07/2017.
 */
public class VEventPublishAgent extends AbstractUserAgent implements PublishAgent<VEvent> {

    public VEventPublishAgent(Property role) {
        super(role, Method.PUBLISH);
    }

    @Override
    public VEvent publish(VEvent object) throws ParseException, IOException, URISyntaxException {
        return (VEvent) object.copy();
    }
}
