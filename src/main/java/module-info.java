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

//    provides java.time.zone.ZoneRulesProvider with net.fortuna.ical4j.model.DefaultZoneRulesProvider;

    uses net.fortuna.ical4j.model.ComponentFactory;
    uses net.fortuna.ical4j.model.ParameterFactory;
    uses net.fortuna.ical4j.model.PropertyFactory;
    uses net.fortuna.ical4j.transform.component.Rfc5545ComponentRule;
    uses net.fortuna.ical4j.transform.property.Rfc5545PropertyRule;
    uses net.fortuna.ical4j.validate.CalendarValidatorFactory;
}