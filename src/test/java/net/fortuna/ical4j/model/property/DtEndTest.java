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
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$
 *
 * Created on 30/06/2005
 *
 * @author Ben Fortuna
 */
public class DtEndTest extends PropertyTest {

    private static Log log = LogFactory.getLog(DtEndTest.class);

    /**
     * @param testMethod
     * @param property
     */
    public DtEndTest(String testMethod, DtEnd property) {
        super(testMethod, property);
    }

    /**
     * @return
     * @throws ParseException
     * @throws URISyntaxException
     * @throws IOException
     */
    public static TestSuite suite() throws IOException, URISyntaxException, ParseException {

        // enable relaxed parsing to allow copying of invalid properties..
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        
        TestSuite suite = new TestSuite();
        DtEnd dtEnd = new DtEnd(new DateTime());
        dtEnd.getParameters().replace(Value.DATE_TIME);

        // test validation..
        log.info(dtEnd);
        suite.addTest(new DtEndTest("testValidation", dtEnd));

        //
        dtEnd = (DtEnd) dtEnd.copy();
        dtEnd.getParameters().replace(Value.DATE);
        log.info(dtEnd);
        suite.addTest(new DtEndTest("testValidationException", dtEnd));

        //
        dtEnd = (DtEnd) dtEnd.copy();
        dtEnd.setUtc(true);
        log.info(dtEnd);
        suite.addTest(new DtEndTest("testValidation", dtEnd));

        //
        dtEnd = (DtEnd) dtEnd.copy();
        dtEnd.getParameters().replace(Value.DATE);
        log.info(dtEnd);
        suite.addTest(new DtEndTest("testValidation", dtEnd));

        //
        dtEnd = (DtEnd) dtEnd.copy();
        dtEnd.setDate(new Date());
        dtEnd.getParameters().remove(Value.DATE);
        log.info(dtEnd);
        suite.addTest(new DtEndTest("testValidationException", dtEnd));
        
        // disable relaxed parsing after copying invalid properties..
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);

        return suite;
    }
}
