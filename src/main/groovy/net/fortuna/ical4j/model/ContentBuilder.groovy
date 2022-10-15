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

import net.fortuna.ical4j.model.LocationType
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
        registerFactory('available', new ComponentFactoryWrapper(Available, new Available.Factory()))
        registerFactory('daylight', new ComponentFactoryWrapper(Daylight, new Daylight.Factory()))
        registerFactory('standard', new ComponentFactoryWrapper(Standard, new Standard.Factory()))
        registerFactory('valarm', new ComponentFactoryWrapper(VAlarm, new VAlarm.Factory()))
        registerFactory('vavailability', new ComponentFactoryWrapper(VAvailability, new VAvailability.Factory()))
        registerFactory('vevent', new ComponentFactoryWrapper(VEvent, new VEvent.Factory()))
        registerFactory('vfreebusy', new ComponentFactoryWrapper(VFreeBusy, new VFreeBusy.Factory()))
        registerFactory('vjournal', new ComponentFactoryWrapper(VJournal, new VJournal.Factory()))
        registerFactory('vtimezone', new ComponentFactoryWrapper(VTimeZone, new VTimeZone.Factory()))
        registerFactory('vtodo', new ComponentFactoryWrapper(VToDo, new VToDo.Factory()))
        registerFactory('vvenue', new ComponentFactoryWrapper(VVenue, new VVenue.Factory()))
        registerFactory('vlocation', new ComponentFactoryWrapper(VLocation, new VLocation.Factory()))
        registerFactory('vresource', new ComponentFactoryWrapper(VResource, new VResource.Factory()))
        registerFactory('participant', new ComponentFactoryWrapper(Participant, new Participant.Factory()))
        registerFactory('xcomponent', new XComponentFactory())
    }
    
    def registerProperties() {
        // properties..
        registerFactory('action', new PropertyFactoryWrapper(Action, new Action.Factory()))
        registerFactory('attach', new PropertyFactoryWrapper(Attach, new Attach.Factory()))
        registerFactory('attendee', new PropertyFactoryWrapper(Attendee, new Attendee.Factory()))
        registerFactory('busytype', new PropertyFactoryWrapper(BusyType, new BusyType.Factory()))
        registerFactory('calscale', new PropertyFactoryWrapper(CalScale, new CalScale.Factory()))
        registerFactory('categories', new PropertyFactoryWrapper(Categories, new Categories.Factory()))
        registerFactory('class', new PropertyFactoryWrapper(Clazz, new Clazz.Factory()))
        registerFactory('comment', new DefaultPropertyFactory(klass: Comment))
        registerFactory('completed', new DefaultPropertyFactory(klass: Completed))
        registerFactory('contact', new DefaultPropertyFactory(klass: Contact))
        registerFactory('country', new DefaultPropertyFactory(klass: Country))
        registerFactory('created', new DefaultPropertyFactory(klass: Created))
        registerFactory('description', new DefaultPropertyFactory(klass: Description))
        registerFactory('dtend', new PropertyFactoryWrapper(DtEnd, new DtEnd.Factory()))
        registerFactory('dtstamp', new PropertyFactoryWrapper(DtStamp, new DtStamp.Factory()))
        registerFactory('dtstart', new PropertyFactoryWrapper(DtStart, new DtStart.Factory()))
        registerFactory('due', new PropertyFactoryWrapper(Due, new Due.Factory()))
        registerFactory('duration', new DefaultPropertyFactory(klass: Duration))
        registerFactory('exdate', new DefaultPropertyFactory(klass: ExDate))
        registerFactory('exrule', new DefaultPropertyFactory(klass: ExRule))
        registerFactory('freebusy', new DefaultPropertyFactory(klass: FreeBusy))
        registerFactory('geo', new DefaultPropertyFactory(klass: Geo))
        registerFactory('lastmodified', new DefaultPropertyFactory(klass: LastModified))
        registerFactory('location', new DefaultPropertyFactory(klass: Location))
        registerFactory('locationtype', new DefaultPropertyFactory(klass: LocationType))
        registerFactory('method', new PropertyFactoryWrapper(Method, new Method.Factory()))
        registerFactory('name', new DefaultPropertyFactory(klass: Name))
        registerFactory('organizer', new DefaultPropertyFactory(klass: Organizer))
        registerFactory('percentcomplete', new DefaultPropertyFactory(klass: PercentComplete))
        registerFactory('postalcode', new DefaultPropertyFactory(klass: Postalcode))
        registerFactory('priority', new PropertyFactoryWrapper(Priority, new Priority.Factory()))
        registerFactory('prodid', new DefaultPropertyFactory(klass: ProdId))
        registerFactory('rdate', new DefaultPropertyFactory(klass: RDate))
        registerFactory('recurrenceid', new DefaultPropertyFactory(klass: RecurrenceId))
        registerFactory('region', new DefaultPropertyFactory(klass: Region))
        registerFactory('relatedto', new DefaultPropertyFactory(klass: RelatedTo))
        registerFactory('repeat', new DefaultPropertyFactory(klass: Repeat))
        registerFactory('requeststatus', new PropertyFactoryWrapper(RequestStatus, new RequestStatus.Factory()))
        registerFactory('resources', new DefaultPropertyFactory(klass: Resources))
        registerFactory('rrule', new PropertyFactoryWrapper(RRule, new RRule.Factory()))
        registerFactory('sequence', new DefaultPropertyFactory(klass: Sequence))
        registerFactory('status', new PropertyFactoryWrapper(Status, new Status.Factory()))
        registerFactory('streetaddress', new DefaultPropertyFactory(klass: StreetAddress))
        registerFactory('summary', new DefaultPropertyFactory(klass: Summary))
        registerFactory('tel', new DefaultPropertyFactory(klass: Tel))
        registerFactory('transp', new PropertyFactoryWrapper(Transp, new Transp.Factory()))
        registerFactory('trigger', new DefaultPropertyFactory(klass: Trigger))
        registerFactory('tzid', new DefaultPropertyFactory(klass: net.fortuna.ical4j.model.property.TzId))
        registerFactory('tzname', new DefaultPropertyFactory(klass: TzName))
        registerFactory('tzoffsetfrom', new DefaultPropertyFactory(klass: TzOffsetFrom))
        registerFactory('tzoffsetto', new DefaultPropertyFactory(klass: TzOffsetTo))
        registerFactory('tzurl', new DefaultPropertyFactory(klass: TzUrl))
        registerFactory('uid', new DefaultPropertyFactory(klass: Uid))
        registerFactory('url', new DefaultPropertyFactory(klass: Url))
        registerFactory('version', new PropertyFactoryWrapper(Version, new Version.Factory()))
        registerFactory('xproperty', new XPropertyFactory())

        // RFC7986
        registerFactory('color', new PropertyFactoryWrapper(Color, new Color.Factory()))
        registerFactory('conference', new PropertyFactoryWrapper(Conference, new Conference.Factory()))
        registerFactory('image', new PropertyFactoryWrapper(Image, new Image.Factory()))
        registerFactory('refreshinterval', new PropertyFactoryWrapper(RefreshInterval, new RefreshInterval.Factory()))
        registerFactory('source', new PropertyFactoryWrapper(Source, new Source.Factory()))

        registerFactory('concept', new PropertyFactoryWrapper(Concept, new Concept.Factory()))
        registerFactory('link', new PropertyFactoryWrapper(Link, new Link.Factory()))
        registerFactory('refid', new PropertyFactoryWrapper(RefId, new RefId.Factory()))
    }
    
    def registerParameters() {
        // parameters..
        registerFactory('abbrev', new ParameterFactoryWrapper(Abbrev, new Abbrev.Factory()))
        registerFactory('altrep', new ParameterFactoryWrapper(AltRep, new AltRep.Factory()))
        registerFactory('cn', new ParameterFactoryWrapper(Cn, new Cn.Factory()))
        registerFactory('cutype', new ParameterFactoryWrapper(CuType, new CuType.Factory()))
        registerFactory('delegatedfrom', new ParameterFactoryWrapper(DelegatedFrom, new DelegatedFrom.Factory()))
        registerFactory('delegatedto', new ParameterFactoryWrapper(DelegatedTo, new DelegatedTo.Factory()))
        registerFactory('dir', new ParameterFactoryWrapper(Dir, new Dir.Factory()))
        registerFactory('encoding', new ParameterFactoryWrapper(Encoding, new Encoding.Factory()))
        registerFactory('fbtype', new ParameterFactoryWrapper(FbType, new FbType.Factory()))
        registerFactory('fmttype', new ParameterFactoryWrapper(FmtType, new FmtType.Factory()))
        registerFactory('language', new ParameterFactoryWrapper(Language, new Language.Factory()))
        registerFactory('member', new ParameterFactoryWrapper(Member, new Member.Factory()))
        registerFactory('partstat', new ParameterFactoryWrapper(PartStat, new PartStat.Factory()))
        registerFactory('range', new ParameterFactoryWrapper(Range, new Range.Factory()))
        registerFactory('related', new ParameterFactoryWrapper(Related, new Related.Factory()))
        registerFactory('reltype', new ParameterFactoryWrapper(RelType, new RelType.Factory()))
        registerFactory('role', new ParameterFactoryWrapper(Role, new Role.Factory()))
        registerFactory('rsvp', new ParameterFactoryWrapper(Rsvp, new Rsvp.Factory()))
        registerFactory('sentby', new ParameterFactoryWrapper(SentBy, new SentBy.Factory()))
        registerFactory('type', new ParameterFactoryWrapper(Type, new Type.Factory()))
        registerFactory('tzid_', new ParameterFactoryWrapper(TzId, new TzId.Factory()))
        registerFactory('value', new ParameterFactoryWrapper(Value, new Value.Factory()))
        registerFactory('xparameter', new XParameterFactory())

        // RFC7986
        registerFactory('display', new ParameterFactoryWrapper(Display, new Display.Factory()))
        registerFactory('email', new ParameterFactoryWrapper(Email, new Email.Factory()))
        registerFactory('feature', new ParameterFactoryWrapper(Feature, new Feature.Factory()))
        registerFactory('label', new ParameterFactoryWrapper(Label, new Label.Factory()))

        registerFactory('gap', new ParameterFactoryWrapper(Gap, new Gap.Factory()))
        registerFactory('linkrel', new ParameterFactoryWrapper(LinkRel, new LinkRel.Factory()))
    }
}

