/**
 * Copyright (c) 2009, Ben Fortuna
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
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactoryImpl;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VAvailability;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Constants;
import net.fortuna.ical4j.util.Strings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <pre>
 * $Id$
 *
 * Created: Apr 5, 2004
 * </pre>
 *
 * Parses and builds an iCalendar model from an input stream. Note that this class is not thread-safe.
 * @version 2.0
 * @author Ben Fortuna
 */
public class CalendarBuilder {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Log log = LogFactory.getLog(CalendarBuilder.class);

    private final CalendarParser parser;
    
    private final ContentHandler contentHandler;

    private final TimeZoneRegistry registry;
    
    private List datesMissingTimezones;

    protected Calendar calendar;

    protected Component component;

    protected Component subComponent;

    protected Property property;

    /**
     * Default constructor.
     */
    public CalendarBuilder() {
        this(CalendarParserFactory.getInstance().createParser(),
                TimeZoneRegistryFactory.getInstance().createRegistry());
    }

    /**
     * Constructs a new calendar builder using the specified calendar parser.
     * @param parser a calendar parser used to parse calendar files
     */
    public CalendarBuilder(final CalendarParser parser) {
        this(parser, TimeZoneRegistryFactory.getInstance().createRegistry());
    }

    /**
     * Constructs a new calendar builder using the specified timezone registry.
     * @param parser a calendar parser used to parse calendar files
     */
    public CalendarBuilder(final TimeZoneRegistry registry) {
        this(CalendarParserFactory.getInstance().createParser(), registry);
    }

    /**
     * Constructs a new instance using the specified parser and registry.
     * @param parser a calendar parser used to construct the calendar
     * @param registry a timezone registry used to retrieve {@link TimeZone}s and register additional timezone information found
     * in the calendar
     */
    public CalendarBuilder(final CalendarParser parser,
            final TimeZoneRegistry registry) {
        this.parser = parser;
        this.registry = registry;
        this.contentHandler = new ContentHandlerImpl();
    }

    /**
     * Builds an iCalendar model from the specified input stream.
     * @param in
     * @return a calendar
     * @throws IOException
     * @throws ParserException
     */
    public Calendar build(final InputStream in) throws IOException,
            ParserException {
        return build(new InputStreamReader(in, DEFAULT_CHARSET));
    }

    /**
     * Builds an iCalendar model from the specified reader. An <code>UnfoldingReader</code> is applied to the
     * specified reader to ensure the data stream is correctly unfolded where appropriate.
     * @param in
     * @return a calendar
     * @throws IOException
     * @throws ParserException
     */
    public Calendar build(final Reader in) throws IOException, ParserException {
        return build(new UnfoldingReader(in));
    }

    /**
     * Build an iCalendar model by parsing data from the specified reader.
     * @param uin an unfolding reader to read data from
     * @return a calendar model
     * @throws IOException
     * @throws ParserException
     */
    public Calendar build(final UnfoldingReader uin) throws IOException,
            ParserException {
        // re-initialise..
        calendar = null;
        component = null;
        subComponent = null;
        property = null;
        datesMissingTimezones = new ArrayList();

        parser.parse(uin, contentHandler);

        if(datesMissingTimezones.size()>0 && registry!=null)
            resolveTimezones();
        
        return calendar;
    }

    private class ContentHandlerImpl implements ContentHandler {
        /*
         * (non-Javadoc)
         * @see net.fortuna.ical4j.data.ContentHandler#endCalendar()
         */
        public void endCalendar() {
            // do nothing..
        }

        /*
         * (non-Javadoc)
         * @see net.fortuna.ical4j.data.ContentHandler#endComponent(java.lang.String)
         */
        public void endComponent(final String name) {
            if (component != null) {
                if (subComponent != null) {
                    if (component instanceof VTimeZone) {
                        ((VTimeZone) component).getObservances().add(subComponent);
                    }
                    else if (component instanceof VEvent) {
                        ((VEvent) component).getAlarms().add(subComponent);
                    }
                    else if (component instanceof VToDo) {
                        ((VToDo) component).getAlarms().add(subComponent);
                    }
                    else if (component instanceof VAvailability) {
                        ((VAvailability) component).getAvailable().add(subComponent);
                    }
                    subComponent = null;
                }
                else {
                    calendar.getComponents().add(component);
                    if (component instanceof VTimeZone && registry != null) {
                        // register the timezone for use with iCalendar objects..
                        registry.register(new TimeZone((VTimeZone) component));
                    }
                    component = null;
                }
            }
        }

