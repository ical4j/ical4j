/*
 * This file is part of Touchbase.
 *
 * Created: [17/11/2008]
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

package net.fortuna.ical4j.model;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author fortuna
 *
 */
public class ParameterTest extends TestCase {

	private Parameter parameter;
	
	private String expectedName;
	
	private String expectedValue;
	
	/**
	 * @param parameter
	 * @param expectedValue
	 */
	public ParameterTest(String testMethod, Parameter parameter, String expectedName, String expectedValue) {
		super(testMethod);
		this.parameter = parameter;
		this.expectedName = expectedName;
		this.expectedValue = expectedValue;
	}
	
	/**
	 * 
	 */
	public void testGetName() {
		assertEquals(expectedName, parameter.getName());
	}
	
	/**
	 * 
	 */
	public void testGetValue() {
		assertEquals(expectedValue, parameter.getValue());
	}
	
	/**
	 * 
	 */
	public void testToString() {
		assertEquals(expectedName + "=" + expectedValue, parameter.toString());
	}
	
	/**
	 * @return
	 */
	public static TestSuite suite() throws Exception {
		TestSuite suite = new TestSuite();
		
		Parameter p = new Parameter("name") {
			public String getValue() {
				return "value";
			}
		};
		
		suite.addTest(new ParameterTest("testGetName",p, "name", "value"));
		suite.addTest(new ParameterTest("testGetValue",p, "name", "value"));
		suite.addTest(new ParameterTest("testToString",p, "name", "value"));
		return suite;
	}
}
