package net.fortuna.ical4j;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.fortuna.ical4j.data.CalendarBuilderTest;
import net.fortuna.ical4j.model.CalendarTest;
import net.fortuna.ical4j.model.PeriodListTest;
import net.fortuna.ical4j.model.PeriodTest;
import net.fortuna.ical4j.model.RecurTest;
import net.fortuna.ical4j.model.WeekDayTest;
import net.fortuna.ical4j.model.component.VEventTest;
import net.fortuna.ical4j.model.component.VFreeBusyTest;
import net.fortuna.ical4j.model.component.VTimeZoneTest;
import net.fortuna.ical4j.model.parameter.AltRepTest;
import net.fortuna.ical4j.model.property.AttachTest;
import net.fortuna.ical4j.model.property.CalScaleTest;
import net.fortuna.ical4j.model.property.TriggerTest;
import net.fortuna.ical4j.model.property.VersionTest;
import net.fortuna.ical4j.util.StringUtilsTest;

/**
 * User: tobli
 * Date: Apr 13, 2005
 * Time: 11:22:55 AM
 */
public class AllTests extends TestSuite{
    /**
     * Test suite.
     * @return test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        // data tests
        suite.addTest(CalendarBuilderTest.suite());
//        suite.addTest(CalendarOutputterTest.suite());

        // model tests
        suite.addTestSuite(CalendarTest.class);
        suite.addTestSuite(PeriodTest.class);
        suite.addTestSuite(PeriodListTest.class);
        suite.addTestSuite(RecurTest.class);
        suite.addTestSuite(WeekDayTest.class);

        // component tests
        suite.addTestSuite(VEventTest.class);
        suite.addTestSuite(VFreeBusyTest.class);
        suite.addTestSuite(VTimeZoneTest.class);

        // parameter tests
        suite.addTestSuite(AltRepTest.class);

        // property tests
        suite.addTestSuite(AttachTest.class);
        suite.addTestSuite(CalScaleTest.class);
        suite.addTestSuite(TriggerTest.class);
        suite.addTestSuite(VersionTest.class);

        // util tests
        suite.addTestSuite(StringUtilsTest.class);

        return suite;
    }

}
