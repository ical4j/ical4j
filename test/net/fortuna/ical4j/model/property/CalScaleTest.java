/*
 * Created on 16/03/2005
 *
 * $Id$
 *
 * Copyright (c) 2005, Ben Fortuna
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.fortuna.ical4j.model.property;

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.PropertyTest;

/**
 * @author Ben Tests related to the property CALSCALE
 */
public class CalScaleTest extends PropertyTest {

    /**
     * @param property
     * @param expectedValue
     */
    public CalScaleTest(CalScale property, String expectedValue) {
        super(property, expectedValue);
    }

    /**
     * @param testMethod
     * @param property
     */
    public CalScaleTest(String testMethod, CalScale property) {
        super(testMethod, property);
    }

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new CalScaleTest(CalScale.GREGORIAN, "GREGORIAN"));
        suite.addTest(new CalScaleTest("testImmutable", CalScale.GREGORIAN));
        return suite;
    }
}
