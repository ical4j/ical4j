/*
 * $Id: AltRepTest.java [23-Apr-2004]
 *
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URI;
import java.net.URISyntaxException;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;

import junit.framework.TestCase;


/**
 * Test case for AltRep.
 * @author benfortuna
 */
public class AltRepTest extends TestCase {

    /*
     * Class to test for void AltRep(String)
     */
    public void testAltRepString() throws URISyntaxException {
        
        try {
            new AltRep("::...invalid...");
            
            fail("URISyntaxException not thrown!");
        }
        catch (URISyntaxException use) {
            // test success.
        }
        
        AltRep ar = (AltRep) ParameterFactoryImpl.getInstance().createParameter(Parameter.ALTREP, "mailto:valid@test.com");
        
        assertNotNull(ar.getUri());
    }

    /*
     * Class to test for void AltRep(URI)
     */
    public void testAltRepURI() throws URISyntaxException {
        
        AltRep ar = new AltRep(new URI("mailto:valid@test.com"));
        
        assertNotNull(ar.getUri());
    }

}
