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
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.Completed;
import net.fortuna.ical4j.model.property.Contact;
import net.fortuna.ical4j.model.property.Country;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.ExtendedAddress;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Locality;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.LocationType;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Name;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Postalcode;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Region;
import net.fortuna.ical4j.model.property.RelatedTo;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.RequestStatus;
import net.fortuna.ical4j.model.property.Resources;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.StreetAddress;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Tel;
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
 * $Id$ [05-Apr-2004]
 *
 * A factory for creating iCalendar properties. Note that if relaxed parsing is enabled (via specifying the system
 * property: icalj.parsing.relaxed=true) illegal property names are allowed.
 * @author Ben Fortuna
 */
public final class PropertyFactoryImpl extends AbstractContentFactory implements
        PropertyFactory {

    private static PropertyFactoryImpl instance = new PropertyFactoryImpl();

    /**
     * Constructor made private to prevent instantiation.
     */
    private PropertyFactoryImpl() {
        factories.put(Property.ACTION, createActionFactory());
        factories.put(Property.ATTACH, createAttachFactory());
        factories.put(Property.ATTENDEE, createAttendeeFactory());
        factories.put(Property.CALSCALE, createCalScaleFactory());
        factories.put(Property.CATEGORIES, createCategoriesFactory());
        factories.put(Property.CLASS, createClazzFactory());
        factories.put(Property.COMMENT, createCommentFactory());
        factories.put(Property.COMPLETED, createCompletedFactory());
        factories.put(Property.CONTACT, createContactFactory());
        factories.put(Property.COUNTRY, createCountryFactory());
        factories.put(Property.CREATED, createCreatedFactory());
        factories.put(Property.DESCRIPTION, createDescriptionFactory());
        factories.put(Property.DTEND, createDtEndFactory());
        factories.put(Property.DTSTAMP, createDtStampFactory());
        factories.put(Property.DTSTART, createDtStartFactory());
        factories.put(Property.DUE, createDueFactory());
        factories.put(Property.DURATION, createDurationFactory());
        factories.put(Property.EXDATE, createExDateFactory());
        factories.put(Property.EXRULE, createExRuleFactory());
        factories.put(Property.EXTENDED_ADDRESS, createExtendedAddressFactory());
        factories.put(Property.FREEBUSY, createFreeBusyFactory());
        factories.put(Property.GEO, createGeoFactory());
        factories.put(Property.LAST_MODIFIED, createLastModifiedFactory());
        factories.put(Property.LOCALITY, createLocalityFactory());
        factories.put(Property.LOCATION, createLocationFactory());
        factories.put(Property.LOCATION_TYPE, createLocationTypeFactory());
        factories.put(Property.METHOD, createMethodFactory());
        factories.put(Property.NAME, createNameFactory());
        factories.put(Property.ORGANIZER, createOrganizerFactory());
        factories
                .put(Property.PERCENT_COMPLETE, createPercentCompleteFactory());
        factories.put(Property.POSTALCODE, createPostalcodeFactory());
        factories.put(Property.PRIORITY, createPriorityFactory());
        factories.put(Property.PRODID, createProdIdFactory());
        factories.put(Property.RDATE, createRDateFactory());
        factories.put(Property.RECURRENCE_ID, createRecurrenceIdFactory());
        factories.put(Property.REGION, createRegionFactory());
        factories.put(Property.RELATED_TO, createRelatedToFactory());
        factories.put(Property.REPEAT, createRepeatFactory());
        factories.put(Property.REQUEST_STATUS, createRequestStatusFactory());
        factories.put(Property.RESOURCES, createResourcesFactory());
        factories.put(Property.RRULE, createRRuleFactory());
        factories.put(Property.SEQUENCE, createSequenceFactory());
        factories.put(Property.STATUS, createStatusFactory());
        factories.put(Property.STREET_ADDRESS, createStreetAddressFactory());
        factories.put(Property.SUMMARY, createSummaryFactory());
        factories.put(Property.TEL, createTelFactory());
        factories.put(Property.TRANSP, createTranspFactory());
        factories.put(Property.TRIGGER, createTriggerFactory());
        factories.put(Property.TZID, createTzIdFactory());
        factories.put(Property.TZNAME, createTzNameFactory());
        factories.put(Property.TZOFFSETFROM, createTzOffsetFromFactory());
        factories.put(Property.TZOFFSETTO, createTzOffsetToFactory());
        factories.put(Property.TZURL, createTzUrlFactory());
        factories.put(Property.UID, createUidFactory());
        factories.put(Property.URL, createUrlFactory());
        factories.put(Property.VERSION, createVersionFactory());
    }

    /**
     * @return
     */
    private PropertyFactory createActionFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Action(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Action();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createAttachFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Attach(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Attach();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createAttendeeFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Attendee(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Attendee();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createCalScaleFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new CalScale(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new CalScale();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createCategoriesFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Categories(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Categories();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createClazzFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Clazz(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Clazz();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createCommentFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Comment(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Comment();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createCompletedFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Completed(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Completed();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createContactFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Contact(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Contact();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createCountryFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Country(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Country();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createCreatedFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Created(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Created();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createDescriptionFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Description(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Description();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createDtEndFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new DtEnd(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new DtEnd();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createDtStampFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new DtStamp(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new DtStamp();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createDtStartFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new DtStart(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new DtStart();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createDueFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Due(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Due();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createDurationFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Duration(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Duration();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createExDateFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new ExDate(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new ExDate();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createExRuleFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new ExRule(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new ExRule();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createExtendedAddressFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new ExtendedAddress(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new ExtendedAddress();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createFreeBusyFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new FreeBusy(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new FreeBusy();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createGeoFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Geo(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Geo();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createLastModifiedFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new LastModified(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new LastModified();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createLocalityFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Locality(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Locality();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createLocationFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Location(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Location();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createLocationTypeFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new LocationType(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new LocationType();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createMethodFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Method(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Method();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createNameFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Name(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Name();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createOrganizerFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Organizer(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Organizer();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createPercentCompleteFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new PercentComplete(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new PercentComplete();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createPostalcodeFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Postalcode(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Postalcode();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createPriorityFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Priority(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Priority();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createProdIdFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new ProdId(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new ProdId();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createRDateFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new RDate(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new RDate();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createRecurrenceIdFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new RecurrenceId(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new RecurrenceId();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createRegionFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Region(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Region();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createRelatedToFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new RelatedTo(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new RelatedTo();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createRepeatFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Repeat(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Repeat();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createRequestStatusFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new RequestStatus(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new RequestStatus();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createResourcesFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Resources(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Resources();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createRRuleFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new RRule(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new RRule();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createSequenceFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Sequence(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Sequence();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createStatusFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Status(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Status();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createStreetAddressFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new StreetAddress(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new StreetAddress();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createSummaryFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Summary(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Summary();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createTelFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Tel(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Tel();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createTranspFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Transp(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Transp();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createTriggerFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Trigger(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Trigger();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createTzIdFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new TzId(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new TzId();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createTzNameFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new TzName(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new TzName();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createTzOffsetFromFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new TzOffsetFrom(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new TzOffsetFrom();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createTzOffsetToFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new TzOffsetTo(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new TzOffsetTo();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createTzUrlFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new TzUrl(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new TzUrl();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createUidFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Uid(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Uid();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createUrlFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Url(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Url();
            }
        };
    }

    /**
     * @return
     */
    private PropertyFactory createVersionFactory() {
        return new PropertyFactory() {
            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String,
             * net.fortuna.ical4j.model.ParameterList, java.lang.String)
             */
            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Version(parameters, value);
            }

            /*
             * (non-Javadoc)
             * @see net.fortuna.ical4j.model.PropertyFactory#createProperty(java.lang.String)
             */
            public Property createProperty(final String name) {
                return new Version();
            }
        };
    }

    /**
     * @return Returns the instance.
     */
    public static PropertyFactoryImpl getInstance() {
        return instance;
    }

    /**
     * Creates an uninitialised property.
     * @param name name of the property
     * @return a property
     */
    public Property createProperty(final String name) {
        final PropertyFactory factory = (PropertyFactory) factories.get(name);
        if (factory != null) {
            return factory.createProperty(name);
        }
        else if (isExperimentalName(name)) {
            return new XProperty(name);
        }
        else if (allowIllegalNames()) {
            return new XProperty(name);
        }
        else {
            throw new IllegalArgumentException("Illegal property [" + name
                    + "]");
        }
    }

    /**
     * Creates a property.
     * @param name name of the property
     * @param parameters a list of property parameters
     * @param value a property value
     * @return a component
     */
    public Property createProperty(final String name,
            final ParameterList parameters, final String value)
            throws IOException, URISyntaxException, ParseException {

        final PropertyFactory factory = (PropertyFactory) factories.get(name);
        if (factory != null) {
            return factory.createProperty(name, parameters, value);
        }
        else if (isExperimentalName(name)) {
            return new XProperty(name, parameters, value);
        }
        else if (allowIllegalNames()) {
            return new XProperty(name, parameters, value);
        }
        else {
            throw new IllegalArgumentException("Illegal property [" + name
                    + "]");
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
