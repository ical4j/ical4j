/*
 * Created on 15/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.fortuna.ical4j.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 * @author Ben_Fortuna
 */
public class ParameterFactoryImplTest extends TestCase {
    
    private static Log log = LogFactory.getLog(ParameterFactoryImplTest.class);

	/**
	 * @throws Exception
	 */
	public void testCreateParameter() throws Exception {
        Parameter p = ParameterFactoryImpl.getInstance().createParameter(Parameter.ALTREP, "Test");
        assertNotNull(p);
        log.info(p);
	}

    /**
     * @throws Exception
     */
    public void testCreateExperimentalParameter() throws Exception {
        Parameter p = ParameterFactoryImpl.getInstance().createParameter("X-my-param", "Test");
        assertNotNull(p);
        log.info(p);
    }

    /**
     * @throws Exception
     */
    public void testInvalidParameter() throws Exception {
        try {
            ParameterFactoryImpl.getInstance().createParameter("my-param", "Test");
            fail("Should throw an IllegalArgumentException");
        }
        catch (IllegalArgumentException iae) {
            log.info("Invalid parameter", iae);
        }
    }
}
