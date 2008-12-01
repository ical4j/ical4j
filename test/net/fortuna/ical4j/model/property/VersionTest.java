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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import junit.framework.TestSuite;

import net.fortuna.ical4j.model.PropertyTest;

/**
 * @author Ben
 *
 * Tests related to the property VERSION
 */
public class VersionTest extends PropertyTest {

    private Version version;
    
    /**
     * @param property
     * @param expectedValue
     */
    public VersionTest(Version property, String expectedValue) {
        super(property, expectedValue);
        this.version = property;
    }

    /**
     * @param testMethod
     * @param property
     */
    public VersionTest(String testMethod, Version property) {
        super(testMethod, property);
        this.version = property;
    }

    /*
     * Test that the constant VERSION_2_0 is immutable.
     */
    public void testImmutable() throws IOException, URISyntaxException, ParseException {
        super.testImmutable();
        
        try {
            version.setMinVersion("3.0");
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
        
        try {
            version.setMaxVersion("5.0");
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
    }

    /**
     * @return
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new VersionTest(Version.VERSION_2_0, "2.0"));
        suite.addTest(new VersionTest("testImmutable", Version.VERSION_2_0));
        return suite;
    }
}
