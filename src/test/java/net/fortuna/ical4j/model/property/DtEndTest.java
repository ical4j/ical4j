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

import junit.framework.TestSuite;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyTest;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Collections;

/**
 * $Id$
 *
 * Created on 30/06/2005
 *
 * @author Ben Fortuna
 */
public class DtEndTest extends PropertyTest {

    private static Logger log = LoggerFactory.getLogger(DtEndTest.class);

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
        ParameterList dtendParams = new ParameterList(Collections.singletonList(Value.DATE_TIME));
        DtEnd<ZonedDateTime> dtEnd = new DtEnd<>(dtendParams, ZonedDateTime.now());

        // test validation..
        log.info(dtEnd.toString());
        suite.addTest(new DtEndTest("testValidation", dtEnd));

        //
        ParameterList newParams;
        newParams = (ParameterList) dtEnd.getParameters().replace(Value.DATE);
        dtEnd = new DtEnd<>(newParams, dtEnd.getDate());
        log.info(dtEnd.toString());
        suite.addTest(new DtEndTest("testValidationException", dtEnd));

        //
        dtEnd = new DtEnd<>(dtEnd.getParameters(), dtEnd.getDate());
        log.info(dtEnd.toString());
        suite.addTest(new DtEndTest("testValidation", dtEnd));

        //
        newParams = (ParameterList) dtEnd.getParameters().replace(Value.DATE);
        dtEnd = new DtEnd<>(newParams, dtEnd.getDate());
        log.info(dtEnd.toString());
        suite.addTest(new DtEndTest("testValidation", dtEnd));

        //
        newParams = (ParameterList) dtEnd.getParameters().removeAll(Parameter.VALUE);
        dtEnd = new DtEnd<>(newParams, ZonedDateTime.now());
        log.info(dtEnd.toString());
        suite.addTest(new DtEndTest("testValidationException", dtEnd));
        
        // disable relaxed parsing after copying invalid properties..
        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);

        return suite;
    }
}
