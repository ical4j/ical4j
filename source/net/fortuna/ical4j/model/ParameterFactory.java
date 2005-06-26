/*
 * $Id$ [05-Apr-2004]
 * Created on 14/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.fortuna.ical4j.model;

import java.net.URISyntaxException;


/**
 * Implementors provide parameter creation services.
 * 
 * @author Ben Fortuna
 */
public interface ParameterFactory {

    /**
     * Returns a parameter instance of the appropriate type with the specified value.
     * @param name a parameter names that identifies the parameter type
     * @param value a value to assign to the returned parameter
     * @return a parameter instance, or null if this factory is unable to create an
     * appropriate parameter
     */
    Parameter createParameter(String name, String value) throws URISyntaxException;
}