        /*
         * (non-Javadoc)
         * @see net.fortuna.ical4j.data.ContentHandler#endProperty(java.lang.String)
         */
        public void endProperty(final String name) {
            if (property != null) {
                // replace with a constant instance if applicable..
                property = Constants.forProperty(property);
                if (component != null) {
                    if (subComponent != null) {
                        subComponent.getProperties().add(property);
                    }
                    else {
                        component.getProperties().add(property);
                    }
                }
                else if (calendar != null) {
                    calendar.getProperties().add(property);
                }

                property = null;
            }
        }

        /*
         * (non-Javadoc)
         * @see net.fortuna.ical4j.data.ContentHandler#parameter(java.lang.String, java.lang.String)
         */
        public void parameter(final String name, final String value) throws URISyntaxException {
            if (property != null) {
                // parameter names are case-insensitive, but convert to upper case to simplify further processing
                final Parameter param = ParameterFactoryImpl.getInstance().createParameter(name.toUpperCase(), value);
                property.getParameters().add(param);
                if (param instanceof TzId && registry != null) {
                    final TimeZone timezone = registry.getTimeZone(param.getValue());
                    if (timezone != null) {
                        try {
                            ((DateProperty) property).setTimeZone(timezone);
                        }
                        catch (ClassCastException e) {
                            try {
                                ((DateListProperty) property).setTimeZone(timezone);
                            }
                            catch (ClassCastException e2) {
                                if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
                                    log.warn("Error setting timezone [" + param
                                            + "] on property [" + property.getName()
                                            + "]", e);
                                }
                                else {
                                    throw e2;
                                }
                            }
                        }
                    } else {
                        // VTIMEZONE may be defined later, so so keep
                        // track of dates until all components have been
                        // parsed, and then try again later
                        datesMissingTimezones.add(property);
                    }
                }
            }
        }

        /*
         * (non-Javadoc)
         * @see net.fortuna.ical4j.data.ContentHandler#propertyValue(java.lang.String)
         */
        public void propertyValue(final String value) throws URISyntaxException,
                ParseException, IOException {
            
            if (property != null) {
                if (property instanceof Escapable) {
                    property.setValue(Strings.unescape(value));
                }
                else {
                    property.setValue(value);
                }
            }
        }

        /*
         * (non-Javadoc)
         * @see net.fortuna.ical4j.data.ContentHandler#startCalendar()
         */
        public void startCalendar() {
            calendar = new Calendar();
        }

        /*
         * (non-Javadoc)
         * @see net.fortuna.ical4j.data.ContentHandler#startComponent(java.lang.String)
         */
        public void startComponent(final String name) {
            if (component != null) {
                subComponent = ComponentFactory.getInstance().createComponent(name);
            }
            else {
                component = ComponentFactory.getInstance().createComponent(name);
            }
        }

        /*
         * (non-Javadoc)
         * @see net.fortuna.ical4j.data.ContentHandler#startProperty(java.lang.String)
         */
        public void startProperty(final String name) {
            // property names are case-insensitive, but convert to upper case to simplify further processing
            property = PropertyFactoryImpl.getInstance().createProperty(
                    name.toUpperCase());
        }
    }

    /**
     * Returns the timezone registry used in the construction of calendars.
     * @return a timezone registry
     */
    public final TimeZoneRegistry getRegistry() {
        return registry;
    }
    
    private void resolveTimezones() 
        throws IOException {
        
        // Go through each property and try to resolve the TZID.
        for (final Iterator it = datesMissingTimezones.iterator();it.hasNext();) {
            final Property property = (Property) it.next();
            final Parameter tzParam = property.getParameter(Parameter.TZID);

            // tzParam might be null: 
            if (tzParam == null) continue;
            
            //lookup timezone
            final TimeZone timezone = registry.getTimeZone(tzParam.getValue());
            
            // If timezone found, then update date property
            if (timezone != null) {
                // Get the String representation of date(s) as
                // we will need this after changing the timezone
                final String strDate = property.getValue();
                
                // Change the timezone
                if(property instanceof DateProperty)
                    ((DateProperty) property).setTimeZone(timezone);
                else if(property instanceof DateListProperty)
                    ((DateListProperty) property).setTimeZone(timezone);
                    
                // Reset value
                try {
                    property.setValue(strDate);
                } catch (ParseException e) {
                    // shouldn't happen as its already been parsed
                    throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    // shouldn't happen as its already been parsed
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
