/**
 * Copyright (c) 2010, Ben Fortuna
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
 * A factory for creating iCalendar properties. Note that if relaxed parsing is enabled (via specifying the system
 * property: icalj.parsing.relaxed=true) illegal property names are allowed.
 * 
 * @author Ben Fortuna
 * 
 * $Id$ [05-Apr-2004]
 */
public class PropertyFactoryImpl extends AbstractContentFactory implements PropertyFactory {

    private static final long serialVersionUID = -7174232004486979641L;
    
    private static PropertyFactoryImpl instance = new PropertyFactoryImpl();

    /**
     * Constructor made private to prevent instantiation.
     */
    protected PropertyFactoryImpl() {
        registerDefaultFactory(Property.ACTION, createActionFactory());
        registerDefaultFactory(Property.ATTACH, createAttachFactory());
        registerDefaultFactory(Property.ATTENDEE, createAttendeeFactory());
        registerDefaultFactory(Property.CALSCALE, createCalScaleFactory());
        registerDefaultFactory(Property.CATEGORIES, createCategoriesFactory());
        registerDefaultFactory(Property.CLASS, createClazzFactory());
        registerDefaultFactory(Property.COMMENT, createCommentFactory());
        registerDefaultFactory(Property.COMPLETED, createCompletedFactory());
        registerDefaultFactory(Property.CONTACT, createContactFactory());
        registerDefaultFactory(Property.COUNTRY, createCountryFactory());
        registerDefaultFactory(Property.CREATED, createCreatedFactory());
        registerDefaultFactory(Property.DESCRIPTION, createDescriptionFactory());
        registerDefaultFactory(Property.DTEND, createDtEndFactory());
        registerDefaultFactory(Property.DTSTAMP, createDtStampFactory());
        registerDefaultFactory(Property.DTSTART, createDtStartFactory());
        registerDefaultFactory(Property.DUE, createDueFactory());
        registerDefaultFactory(Property.DURATION, createDurationFactory());
        registerDefaultFactory(Property.EXDATE, createExDateFactory());
        registerDefaultFactory(Property.EXRULE, createExRuleFactory());
        registerDefaultFactory(Property.EXTENDED_ADDRESS, createExtendedAddressFactory());
        registerDefaultFactory(Property.FREEBUSY, createFreeBusyFactory());
        registerDefaultFactory(Property.GEO, createGeoFactory());
        registerDefaultFactory(Property.LAST_MODIFIED, createLastModifiedFactory());
        registerDefaultFactory(Property.LOCALITY, createLocalityFactory());
        registerDefaultFactory(Property.LOCATION, createLocationFactory());
        registerDefaultFactory(Property.LOCATION_TYPE, createLocationTypeFactory());
        registerDefaultFactory(Property.METHOD, createMethodFactory());
        registerDefaultFactory(Property.NAME, createNameFactory());
        registerDefaultFactory(Property.ORGANIZER, createOrganizerFactory());
        registerDefaultFactory(Property.PERCENT_COMPLETE, createPercentCompleteFactory());
        registerDefaultFactory(Property.POSTALCODE, createPostalcodeFactory());
        registerDefaultFactory(Property.PRIORITY, createPriorityFactory());
        registerDefaultFactory(Property.PRODID, createProdIdFactory());
        registerDefaultFactory(Property.RDATE, createRDateFactory());
        registerDefaultFactory(Property.RECURRENCE_ID, createRecurrenceIdFactory());
        registerDefaultFactory(Property.REGION, createRegionFactory());
        registerDefaultFactory(Property.RELATED_TO, createRelatedToFactory());
        registerDefaultFactory(Property.REPEAT, createRepeatFactory());
        registerDefaultFactory(Property.REQUEST_STATUS, createRequestStatusFactory());
        registerDefaultFactory(Property.RESOURCES, createResourcesFactory());
        registerDefaultFactory(Property.RRULE, createRRuleFactory());
        registerDefaultFactory(Property.SEQUENCE, createSequenceFactory());
        registerDefaultFactory(Property.STATUS, createStatusFactory());
        registerDefaultFactory(Property.STREET_ADDRESS, createStreetAddressFactory());
        registerDefaultFactory(Property.SUMMARY, createSummaryFactory());
        registerDefaultFactory(Property.TEL, createTelFactory());
        registerDefaultFactory(Property.TRANSP, createTranspFactory());
        registerDefaultFactory(Property.TRIGGER, createTriggerFactory());
        registerDefaultFactory(Property.TZID, createTzIdFactory());
        registerDefaultFactory(Property.TZNAME, createTzNameFactory());
        registerDefaultFactory(Property.TZOFFSETFROM, createTzOffsetFromFactory());
        registerDefaultFactory(Property.TZOFFSETTO, createTzOffsetToFactory());
        registerDefaultFactory(Property.TZURL, createTzUrlFactory());
        registerDefaultFactory(Property.UID, createUidFactory());
        registerDefaultFactory(Property.URL, createUrlFactory());
        registerDefaultFactory(Property.VERSION, createVersionFactory());
    }

