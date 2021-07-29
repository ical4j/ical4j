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
/*
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.fortuna.ical4j.model;

import junit.framework.TestCase;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 15/06/2005
 *
 * @author Ben_Fortuna
 */
public class ParameterFactoryImplTest extends TestCase {

    private Logger log = LoggerFactory.getLogger(ParameterFactoryImplTest.class);

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        CompatibilityHints.clearHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING);
    }

    /**
     * @throws Exception
     */
    public void testCreateParameter() throws Exception {
        Parameter p = new ParameterFactoryImpl().createParameter(
                Parameter.ALTREP, "Test");
        assertNotNull(p);
        log.info(p.toString());
    }

    /**
     * @throws Exception
     */
    public void testCreateExperimentalParameter() throws Exception {
        Parameter p = new ParameterFactoryImpl().createParameter(
                "X-my-param", "Test");
        assertNotNull(p);
        log.info(p.toString());
    }

    /**
     * @throws Exception
     */
    public void testInvalidParameter() throws Exception {
        try {
            new ParameterFactoryImpl().createParameter("my-param",
                    "Test");
            fail("Should throw an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            log.debug("Invalid parameter", iae);
        }
    }

    /**
     * @throws Exception
     */
    public void testRelaxedParsing() throws Exception {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);

        new ParameterFactoryImpl().createParameter("VVENUE", "My Place");
    }
}
