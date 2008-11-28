/*
 * This file is part of Touchbase.
 *
 * Created: [24/11/2008]
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

package net.fortuna.ical4j.model.property;

import java.text.ParseException;

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.PropertyTest;

/**
 * @author fortuna
 *
 */
public class DtStampTest extends PropertyTest {

	/**
	 * @param property
	 * @param expectedValue
	 */
	public DtStampTest(DtStamp dtStamp, String expectedValue) {
		super(dtStamp, expectedValue);
	}

	/**
	 * @param testMethod
	 * @param property
	 */
	public DtStampTest(String testMethod, DtStamp property) {
		super(testMethod, property);
	}

	/**
	 * @return
	 * @throws ParseException 
	 */
	public static TestSuite suite() throws ParseException {
		TestSuite suite = new TestSuite();
		DtStamp dtStamp = new DtStamp("20081124T090000");
		suite.addTest(new DtStampTest(dtStamp, "20081124T090000"));
		suite.addTest(new DtStampTest("testEquals", dtStamp));
		suite.addTest(new DtStampTest("testValidation", dtStamp));
		return suite;
	}
}
