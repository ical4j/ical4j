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
        registerDefaultFactory(Property.ACTION, new ActionFactory());
        registerDefaultFactory(Property.ATTACH, new AttachFactory());
        registerDefaultFactory(Property.ATTENDEE, new AttendeeFactory());
        registerDefaultFactory(Property.CALSCALE, new CalScaleFactory());
        registerDefaultFactory(Property.CATEGORIES, new CategoriesFactory());
        registerDefaultFactory(Property.CLASS, new ClazzFactory());
        registerDefaultFactory(Property.COMMENT, new CommentFactory());
        registerDefaultFactory(Property.COMPLETED, new CompletedFactory());
        registerDefaultFactory(Property.CONTACT, new ContactFactory());
        registerDefaultFactory(Property.COUNTRY, new CountryFactory());
        registerDefaultFactory(Property.CREATED, new CreatedFactory());
        registerDefaultFactory(Property.DESCRIPTION, new DescriptionFactory());
        registerDefaultFactory(Property.DTEND, new DtEndFactory());
        registerDefaultFactory(Property.DTSTAMP, new DtStampFactory());
        registerDefaultFactory(Property.DTSTART, new DtStartFactory());
        registerDefaultFactory(Property.DUE, new DueFactory());
        registerDefaultFactory(Property.DURATION, new DurationFactory());
        registerDefaultFactory(Property.EXDATE, new ExDateFactory());
        registerDefaultFactory(Property.EXRULE, new ExRuleFactory());
        registerDefaultFactory(Property.EXTENDED_ADDRESS, new ExtendedAddressFactory());
        registerDefaultFactory(Property.FREEBUSY, new FreeBusyFactory());
        registerDefaultFactory(Property.GEO, new GeoFactory());
        registerDefaultFactory(Property.LAST_MODIFIED, new LastModifiedFactory());
        registerDefaultFactory(Property.LOCALITY, new LocalityFactory());
        registerDefaultFactory(Property.LOCATION, new LocationFactory());
        registerDefaultFactory(Property.LOCATION_TYPE, new LocationTypeFactory());
        registerDefaultFactory(Property.METHOD, new MethodFactory());
        registerDefaultFactory(Property.NAME, new NameFactory());
        registerDefaultFactory(Property.ORGANIZER, new OrganizerFactory());
        registerDefaultFactory(Property.PERCENT_COMPLETE, new PercentCompleteFactory());
        registerDefaultFactory(Property.POSTALCODE, new PostalcodeFactory());
        registerDefaultFactory(Property.PRIORITY, new PriorityFactory());
        registerDefaultFactory(Property.PRODID, new ProdIdFactory());
        registerDefaultFactory(Property.RDATE, new RDateFactory());
        registerDefaultFactory(Property.RECURRENCE_ID, new RecurrenceIdFactory());
        registerDefaultFactory(Property.REGION, new RegionFactory());
        registerDefaultFactory(Property.RELATED_TO, new RelatedToFactory());
        registerDefaultFactory(Property.REPEAT, new RepeatFactory());
        registerDefaultFactory(Property.REQUEST_STATUS, new RequestStatusFactory());
        registerDefaultFactory(Property.RESOURCES, new ResourcesFactory());
        registerDefaultFactory(Property.RRULE, new RRuleFactory());
        registerDefaultFactory(Property.SEQUENCE, new SequenceFactory());
        registerDefaultFactory(Property.STATUS, new StatusFactory());
        registerDefaultFactory(Property.STREET_ADDRESS, new StreetAddressFactory());
        registerDefaultFactory(Property.SUMMARY, new SummaryFactory());
        registerDefaultFactory(Property.TEL, new TelFactory());
        registerDefaultFactory(Property.TRANSP, new TranspFactory());
        registerDefaultFactory(Property.TRIGGER, new TriggerFactory());
        registerDefaultFactory(Property.TZID, new TzIdFactory());
        registerDefaultFactory(Property.TZNAME, new TzNameFactory());
        registerDefaultFactory(Property.TZOFFSETFROM, new TzOffsetFromFactory());
        registerDefaultFactory(Property.TZOFFSETTO, new TzOffsetToFactory());
        registerDefaultFactory(Property.TZURL, new TzUrlFactory());
        registerDefaultFactory(Property.UID, new UidFactory());
        registerDefaultFactory(Property.URL, new UrlFactory());
        registerDefaultFactory(Property.VERSION, new VersionFactory());
    }

    private static class ActionFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Action(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Action();
        }
    }

    private static class AttachFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Attach(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Attach();
        }
    }

    private static class AttendeeFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Attendee(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Attendee();
        }
    }

    private static class CalScaleFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new CalScale(parameters, value);
        }

        public Property createProperty(final String name) {
            return new CalScale();
        }
    }

    private static class CategoriesFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Categories(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Categories();
        }
    }

    private static class ClazzFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Clazz(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Clazz();
        }
    }

    private static class CommentFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Comment(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Comment();
        }
    }

    private static class CompletedFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Completed(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Completed();
        }
    }

    private static class ContactFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Contact(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Contact();
        }
    }

    private static class CountryFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Country(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Country();
        }
    }

    private static class CreatedFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Created(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Created();
        }
    }

    private static class DescriptionFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Description(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Description();
        }
    }

    private static class DtEndFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new DtEnd(parameters, value);
        }

        public Property createProperty(final String name) {
            return new DtEnd();
        }
    }

    private static class DtStampFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new DtStamp(parameters, value);
        }

        public Property createProperty(final String name) {
            return new DtStamp();
        }
    }

    /**
     * @return
     */
    private static class DtStartFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new DtStart(parameters, value);
        }

        public Property createProperty(final String name) {
            return new DtStart();
        }
    }

    private static class DueFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Due(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Due();
        }
    }

    private static class DurationFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Duration(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Duration();
        }
    }

    private static class ExDateFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
               final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new ExDate(parameters, value);
        }

        public Property createProperty(final String name) {
            return new ExDate();
        }
    }

    private static class ExRuleFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new ExRule(parameters, value);
        }

        public Property createProperty(final String name) {
            return new ExRule();
        }
    }

    private static class ExtendedAddressFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new ExtendedAddress(parameters, value);
        }

        public Property createProperty(final String name) {
            return new ExtendedAddress();
        }
    }

    private static class FreeBusyFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new FreeBusy(parameters, value);
        }

        public Property createProperty(final String name) {
            return new FreeBusy();
        }
    }

    private static class GeoFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Geo(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Geo();
        }
    }

    private static class LastModifiedFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new LastModified(parameters, value);
        }

        public Property createProperty(final String name) {
            return new LastModified();
        }
    }

    private static class LocalityFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Locality(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Locality();
        }
    }

    private static class LocationFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Location(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Location();
        }
    }

    private static class LocationTypeFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new LocationType(parameters, value);
        }

        public Property createProperty(final String name) {
            return new LocationType();
        }
    }

    private static class MethodFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Method(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Method();
        }
    }

    private static class NameFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Name(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Name();
        }
    }

    private static class OrganizerFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Organizer(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Organizer();
        }
    }

    private static class PercentCompleteFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new PercentComplete(parameters, value);
        }

        public Property createProperty(final String name) {
            return new PercentComplete();
        }
    }

    private static class PostalcodeFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Postalcode(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Postalcode();
        }
    }

    private static class PriorityFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Priority(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Priority();
        }
    }

    private static class ProdIdFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new ProdId(parameters, value);
        }

        public Property createProperty(final String name) {
            return new ProdId();
        }
    }

    private static class RDateFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new RDate(parameters, value);
        }

        public Property createProperty(final String name) {
            return new RDate();
        }
    }

    private static class RecurrenceIdFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new RecurrenceId(parameters, value);
        }

        public Property createProperty(final String name) {
            return new RecurrenceId();
        }
    }

    private static class RegionFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Region(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Region();
        }
    }

    private static class RelatedToFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new RelatedTo(parameters, value);
        }

        public Property createProperty(final String name) {
            return new RelatedTo();
        }
    }

    private static class RepeatFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Repeat(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Repeat();
        }
    }

    private static class RequestStatusFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new RequestStatus(parameters, value);
        }

        public Property createProperty(final String name) {
            return new RequestStatus();
        }
    }

    private static class ResourcesFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Resources(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Resources();
        }
    }

    private static class RRuleFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new RRule(parameters, value);
        }

        public Property createProperty(final String name) {
            return new RRule();
        }
    }

    private static class SequenceFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Sequence(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Sequence();
        }
    }

    private static class StatusFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Status(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Status();
        }
    }

    private static class StreetAddressFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new StreetAddress(parameters, value);
        }

        public Property createProperty(final String name) {
            return new StreetAddress();
        }
    }

    private static class SummaryFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Summary(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Summary();
        }
    }

    private static class TelFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Tel(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Tel();
        }
    }

    private static class TranspFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Transp(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Transp();
        }
    }

    private static class TriggerFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Trigger(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Trigger();
        }
    }

    private static class TzIdFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new TzId(parameters, value);
        }

        public Property createProperty(final String name) {
            return new TzId();
        }
    }

    private static class TzNameFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new TzName(parameters, value);
        }

        public Property createProperty(final String name) {
            return new TzName();
        }
    }

    private static class TzOffsetFromFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new TzOffsetFrom(parameters, value);
        }

        public Property createProperty(final String name) {
            return new TzOffsetFrom();
        }
    }

    private static class TzOffsetToFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new TzOffsetTo(parameters, value);
        }

        public Property createProperty(final String name) {
            return new TzOffsetTo();
        }
    }

    private static class TzUrlFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new TzUrl(parameters, value);
        }

        public Property createProperty(final String name) {
            return new TzUrl();
        }
    }

    private static class UidFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Uid(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Uid();
        }
    }

    private static class UrlFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Url(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Url();
        }
    }

    private static class VersionFactory implements PropertyFactory {
        private static final long serialVersionUID = 1L;

        public Property createProperty(final String name,
                final ParameterList parameters, final String value)
                throws IOException, URISyntaxException, ParseException {
            return new Version(parameters, value);
        }

        public Property createProperty(final String name) {
            return new Version();
        }
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
