import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.DefaultZoneRulesProvider;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.component.*;
import net.fortuna.ical4j.model.parameter.*;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.transform.component.Rfc5545ComponentRule;
import net.fortuna.ical4j.transform.property.Rfc5545PropertyRule;
import net.fortuna.ical4j.validate.CalendarValidatorFactory;

import java.time.zone.ZoneRulesProvider;

module ical4j.core {
    requires java.base;
    requires java.xml;

    requires org.apache.commons.codec;
    requires org.apache.commons.lang3;
    requires org.slf4j;
    requires org.threeten.extra;
//    requires org.apache.commons.validator;

    // optional dependencies..
    requires static org.jetbrains.annotations;
    requires static cache.api;
    requires static org.jparsec;
    requires static org.codehaus.groovy;
//    requires static json.sKema;

    exports net.fortuna.ical4j.filter;
    exports net.fortuna.ical4j.filter.expression;
    exports net.fortuna.ical4j.filter.predicate;
    exports net.fortuna.ical4j.model;
    exports net.fortuna.ical4j.model.property;
    exports net.fortuna.ical4j.model.property.immutable;
    exports net.fortuna.ical4j.model.parameter;
    exports net.fortuna.ical4j.model.component;
    exports net.fortuna.ical4j.util;
    exports net.fortuna.ical4j.validate;
    exports net.fortuna.ical4j.validate.property;
    exports net.fortuna.ical4j.validate.component;
    exports net.fortuna.ical4j.agent;
    exports net.fortuna.ical4j.data;
    exports net.fortuna.ical4j.transform;
    exports net.fortuna.ical4j.transform.property;
    exports net.fortuna.ical4j.transform.component;
    exports net.fortuna.ical4j.transform.calendar;
    exports net.fortuna.ical4j.transform.recurrence;

    provides ZoneRulesProvider with DefaultZoneRulesProvider;

    provides ComponentFactory with Daylight.Factory, Standard.Factory, VAlarm.Factory, VEvent.Factory,
            VFreeBusy.Factory, VJournal.Factory, VTimeZone.Factory, VToDo.Factory, VVenue.Factory,
            Available.Factory, VAvailability.Factory, Participant.Factory, VLocation.Factory,
            VResource.Factory;

    provides ParameterFactory with Abbrev.Factory, AltRep.Factory, Cn.Factory, CuType.Factory,
            DelegatedFrom.Factory, Dir.Factory, Encoding.Factory, FbType.Factory, FmtType.Factory,
            Language.Factory, Member.Factory, PartStat.Factory, Range.Factory, Related.Factory,
            RelType.Factory, Role.Factory, Rsvp.Factory, ScheduleAgent.Factory, ScheduleStatus.Factory,
            SentBy.Factory, Type.Factory, net.fortuna.ical4j.model.parameter.TzId.Factory,
            Value.Factory, Vvenue.Factory, Display.Factory, Email.Factory, Feature.Factory,
            Label.Factory, Derived.Factory, Order.Factory, Schema.Factory;

    provides PropertyFactory with Action.Factory, Acknowledged.Factory, Attach.Factory, Attendee.Factory,
            BusyType.Factory, CalScale.Factory, Categories.Factory, Clazz.Factory, Comment.Factory,
            Completed.Factory, Contact.Factory, Country.Factory, Created.Factory, Description.Factory,
            DtEnd.Factory, DtStamp.Factory, DtStart.Factory, Due.Factory, Duration.Factory,
            ExDate.Factory, ExRule.Factory, ExtendedAddress.Factory, FreeBusy.Factory, Geo.Factory,
            LastModified.Factory, Locality.Factory, Location.Factory, Method.Factory, Organizer.Factory,
            PercentComplete.Factory, Postalcode.Factory, Priority.Factory, ProdId.Factory, RDate.Factory,
            RecurrenceId.Factory, Region.Factory, RelatedTo.Factory, Repeat.Factory, RequestStatus.Factory,
            Resources.Factory, RRule.Factory, Sequence.Factory, Status.Factory, StreetAddress.Factory,
            Summary.Factory, Tel.Factory, Transp.Factory, Trigger.Factory,
            net.fortuna.ical4j.model.property.TzId.Factory, TzName.Factory, TzOffsetFrom.Factory,
            TzOffsetTo.Factory, TzUrl.Factory, Uid.Factory, Url.Factory, Version.Factory, Color.Factory,
            Conference.Factory, Image.Factory, Name.Factory, RefreshInterval.Factory, Source.Factory,
            CalendarAddress.Factory, LocationType.Factory, ParticipantType.Factory, ResourceType.Factory,
            StructuredData.Factory, StyledDescription.Factory;

    provides Rfc5545ComponentRule with net.fortuna.ical4j.transform.component.VAlarmRule,
            net.fortuna.ical4j.transform.component.VEventRule;

    provides Rfc5545PropertyRule with net.fortuna.ical4j.transform.property.DatePropertyRule,
            net.fortuna.ical4j.transform.property.DateListPropertyRule,
            net.fortuna.ical4j.transform.property.TzIdRule,
            net.fortuna.ical4j.transform.property.AttendeePropertyRule;

    provides CalendarValidatorFactory with net.fortuna.ical4j.validate.DefaultCalendarValidatorFactory;

    uses net.fortuna.ical4j.model.ComponentFactory;
    uses net.fortuna.ical4j.model.ParameterFactory;
    uses net.fortuna.ical4j.model.PropertyFactory;
    uses net.fortuna.ical4j.transform.component.Rfc5545ComponentRule;
    uses net.fortuna.ical4j.transform.property.Rfc5545PropertyRule;
    uses net.fortuna.ical4j.validate.CalendarValidatorFactory;
}