/*
 * This file is part of Touchbase.
 *
 * Created: [18/11/2008]
 *
 * Copyright (c) 2008, Ben Fortuna
 *
 * Touchbase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Touchbase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Touchbase.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.fortuna.ical4j.model.parameter;

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterTest;

/**
 * @author fortuna
 *
 */
public class PartStatTest extends ParameterTest {

	/**
	 * @param testMethod
	 * @param parameter
	 * @param expectedName
	 * @param expectedValue
	 */
	public PartStatTest(String testMethod, PartStat partStat,
			String expectedValue) {
		super(testMethod, partStat, Parameter.PARTSTAT, expectedValue);
	}

	/**
	 * @return
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new PartStatTest("testGetValue", PartStat.ACCEPTED, "ACCEPTED"));
		return suite;
	}
}
