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

import net.fortuna.ical4j.model.property.*;

/**
 * A factory for creating iCalendar properties.
 *
 * @author benfortuna
 */
public final class PropertyFactory {

    private static PropertyFactory instance = new PropertyFactory();

    /**
     * Constructor made private to prevent instantiation.
     */
    private PropertyFactory() {
    }

    /**
     * @return Returns the instance.
     */
    public static PropertyFactory getInstance() {
        return instance;
    }

    /**
     * Creates an uninitialised property.
     * @param name
     *            name of the property
     * @return a property
     */
    public Property createProperty(final String name) {
        // calendar properties..
        if (Property.CALSCALE.equals(name)) {
            return new CalScale();
        }
        else if (Property.METHOD.equals(name)) {
            return new Method();
        }
        else if (Property.PRODID.equals(name)) {
            return new ProdId();
        }
        else if (Property.VERSION.equals(name)) {
            return new Version();
        }
        // component properties..
        else if (Property.ATTACH.equals(name)) {
            return new Attach();
        }
        else if (Property.CATEGORIES.equals(name)) {
            return new Categories();
        }
        else if (Property.CLASS.equals(name)) {
            return new Clazz();
        }
        else if (Property.COMMENT.equals(name)) {
            return new Comment();
        }
        else if (Property.DESCRIPTION.equals(name)) {
            return new Description();
        }
        else if (Property.GEO.equals(name)) {
            return new Geo();
        }
        else if (Property.LOCATION.equals(name)) {
            return new Location();
        }
        else if (Property.PERCENT_COMPLETE.equals(name)) {
            return new PercentComplete();
        }
        else if (Property.PRIORITY.equals(name)) {
            return new Priority();
        }
        else if (Property.RESOURCES.equals(name)) {
            return new Resources();
        }
        else if (Property.STATUS.equals(name)) {
            return new Status();
        }
        else if (Property.SUMMARY.equals(name)) {
            return new Summary();
        }
        else if (Property.COMPLETED.equals(name)) {
            return new Completed();
        }
        else if (Property.DTEND.equals(name)) {
            return new DtEnd();
        }
        else if (Property.DUE.equals(name)) {
            return new Due();
        }
        else if (Property.DTSTART.equals(name)) {
            return new DtStart();
        }
        else if (Property.DURATION.equals(name)) {
            return new Duration();
        }
        // 4.8.3 Time Zone Component Properties
        else if (Property.TZID.equals(name)) {
            return new TzId();
        }
        else if (Property.TZNAME.equals(name)) {
            return new TzName();
        }
        else if (Property.TZOFFSETFROM.equals(name)) {
            return new TzOffsetFrom();
        }
        else if (Property.TZOFFSETTO.equals(name)) {
            return new TzOffsetTo();
        }
        else if (Property.TZURL.equals(name)) {
            return new TzUrl();
        }
        // 4.8.4 Relationship Component Properties
        else if (Property.ATTENDEE.equals(name)) {
            return new Attendee();
        }
        else if (Property.CONTACT.equals(name)) {
            return new Contact();
        }
        else if (Property.ORGANIZER.equals(name)) {
            return new Organizer();
        }
        else if (Property.RECURRENCE_ID.equals(name)) {
            return new RecurrenceId();
        }
        else if (Property.RELATED_TO.equals(name)) {
            return new RelatedTo();
        }
        else if (Property.URL.equals(name)) {
            return new Url();
        }
        else if (Property.UID.equals(name)) {
            return new Uid();
        }
        // 4.8.5 Recurrence Component Properties
        else if (Property.EXDATE.equals(name)) {
            return new ExDate();
        }
        else if (Property.EXRULE.equals(name)) {
            return new ExRule();
        }
        else if (Property.RDATE.equals(name)) {
            return new RDate();
        }
        else if (Property.RRULE.equals(name)) {
            return new RRule();
        }
        // 4.8.6 Alarm Component Properties
        else if (Property.ACTION.equals(name)) {
            return new Action();
        }
        else if (Property.REPEAT.equals(name)) {
            return new Repeat();
        }
        else if (Property.TRIGGER.equals(name)) {
            return new Trigger();
        }
        // 4.8.7 Change Management Component Properties
        else if (Property.CREATED.equals(name)) {
            return new Created();
        }
        else if (Property.DTSTAMP.equals(name)) {
            return new DtStamp();
        }
        else if (Property.LAST_MODIFIED.equals(name)) {
            return new LastModified();
        }
        else if (Property.SEQUENCE.equals(name)) {
            return new Sequence();
        }
        // 4.8.8 Miscellaneous Component Properties
        else if (Property.REQUEST_STATUS.equals(name)) {
            return new RequestStatus();
        }
        // 4.8.8.1 Non-standard Properties
        else {
            return new XProperty(name);
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

        // calendar properties..
        if (Property.CALSCALE.equals(name)) {
            return new CalScale(parameters, value);
        }
        else if (Property.METHOD.equals(name)) {
            return new Method(parameters, value);
        }
        else if (Property.PRODID.equals(name)) {
            return new ProdId(parameters, value);
        }
        else if (Property.VERSION.equals(name)) {
            return new Version(parameters, value);
        }
        // component properties..
        else if (Property.ATTACH.equals(name)) {
            return new Attach(parameters, value);
        }
        else if (Property.CATEGORIES.equals(name)) {
            return new Categories(parameters, value);
        }
        else if (Property.CLASS.equals(name)) {
            return new Clazz(parameters, value);
        }
        else if (Property.COMMENT.equals(name)) {
            return new Comment(parameters, value);
        }
        else if (Property.DESCRIPTION.equals(name)) {
            return new Description(parameters, value);
        }
        else if (Property.GEO.equals(name)) {
            return new Geo(parameters, value);
        }
        else if (Property.LOCATION.equals(name)) {
            return new Location(parameters, value);
        }
        else if (Property.PERCENT_COMPLETE.equals(name)) {
            return new PercentComplete(parameters, value);
        }
        else if (Property.PRIORITY.equals(name)) {
            return new Priority(parameters, value);
        }
        else if (Property.RESOURCES.equals(name)) {
            return new Resources(parameters, value);
        }
        else if (Property.STATUS.equals(name)) {
            return new Status(parameters, value);
        }
        else if (Property.SUMMARY.equals(name)) {
            return new Summary(parameters, value);
        }
        else if (Property.COMPLETED.equals(name)) {
            return new Completed(parameters, value);
        }
        else if (Property.DTEND.equals(name)) {
            return new DtEnd(parameters, value);
        }
        else if (Property.DUE.equals(name)) {
            return new Due(parameters, value);
        }
        else if (Property.DTSTART.equals(name)) {
            return new DtStart(parameters, value);
        }
        else if (Property.DURATION.equals(name)) {
            return new Duration(parameters, value);
        }
        // 4.8.3 Time Zone Component Properties
        else if (Property.TZID.equals(name)) {
            return new TzId(parameters, value);
        }
        else if (Property.TZNAME.equals(name)) {
            return new TzName(parameters, value);
        }
        else if (Property.TZOFFSETFROM.equals(name)) {
            return new TzOffsetFrom(parameters, value);
        }
        else if (Property.TZOFFSETTO.equals(name)) {
            return new TzOffsetTo(parameters, value);
        }
        else if (Property.TZURL.equals(name)) {
            return new TzUrl(parameters, value);
        }
        // 4.8.4 Relationship Component Properties
        else if (Property.ATTENDEE.equals(name)) {
            return new Attendee(parameters, value);
        }
        else if (Property.CONTACT.equals(name)) {
            return new Contact(parameters, value);
        }
        else if (Property.ORGANIZER.equals(name)) {
            return new Organizer(parameters, value);
        }
        else if (Property.RECURRENCE_ID.equals(name)) {
            return new RecurrenceId(parameters, value);
        }
        else if (Property.RELATED_TO.equals(name)) {
            return new RelatedTo(parameters, value);
        }
        else if (Property.URL.equals(name)) {
            return new Url(parameters, value);
        }
        else if (Property.UID.equals(name)) {
            return new Uid(parameters, value);
        }
        // 4.8.5 Recurrence Component Properties
        else if (Property.EXDATE.equals(name)) {
            return new ExDate(parameters, value);
        }
        else if (Property.EXRULE.equals(name)) {
            return new ExRule(parameters, value);
        }
        else if (Property.RDATE.equals(name)) {
            return new RDate(parameters, value);
        }
        else if (Property.RRULE.equals(name)) {
            return new RRule(parameters, value);
        }
        // 4.8.6 Alarm Component Properties
        else if (Property.ACTION.equals(name)) {
            return new Action(parameters, value);
        }
        else if (Property.REPEAT.equals(name)) {
            return new Repeat(parameters, value);
        }
        else if (Property.TRIGGER.equals(name)) {
            return new Trigger(parameters, value);
        }
        // 4.8.7 Change Management Component Properties
        else if (Property.CREATED.equals(name)) {
            return new Created(parameters, value);
        }
        else if (Property.DTSTAMP.equals(name)) {
            return new DtStamp(parameters, value);
        }
        else if (Property.LAST_MODIFIED.equals(name)) {
            return new LastModified(parameters, value);
        }
        else if (Property.SEQUENCE.equals(name)) {
            return new Sequence(parameters, value);
        }
        // 4.8.8 Miscellaneous Component Properties
        else if (Property.REQUEST_STATUS.equals(name)) {
            return new RequestStatus(parameters, value);
        }
        // 4.8.8.1 Non-standard Properties
        else {
            return new XProperty(name, parameters, value);
        }
    }
}