/**
 * Copyright (c) 2026, Ben Fortuna
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

package net.fortuna.ical4j.fuzz;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ConstraintViolationException;
import net.fortuna.ical4j.model.DateTimePropertyAccessor;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.AltRep;
import net.fortuna.ical4j.model.parameter.Dir;
import net.fortuna.ical4j.model.parameter.Gap;
import net.fortuna.ical4j.model.parameter.Rsvp;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.DateListProperty;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.EstimatedDuration;
import net.fortuna.ical4j.model.property.ExRule;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Geo;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.RequestStatus;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Trigger;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.model.property.TzUrl;
import net.fortuna.ical4j.model.property.Url;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 09 — the typed accessors consumers call on a parsed calendar (byte 0 as in 01). Each returns, or throws
 * DateTimeException (DateTimeParseException, ZoneRulesException...), IllegalArgumentException or
 * ConstraintViolationException; anything else escapes and is the finding. A VALUE=DATE date that is not a LocalDate,
 * or a VALUE=DATE-TIME one that is, is a type contradiction.
 */
public class LazyValueAccessorsFuzzer {

    private static TimeZoneRegistry registry;

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    private static Object call(Supplier<?> accessor) {
        try {
            return accessor.get();
        } catch (DateTimeException | IllegalArgumentException | ConstraintViolationException e) {
            return null;
        }
    }

    public static void fuzzerTestOneInput(byte[] input) {
        if (input.length < 1) {
            return;
        }
        int selector = input[0] & 0xff;
        Ical.hints((selector & 1) != 0);
        CalendarBuilder builder = Ical.builder((selector & 2) != 0);
        Calendar calendar;
        try {
            calendar = builder.build(new ByteArrayInputStream(input, 1, input.length - 1));
        } catch (ParserException | IOException e) {
            return;
        }
        if (calendar == null) {
            return;
        }
        registry = builder.getRegistry();
        calendar.getProperties().forEach(LazyValueAccessorsFuzzer::property);
        for (Component c : calendar.getComponents()) {
            component(c, 0);
        }
    }

    private static void component(Component c, int depth) {
        c.getProperties().forEach(LazyValueAccessorsFuzzer::property);
        if (c instanceof VEvent) {
            VEvent event = (VEvent) c;
            call(() -> event.getEndDate(true));
            call(() -> event.getEndDate(false));
            call(() -> {
                Optional<DtStart<Temporal>> start = event.getStartDate();
                // an empty DTSTART has no date: getOccurrence(null) would be a misuse by the caller
                return start.map(DtStart::getDate).map(event::getOccurrence).orElse(null);
            });
        }
        if (c instanceof DateTimePropertyAccessor) {
            DateTimePropertyAccessor a = (DateTimePropertyAccessor) c;
            call(a::getDateTimeStart);
            call(a::getDateTimeEnd);
            call(a::getDateTimeDue);
        }
        if (c instanceof VAlarm) {
            VAlarm alarm = (VAlarm) c;
            call(() -> alarm.getTrigger().map(Trigger::getDuration));
            call(() -> alarm.getTrigger().map(Trigger::getDate));
            call(alarm::getDuration);
            call(alarm::getRepeat);
        }
        if (depth < 12) {
            for (Component sub : Diff.subComponents(c)) {
                component(sub, depth + 1);
            }
        }
    }

    private static void checkType(Property p, Object date) {
        if (!(date instanceof Temporal)) {
            return;
        }
        Optional<Value> value = p.getParameter(Parameter.VALUE);
        if (value.isEmpty()) {
            return;
        }
        if (Value.DATE.equals(value.get()) && !(date instanceof LocalDate)) {
            throw new AssertionError(p.getName() + ";VALUE=DATE yields a " + date.getClass().getSimpleName()
                    + " (" + date + "), from [" + Ical.abbreviate(p.toString(), 200) + "]");
        }
        if (Value.DATE_TIME.equals(value.get()) && date instanceof LocalDate) {
            throw new AssertionError(p.getName() + ";VALUE=DATE-TIME yields a LocalDate (" + date + "), from ["
                    + Ical.abbreviate(p.toString(), 200) + "]");
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void property(Property p) {
        call(p::getValue);
        if (p instanceof DateProperty) {
            DateProperty<?> d = (DateProperty<?>) p;
            checkType(p, call(d::getDate));
        }
        if (p instanceof DateListProperty) {
            Object dates = call(((DateListProperty<?>) p)::getDates);
            if (dates instanceof List) {
                for (Object date : (List<?>) dates) {
                    checkType(p, date);
                }
            }
        }
        if (p instanceof RDate) {
            call(((RDate<?>) p)::getPeriods);
        }
        if (p instanceof RRule) {
            call(((RRule<?>) p)::getRecur);
        }
        if (p instanceof ExRule) {
            call(((ExRule<?>) p)::getRecur);
        }
        if (p instanceof Duration) {
            call(((Duration) p)::getDuration);
        }
        if (p instanceof EstimatedDuration) {
            call(((EstimatedDuration) p)::getDuration);
        }
        if (p instanceof Trigger) {
            call(((Trigger) p)::getDuration);
            call(((Trigger) p)::isAbsolute);
        }
        if (p instanceof TzOffsetFrom) {
            call(((TzOffsetFrom) p)::getOffset);
        }
        if (p instanceof TzOffsetTo) {
            call(((TzOffsetTo) p)::getOffset);
        }
        if (p instanceof Attendee) {
            call(((Attendee) p)::getCalAddress);
        }
        if (p instanceof Organizer) {
            call(((Organizer) p)::getCalAddress);
        }
        if (p instanceof Url) {
            call(((Url) p)::getUri);
        }
        if (p instanceof TzUrl) {
            call(((TzUrl) p)::getUri);
        }
        if (p instanceof Attach) {
            call(((Attach) p)::getUri);
            call(((Attach) p)::getBinary);
            call(((Attach) p)::getBinaryData);
        }
        if (p instanceof Geo) {
            call(((Geo) p)::getLatitude);
            call(((Geo) p)::getLongitude);
        }
        if (p instanceof Sequence) {
            call(((Sequence) p)::getSequenceNo);
        }
        if (p instanceof Priority) {
            call(((Priority) p)::getLevel);
        }
        if (p instanceof PercentComplete) {
            call(((PercentComplete) p)::getPercentage);
        }
        if (p instanceof Repeat) {
            call(((Repeat) p)::getCount);
        }
        if (p instanceof FreeBusy) {
            call(((FreeBusy) p)::getIntervals);
        }
        if (p instanceof RequestStatus) {
            call(((RequestStatus) p)::getStatusCode);
            call(((RequestStatus) p)::getDescription);
            call(((RequestStatus) p)::getExData);
        }
        for (Parameter param : p.getParameterList().getAll()) {
            call(param::getValue);
            if (param instanceof TzId) {
                call(() -> ((TzId) param).toZoneId(registry));
                call(((TzId) param)::toZoneId);
            }
            if (param instanceof Rsvp) {
                call(((Rsvp) param)::getRsvp);
            }
            if (param instanceof AltRep) {
                call(((AltRep) param)::getUri);
            }
            if (param instanceof Dir) {
                call(((Dir) param)::getUri);
            }
            if (param instanceof Gap) {
                call(((Gap) param)::getDuration);
            }
        }
    }
}
