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

import junit.framework.TestCase;
import net.fortuna.ical4j.model.parameter.Value;

/**
 * @author Ben
 *
 * Tests related to the property VERSION
 */
public class VersionTest extends TestCase {

    /*
     * Test that the constant VERSION_2_0 is immutable.
     */
    public void testVersion_2_0Immutable() {
        try {
            Version.VERSION_2_0.getParameters().add(Value.DATE);
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
        
        try {
            Version.VERSION_2_0.setMinVersion("3.0");
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
        
        try {
            Version.VERSION_2_0.setMaxVersion("5.0");
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
        
        try {
            Version.VERSION_2_0.setValue("5.0");
            fail("UnsupportedOperationException should be thrown");
        }
        catch (UnsupportedOperationException uoe) {
        }
    }

}
