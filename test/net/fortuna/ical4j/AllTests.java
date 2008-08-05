package net.fortuna.ical4j;

import java.text.ParseException;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.fortuna.ical4j.data.CalendarBuilderTest;
import net.fortuna.ical4j.data.CalendarEqualsTest;
import net.fortuna.ical4j.data.CalendarParserImplTest;
import net.fortuna.ical4j.data.HCalendarParserTest;
import net.fortuna.ical4j.filter.FilterTest;
import net.fortuna.ical4j.filter.HasPropertyRuleTest;
import net.fortuna.ical4j.filter.PeriodRuleTest;
import net.fortuna.ical4j.model.AddressListTest;
import net.fortuna.ical4j.model.CalendarTest;
import net.fortuna.ical4j.model.DateTest;
import net.fortuna.ical4j.model.DateTimeTest;
import net.fortuna.ical4j.model.DurTest;
import net.fortuna.ical4j.model.IndexedComponentListTest;
import net.fortuna.ical4j.model.IndexedPropertyListTest;
import net.fortuna.ical4j.model.NumberListTest;
import net.fortuna.ical4j.model.ParameterFactoryImplTest;
import net.fortuna.ical4j.model.PeriodListTest;
import net.fortuna.ical4j.model.PeriodTest;
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.model.RecurTest;
import net.fortuna.ical4j.model.ResourceListTest;
import net.fortuna.ical4j.model.TimeZoneTest;
import net.fortuna.ical4j.model.UtcOffsetTest;
import net.fortuna.ical4j.model.WeekDayTest;
import net.fortuna.ical4j.model.component.VAlarmTest;
import net.fortuna.ical4j.model.component.VEventTest;
import net.fortuna.ical4j.model.component.VFreeBusyTest;
import net.fortuna.ical4j.model.component.VTimeZoneTest;
import net.fortuna.ical4j.model.component.XComponentTest;
import net.fortuna.ical4j.model.parameter.AltRepTest;
import net.fortuna.ical4j.model.parameter.CnTest;
import net.fortuna.ical4j.model.parameter.CuTypeTest;
import net.fortuna.ical4j.model.parameter.TzIdTest;
import net.fortuna.ical4j.model.property.AttachTest;
import net.fortuna.ical4j.model.property.AttendeeTest;
import net.fortuna.ical4j.model.property.CalScaleTest;
import net.fortuna.ical4j.model.property.DtEndTest;
import net.fortuna.ical4j.model.property.DtStartTest;
import net.fortuna.ical4j.model.property.ExDateTest;
import net.fortuna.ical4j.model.property.FreeBusyTest;
import net.fortuna.ical4j.model.property.GeoTest;
import net.fortuna.ical4j.model.property.LocationTest;
import net.fortuna.ical4j.model.property.OrganizerTest;
import net.fortuna.ical4j.model.property.SummaryTest;
import net.fortuna.ical4j.model.property.TriggerTest;
import net.fortuna.ical4j.model.property.VersionTest;
import net.fortuna.ical4j.model.property.XPropertyTest;
import net.fortuna.ical4j.util.StringsTest;

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
    public static Test suite() throws ParseException {
        TestSuite suite = new TestSuite(AllTests.class.getSimpleName());

        // data tests
        suite.addTest(CalendarBuilderTest.suite());
        suite.addTest(CalendarEqualsTest.suite());
//        suite.addTest(CalendarOutputterTest.suite());
        suite.addTestSuite(CalendarParserImplTest.class);
        suite.addTestSuite(HCalendarParserTest.class);

        // filter tests..
        suite.addTestSuite(FilterTest.class);
        suite.addTestSuite(HasPropertyRuleTest.class);
        suite.addTestSuite(PeriodRuleTest.class);
        
        // model tests
        suite.addTestSuite(AddressListTest.class);
        suite.addTestSuite(CalendarTest.class);
        suite.addTestSuite(DateTest.class);
        suite.addTest(DateTimeTest.suite());
        suite.addTestSuite(DurTest.class);
        suite.addTestSuite(IndexedComponentListTest.class);
        suite.addTestSuite(IndexedPropertyListTest.class);
        suite.addTestSuite(NumberListTest.class);
        suite.addTestSuite(ParameterFactoryImplTest.class);
        suite.addTestSuite(PeriodListTest.class);
        suite.addTestSuite(PeriodTest.class);
        suite.addTestSuite(RecurTest.class);
        suite.addTestSuite(ResourceListTest.class);
        suite.addTestSuite(TimeZoneTest.class);
        suite.addTestSuite(WeekDayTest.class);
        suite.addTestSuite(PropertyTest.class);
        suite.addTestSuite(UtcOffsetTest.class);

        // component tests
        suite.addTestSuite(VAlarmTest.class);
        suite.addTestSuite(VEventTest.class);
        suite.addTestSuite(VFreeBusyTest.class);
        suite.addTestSuite(VTimeZoneTest.class);
        suite.addTestSuite(XComponentTest.class);

        // parameter tests
        suite.addTestSuite(AltRepTest.class);
        suite.addTestSuite(CnTest.class);
        suite.addTestSuite(CuTypeTest.class);
        suite.addTestSuite(TzIdTest.class);

        // property tests
        suite.addTestSuite(AttendeeTest.class);
        suite.addTestSuite(AttachTest.class);
        suite.addTestSuite(CalScaleTest.class);
        suite.addTestSuite(DtEndTest.class);
        suite.addTestSuite(DtStartTest.class);
        suite.addTestSuite(ExDateTest.class);
        suite.addTestSuite(FreeBusyTest.class);
        suite.addTestSuite(LocationTest.class);
        suite.addTestSuite(OrganizerTest.class);
        suite.addTestSuite(SummaryTest.class);
        suite.addTestSuite(TriggerTest.class);
        suite.addTestSuite(VersionTest.class);
        suite.addTestSuite(GeoTest.class);
        suite.addTestSuite(XPropertyTest.class);

        // util tests
        suite.addTestSuite(StringsTest.class);

        return suite;
    }

}