    private PropertyFactory createActionFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Action(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Action();
            }
        };
    }

    private PropertyFactory createAttachFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Attach(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Attach();
            }
        };
    }

    private PropertyFactory createAttendeeFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Attendee(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Attendee();
            }
        };
    }

    private PropertyFactory createCalScaleFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new CalScale(parameters, value);
            }

            public Property createProperty(final String name) {
                return new CalScale();
            }
        };
    }

    private PropertyFactory createCategoriesFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Categories(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Categories();
            }
        };
    }

    private PropertyFactory createClazzFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Clazz(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Clazz();
            }
        };
    }

    private PropertyFactory createCommentFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Comment(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Comment();
            }
        };
    }

    private PropertyFactory createCompletedFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Completed(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Completed();
            }
        };
    }

    private PropertyFactory createContactFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Contact(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Contact();
            }
        };
    }

    private PropertyFactory createCountryFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Country(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Country();
            }
        };
    }

    private PropertyFactory createCreatedFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Created(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Created();
            }
        };
    }

    private PropertyFactory createDescriptionFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Description(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Description();
            }
        };
    }

    private PropertyFactory createDtEndFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new DtEnd(parameters, value);
            }

            public Property createProperty(final String name) {
                return new DtEnd();
            }
        };
    }

    private PropertyFactory createDtStampFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new DtStamp(parameters, value);
            }

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

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new DtStart(parameters, value);
            }

            public Property createProperty(final String name) {
                return new DtStart();
            }
        };
    }

    private PropertyFactory createDueFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Due(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Due();
            }
        };
    }

    private PropertyFactory createDurationFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Duration(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Duration();
            }
        };
    }

    private PropertyFactory createExDateFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new ExDate(parameters, value);
            }

            public Property createProperty(final String name) {
                return new ExDate();
            }
        };
    }

    private PropertyFactory createExRuleFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new ExRule(parameters, value);
            }

            public Property createProperty(final String name) {
                return new ExRule();
            }
        };
    }

    private PropertyFactory createExtendedAddressFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new ExtendedAddress(parameters, value);
            }

            public Property createProperty(final String name) {
                return new ExtendedAddress();
            }
        };
    }

    private PropertyFactory createFreeBusyFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new FreeBusy(parameters, value);
            }

            public Property createProperty(final String name) {
                return new FreeBusy();
            }
        };
    }

    private PropertyFactory createGeoFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Geo(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Geo();
            }
        };
    }

    private PropertyFactory createLastModifiedFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new LastModified(parameters, value);
            }

            public Property createProperty(final String name) {
                return new LastModified();
            }
        };
    }

    private PropertyFactory createLocalityFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Locality(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Locality();
            }
        };
    }

    private PropertyFactory createLocationFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Location(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Location();
            }
        };
    }

    private PropertyFactory createLocationTypeFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new LocationType(parameters, value);
            }

            public Property createProperty(final String name) {
                return new LocationType();
            }
        };
    }

    private PropertyFactory createMethodFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Method(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Method();
            }
        };
    }

    private PropertyFactory createNameFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Name(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Name();
            }
        };
    }

    private PropertyFactory createOrganizerFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Organizer(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Organizer();
            }
        };
    }

    private PropertyFactory createPercentCompleteFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new PercentComplete(parameters, value);
            }

            public Property createProperty(final String name) {
                return new PercentComplete();
            }
        };
    }

    private PropertyFactory createPostalcodeFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Postalcode(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Postalcode();
            }
        };
    }

    private PropertyFactory createPriorityFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Priority(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Priority();
            }
        };
    }

    private PropertyFactory createProdIdFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new ProdId(parameters, value);
            }

            public Property createProperty(final String name) {
                return new ProdId();
            }
        };
    }

    private PropertyFactory createRDateFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new RDate(parameters, value);
            }

            public Property createProperty(final String name) {
                return new RDate();
            }
        };
    }

    private PropertyFactory createRecurrenceIdFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new RecurrenceId(parameters, value);
            }

            public Property createProperty(final String name) {
                return new RecurrenceId();
            }
        };
    }

    private PropertyFactory createRegionFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Region(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Region();
            }
        };
    }

    private PropertyFactory createRelatedToFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new RelatedTo(parameters, value);
            }

            public Property createProperty(final String name) {
                return new RelatedTo();
            }
        };
    }

    private PropertyFactory createRepeatFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Repeat(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Repeat();
            }
        };
    }

    private PropertyFactory createRequestStatusFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new RequestStatus(parameters, value);
            }

            public Property createProperty(final String name) {
                return new RequestStatus();
            }
        };
    }

    private PropertyFactory createResourcesFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Resources(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Resources();
            }
        };
    }

    private PropertyFactory createRRuleFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new RRule(parameters, value);
            }

            public Property createProperty(final String name) {
                return new RRule();
            }
        };
    }

    private PropertyFactory createSequenceFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Sequence(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Sequence();
            }
        };
    }

    private PropertyFactory createStatusFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Status(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Status();
            }
        };
    }

    private PropertyFactory createStreetAddressFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new StreetAddress(parameters, value);
            }

            public Property createProperty(final String name) {
                return new StreetAddress();
            }
        };
    }

    private PropertyFactory createSummaryFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Summary(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Summary();
            }
        };
    }

    private PropertyFactory createTelFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Tel(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Tel();
            }
        };
    }

    private PropertyFactory createTranspFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Transp(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Transp();
            }
        };
    }

    private PropertyFactory createTriggerFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Trigger(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Trigger();
            }
        };
    }

    private PropertyFactory createTzIdFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new TzId(parameters, value);
            }

            public Property createProperty(final String name) {
                return new TzId();
            }
        };
    }

    private PropertyFactory createTzNameFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new TzName(parameters, value);
            }

            public Property createProperty(final String name) {
                return new TzName();
            }
        };
    }

    private PropertyFactory createTzOffsetFromFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new TzOffsetFrom(parameters, value);
            }

            public Property createProperty(final String name) {
                return new TzOffsetFrom();
            }
        };
    }

    private PropertyFactory createTzOffsetToFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new TzOffsetTo(parameters, value);
            }

            public Property createProperty(final String name) {
                return new TzOffsetTo();
            }
        };
    }

    private PropertyFactory createTzUrlFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new TzUrl(parameters, value);
            }

            public Property createProperty(final String name) {
                return new TzUrl();
            }
        };
    }

    private PropertyFactory createUidFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Uid(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Uid();
            }
        };
    }

    private PropertyFactory createUrlFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Url(parameters, value);
            }

            public Property createProperty(final String name) {
                return new Url();
            }
        };
    }

    private PropertyFactory createVersionFactory() {
        return new PropertyFactory() {

            public Property createProperty(final String name,
                    final ParameterList parameters, final String value)
                    throws IOException, URISyntaxException, ParseException {
                return new Version(parameters, value);
            }

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
     * {@inheritDoc}
     */
    public Property createProperty(final String name) {
        final PropertyFactory factory = (PropertyFactory) getFactory(name);
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
     * {@inheritDoc}
     */
    public Property createProperty(final String name,
            final ParameterList parameters, final String value)
            throws IOException, URISyntaxException, ParseException {

        final PropertyFactory factory = (PropertyFactory) getFactory(name);
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
