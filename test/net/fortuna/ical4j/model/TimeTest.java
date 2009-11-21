package net.fortuna.ical4j.model;

import java.text.ParseException;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author fortuna
 *
 */
public class TimeTest extends TestCase {
    
    private final TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testTimeString() throws ParseException {
        Time time = new Time("020000", registry.getTimeZone("America/Los_Angeles"));
        Assert.assertEquals("020000", time.toString());
    }
}
