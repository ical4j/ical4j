/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created: [17/11/2008]
 *
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
		
		Parameter p = new Parameter("name", null) {
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
