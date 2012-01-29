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
package net.fortuna.ical4j.model

import groovy.util.FactoryBuilderSupportimport net.fortuna.ical4j.model.component.*
import net.fortuna.ical4j.model.property.*import net.fortuna.ical4j.model.parameter.*import net.fortuna.ical4j.model.property.BusyTypeFactory/**
 * $Id$
 *
 * Created on: 03/08/2009
 *
 * @author fortuna
 *
 */
public class ContentBuilder extends FactoryBuilderSupport {

    public ContentBuilder(boolean init = true) {
        super(init)
    }
    
    def registerCalendarAndCollections() {
        registerFactory('calendar', new CalendarFactory())
        registerFactory('parameters', new ParameterListFactory())
    }
    
    def registerComponents() {
        // components..
        registerFactory('available', new AvailableFactory())
        registerFactory('daylight', new DaylightFactory())
        registerFactory('standard', new StandardFactory())
        registerFactory('valarm', new VAlarmFactory())
        registerFactory('vavailability', new VAvailabilityFactory())
        registerFactory('vevent', new VEventFactory())
        registerFactory('vfreebusy', new VFreeBusyFactory())
        registerFactory('vjournal', new VJournalFactory())
        registerFactory('vtimezone', new VTimeZoneFactory())
        registerFactory('vtodo', new VToDoFactory())
        registerFactory('vvenue', new VVenueFactory())
        registerFactory('xcomponent', new XComponentFactory())
    }
    
    def registerProperties() {
        // properties..
        registerFactory('action', new ActionFactory())
        registerFactory('attach', new AttachFactory())
        registerFactory('attendee', new AttendeeFactory())
        registerFactory('busytype', new BusyTypeFactory())
        registerFactory('calscale', new CalScaleFactory())
        registerFactory('categories', new CategoriesFactory())
        registerFactory('class', new ClazzFactory())
        registerFactory('comment', new CommentFactory())
        registerFactory('completed', new CompletedFactory())
        registerFactory('contact', new ContactFactory())
        registerFactory('country', new CountryFactory())
        registerFactory('created', new CreatedFactory())
        registerFactory('description', new DescriptionFactory())
        registerFactory('dtend', new DtEndFactory())
        registerFactory('dtstamp', new DtStampFactory())
        registerFactory('dtstart', new DtStartFactory())
        registerFactory('due', new DueFactory())
        registerFactory('duration', new DurationFactory())
        registerFactory('exdate', new ExDateFactory())
        registerFactory('exrule', new ExRuleFactory())
        registerFactory('freebusy', new FreeBusyFactory())
        registerFactory('geo', new GeoFactory())
        registerFactory('lastmodified', new LastModifiedFactory())
        registerFactory('location', new LocationFactory())
        registerFactory('locationtype', new LocationTypeFactory())
        registerFactory('method', new MethodFactory())
        registerFactory('name', new NameFactory())
        registerFactory('organizer', new OrganizerFactory())
        registerFactory('percentcomplete', new PercentCompleteFactory())
        registerFactory('postalcode', new PostalcodeFactory())
        registerFactory('priority', new PriorityFactory())
        registerFactory('prodid', new ProdIdFactory())
        registerFactory('rdate', new RDateFactory())
        registerFactory('recurrenceid', new RecurrenceIdFactory())
        registerFactory('region', new RegionFactory())
        registerFactory('relatedto', new RelatedToFactory())
        registerFactory('repeat', new RepeatFactory())
        registerFactory('requeststatus', new RequestStatusFactory())
        registerFactory('resources', new ResourcesFactory())
        registerFactory('rrule', new RRuleFactory())
        registerFactory('sequence', new SequenceFactory())
        registerFactory('status', new StatusFactory())
        registerFactory('streetaddress', new StreetAddressFactory())
        registerFactory('summary', new SummaryFactory())
        registerFactory('tel', new TelFactory())
        registerFactory('transp', new TranspFactory())
        registerFactory('trigger', new TriggerFactory())
        registerFactory('tzid', new TzIdFactory())
        registerFactory('tzname', new TzNameFactory())
        registerFactory('tzoffsetfrom', new TzOffsetFromFactory())
        registerFactory('tzoffsetto', new TzOffsetToFactory())
        registerFactory('tzurl', new TzUrlFactory())
        registerFactory('uid', new UidFactory())
        registerFactory('url', new UrlFactory())
        registerFactory('version', new VersionFactory())
        registerFactory('xproperty', new XPropertyFactory())
    }
    
    def registerParameters() {
        // parameters..
        registerFactory('abbrev', new AbbrevFactory())
        registerFactory('altrep', new AltRepFactory())
        registerFactory('cn', new CnFactory())
        registerFactory('cutype', new CuTypeFactory())
        registerFactory('delegatedfrom', new DelegatedFromFactory())
        registerFactory('delegatedto', new DelegatedToFactory())
        registerFactory('dir', new DirFactory())
        registerFactory('encoding', new EncodingFactory())
        registerFactory('fbtype', new FbTypeFactory())
        registerFactory('fmttype', new FmtTypeFactory())
        registerFactory('language', new LanguageFactory())
        registerFactory('member', new MemberFactory())
        registerFactory('partstat', new PartStatFactory())
        registerFactory('range', new RangeFactory())
        registerFactory('related', new RelatedFactory())
        registerFactory('reltype', new RelTypeFactory())
        registerFactory('role', new RoleFactory())
        registerFactory('rsvp', new RsvpFactory())
        registerFactory('sentby', new SentByFactory())
        registerFactory('type', new TypeFactory())
        registerFactory('tzid', new TzIdFactory())
        registerFactory('value', new ValueFactory())
        registerFactory('xparameter', new XParameterFactory())
    }
}
