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

import net.fortuna.ical4j.model.component.*
import net.fortuna.ical4j.model.parameter.*
import net.fortuna.ical4j.model.property.*

/**
 * $Id$
 *
 * Created on: 03/08/2009
 *
 * @author fortuna
 *
 */
class ContentBuilder extends FactoryBuilderSupport {

    ContentBuilder(boolean init = true) {
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
        registerFactory('attach', new DefaultPropertyFactory(klass: Attach))
        registerFactory('attendee', new DefaultPropertyFactory(klass: Attendee))
        registerFactory('busytype', new BusyTypeFactory())
        registerFactory('calscale', new CalScaleFactory())
        registerFactory('categories', new DefaultPropertyFactory(klass: Categories))
        registerFactory('class', new ClazzFactory())
        registerFactory('comment', new DefaultPropertyFactory(klass: Comment))
        registerFactory('completed', new DefaultPropertyFactory(klass: Completed))
        registerFactory('contact', new DefaultPropertyFactory(klass: Contact))
        registerFactory('country', new DefaultPropertyFactory(klass: Country))
        registerFactory('created', new DefaultPropertyFactory(klass: Created))
        registerFactory('description', new DefaultPropertyFactory(klass: Description))
        registerFactory('dtend', new DefaultPropertyFactory(klass: DtEnd))
        registerFactory('dtstamp', new DtStampFactory())
        registerFactory('dtstart', new DefaultPropertyFactory(klass: DtStart))
        registerFactory('due', new DefaultPropertyFactory(klass: Due))
        registerFactory('duration', new DefaultPropertyFactory(klass: Duration))
        registerFactory('exdate', new DefaultPropertyFactory(klass: ExDate))
        registerFactory('exrule', new DefaultPropertyFactory(klass: ExRule))
        registerFactory('freebusy', new DefaultPropertyFactory(klass: FreeBusy))
        registerFactory('geo', new DefaultPropertyFactory(klass: Geo))
        registerFactory('lastmodified', new DefaultPropertyFactory(klass: LastModified))
        registerFactory('location', new DefaultPropertyFactory(klass: Location))
        registerFactory('locationtype', new DefaultPropertyFactory(klass: LocationType))
        registerFactory('method', new MethodFactory())
        registerFactory('name', new DefaultPropertyFactory(klass: Name))
        registerFactory('organizer', new DefaultPropertyFactory(klass: Organizer))
        registerFactory('percentcomplete', new DefaultPropertyFactory(klass: PercentComplete))
        registerFactory('postalcode', new DefaultPropertyFactory(klass: Postalcode))
        registerFactory('priority', new PriorityFactory())
        registerFactory('prodid', new DefaultPropertyFactory(klass: ProdId))
        registerFactory('rdate', new DefaultPropertyFactory(klass: RDate))
        registerFactory('recurrenceid', new DefaultPropertyFactory(klass: RecurrenceId))
        registerFactory('region', new DefaultPropertyFactory(klass: Region))
        registerFactory('relatedto', new DefaultPropertyFactory(klass: RelatedTo))
        registerFactory('repeat', new DefaultPropertyFactory(klass: Repeat))
        registerFactory('requeststatus', new RequestStatusFactory())
        registerFactory('resources', new DefaultPropertyFactory(klass: Resources))
        registerFactory('rrule', new DefaultPropertyFactory(klass: RRule))
        registerFactory('sequence', new DefaultPropertyFactory(klass: Sequence))
        registerFactory('status', new StatusFactory())
        registerFactory('streetaddress', new DefaultPropertyFactory(klass: StreetAddress))
        registerFactory('summary', new DefaultPropertyFactory(klass: Summary))
        registerFactory('tel', new DefaultPropertyFactory(klass: Tel))
        registerFactory('transp', new TranspFactory())
        registerFactory('trigger', new DefaultPropertyFactory(klass: Trigger))
        registerFactory('tzid', new DefaultPropertyFactory(klass: net.fortuna.ical4j.model.property.TzId))
        registerFactory('tzname', new DefaultPropertyFactory(klass: TzName))
        registerFactory('tzoffsetfrom', new DefaultPropertyFactory(klass: TzOffsetFrom))
        registerFactory('tzoffsetto', new DefaultPropertyFactory(klass: TzOffsetTo))
        registerFactory('tzurl', new DefaultPropertyFactory(klass: TzUrl))
        registerFactory('uid', new DefaultPropertyFactory(klass: Uid))
        registerFactory('url', new DefaultPropertyFactory(klass: Url))
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
        registerFactory('tzid_', new TzIdFactory())
        registerFactory('value', new ValueFactory())
        registerFactory('xparameter', new XParameterFactory())
    }
}

