/*
 * $Id: CalendarBuilderTest.java [Apr 5, 2004]
 *
 * Copyright (c) 2004 Ben Fortuna
 */
package net.fortuna.ical4j.data;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import net.fortuna.ical4j.FileOnlyFilter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test case for CalendarBuilder.
 *
 * @author benf
 */
public class CalendarEqualsTest extends TestCase {

    public final void testValidFiles() throws Exception {

        List testFiles = new ArrayList(Arrays.asList(new File("etc/samples/valid").listFiles(new FileOnlyFilter())));

        for (int i = 0; i < testFiles.size(); i++) {
            doTestCalendarEquals((File) testFiles.get(i), true);
        }
    }

    /**
     *
     * @param file
     * @param valid true if file is supposed to be valid
     * @throws Exception
     */ 
    private void doTestCalendarEquals(File file, boolean valid) throws Exception
    {
        System.setProperty("ical4j.unfolding.relaxed", "true");

        FileInputStream fin = new FileInputStream(file);
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = null;

        boolean errorOccurred = false;
        Exception exception = null;

        try {
            calendar = builder.build(fin);
        } catch (IOException e) {
            exception = e;
            errorOccurred = true;
        } catch (ParserException e) {
            exception = e;
            errorOccurred = true;
        }

        if (errorOccurred) {
            if (valid) {
                throw new AssertionFailedError("Calendar file " + file.toString() + " isn't valid:\n" + exception.getMessage());
            }
        }

        if (calendar != null) {
            try {
                calendar.validate();
            } catch (ValidationException e) {
                exception = e;
                errorOccurred = true;
            }

            if (errorOccurred) {
                if (valid) {
                    throw new AssertionFailedError("Calendar file " + file.toString() + " isn't valid:\n" + exception.getMessage());
                }
            } else {
                if (! valid) {
                    throw new AssertionFailedError("Calendar file " + file.toString() + " isn't valid and shouldn't validate.");
                }
            }

            fin = new FileInputStream(file);
            Calendar reparsedCalendar = null;

            try {
                reparsedCalendar = builder.build(fin);
            } catch (IOException e) {
                exception = e;
                errorOccurred = true;
            } catch (ParserException e) {
                exception = e;
                errorOccurred = true;
            }

            assertTrue("Parsed calendar isn't equal to itself!  : " + file.toString(),
                    calendar.equals(reparsedCalendar));
        }
    }
}
