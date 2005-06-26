/*
 * $Id$ [05-Apr-2004]
 *
 * Copyright (c) 2004, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.Completed;
import net.fortuna.ical4j.model.property.Contact;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.RelatedTo;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.RequestStatus;
import net.fortuna.ical4j.model.property.Resources;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzName;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.model.property.TzUrl;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;

/**
 * A factory for creating iCalendar properties.
 *
 * @author Ben Fortuna
 */
public final class PropertyFactoryImpl implements PropertyFactory {

    private static PropertyFactoryImpl instance = new PropertyFactoryImpl();
    
    private Map factories;

    /**
     * Constructor made private to prevent instantiation.
     */
    private PropertyFactoryImpl() {
        factories = new HashMap();
        factories.put(Property.ACTION, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Action(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Action();
            }
        });
        factories.put(Property.ATTACH, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Attach(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Attach();
            }
        });
        factories.put(Property.ATTENDEE, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Attendee(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Attendee();
            }
        });
        factories.put(Property.CALSCALE, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new CalScale(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new CalScale();
            }
        });
        factories.put(Property.CATEGORIES, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Categories(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Categories();
            }
        });
        factories.put(Property.CLASS, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Clazz(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Clazz();
            }
        });
        factories.put(Property.COMMENT, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Comment(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Comment();
            }
        });
        factories.put(Property.COMPLETED, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Completed(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Completed();
            }
        });
        factories.put(Property.CONTACT, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Contact(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Contact();
            }
        });
        factories.put(Property.CREATED, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Created(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Created();
            }
        });
        factories.put(Property.DESCRIPTION, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Description(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Description();
            }
        });
        factories.put(Property.DTEND, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new DtEnd(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new DtEnd();
            }
        });
        factories.put(Property.DTSTAMP, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new DtStamp(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new DtStamp();
            }
        });
        factories.put(Property.DTSTART, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new DtStart(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new DtStart();
            }
        });
        factories.put(Property.DUE, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Due(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Due();
            }
        });
        factories.put(Property.DURATION, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Duration(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Duration();
            }
        });
        factories.put(Property.EXDATE, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new ExDate(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new ExDate();
            }
        });
        factories.put(Property.EXRULE, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new ExRule(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new ExRule();
            }
        });
        factories.put(Property.FREEBUSY, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new FreeBusy(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new FreeBusy();
            }
        });
        factories.put(Property.GEO, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Geo(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Geo();
            }
        });
        factories.put(Property.LAST_MODIFIED, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new LastModified(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new LastModified();
            }
        });
        factories.put(Property.LOCATION, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Location(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Location();
            }
        });
        factories.put(Property.METHOD, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Method(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Method();
            }
        });
        factories.put(Property.ORGANIZER, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Organizer(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Organizer();
            }
        });
        factories.put(Property.PERCENT_COMPLETE, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new PercentComplete(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new PercentComplete();
            }
        });
        factories.put(Property.PRIORITY, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Priority(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Priority();
            }
        });
        factories.put(Property.PRODID, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new ProdId(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new ProdId();
            }
        });
        factories.put(Property.RDATE, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new RDate(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new RDate();
            }
        });
        factories.put(Property.RECURRENCE_ID, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new RecurrenceId(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new RecurrenceId();
            }
        });
        factories.put(Property.RELATED_TO, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new RelatedTo(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new RelatedTo();
            }
        });
        factories.put(Property.REPEAT, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Repeat(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Repeat();
            }
        });
        factories.put(Property.REQUEST_STATUS, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new RequestStatus(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new RequestStatus();
            }
        });
        factories.put(Property.RESOURCES, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Resources(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Resources();
            }
        });
        factories.put(Property.RRULE, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new RRule(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new RRule();
            }
        });
        factories.put(Property.SEQUENCE, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Sequence(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Sequence();
            }
        });
        factories.put(Property.STATUS, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Status(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Status();
            }
        });
        factories.put(Property.SUMMARY, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Summary(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Summary();
            }
        });
        factories.put(Property.TRANSP, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Transp(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Transp();
            }
        });
        factories.put(Property.TRIGGER, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Trigger(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Trigger();
            }
        });
        factories.put(Property.TZID, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new TzId(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new TzId();
            }
        });
        factories.put(Property.TZNAME, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new TzName(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new TzName();
            }
        });
        factories.put(Property.TZOFFSETFROM, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new TzOffsetFrom(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new TzOffsetFrom();
            }
        });
        factories.put(Property.TZOFFSETTO, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new TzOffsetTo(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new TzOffsetTo();
            }
        });
        factories.put(Property.TZURL, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new TzUrl(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new TzUrl();
            }
        });
        factories.put(Property.UID, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Uid(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Uid();
            }
        });
        factories.put(Property.URL, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Url(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Url();
            }
        });
        factories.put(Property.VERSION, new PropertyFactory() {
            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String, net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public final Property createProperty(final String name, final ParameterList parameters, final String value) throws IOException, URISyntaxException, ParseException {
                return new Version(parameters, value);
            }

            /* (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public final Property createProperty(final String name) {
                return new Version();
            }
        });
    }

    /**
     * @return Returns the instance.
     */
    public static PropertyFactoryImpl getInstance() {
        return instance;
    }

    /**
     * Creates an uninitialised property.
     * @param name
     *            name of the property
     * @return a property
     */
    public Property createProperty(final String name) {
        PropertyFactory factory = (PropertyFactory) factories.get(name);
        if (factory != null) {
            return factory.createProperty(name);
        }
        else if (isExperimentalName(name)) {
            return new XProperty(name);
        }
        else {
            throw new IllegalArgumentException("Invalid property name: " + name);
        }
    }

    /**
     * Creates a property.
     *
     * @param name
     *            name of the property
     * @param parameters
     *            a list of property parameters
     * @param value
     *            a property value
     * @return a component
     */
    public Property createProperty(final String name,
            final ParameterList parameters, final String value)
            throws IOException, URISyntaxException, ParseException {
        PropertyFactory factory = (PropertyFactory) factories.get(name);
        if (factory != null) {
            return factory.createProperty(name, parameters, value);
        }
        else if (isExperimentalName(name)) {
            return new XProperty(name, parameters, value);
        }
        else {
            throw new IllegalArgumentException("Invalid property name: " + name);
        }
    }
    
    /**
     * @param name
     * @return
     */
    private boolean isExperimentalName(final String name) {
        return name.startsWith(Property.EXPERIMENTAL_PREFIX)
                && name.length() > Property.EXPERIMENTAL_PREFIX.length();
    }
}