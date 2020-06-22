package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;

import java.time.*;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.time.zone.ZoneOffsetTransitionRule.TimeDefinition;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Construct a {@link java.time.zone.ZoneRules} instance from a {@link net.fortuna.ical4j.model.component.VTimeZone}.
 */
public class ZoneRulesBuilder {

    private VTimeZone vTimeZone;

    public ZoneRulesBuilder vTimeZone(VTimeZone vTimeZone) {
        this.vTimeZone = vTimeZone;
        return this;
    }

    private List<ZoneOffsetTransition> buildTransitions(List<Observance> observances) {
        List<ZoneOffsetTransition> transitions = new ArrayList<>();
        for (Observance observance : observances) {
            // ignore transitions that have no effect..
            Optional<TzOffsetFrom> offsetFrom = observance.getProperties().getFirst(Property.TZOFFSETFROM);
            Optional<TzOffsetTo> offsetTo = observance.getProperties().getFirst(Property.TZOFFSETTO);

            if (offsetFrom.isPresent() && !offsetFrom.get().getOffset().equals(offsetTo.get().getOffset())) {
                Optional<DtStart<LocalDateTime>> startDate = observance.getProperties().getFirst(Property.DTSTART);
                if (startDate.isPresent()) {
                    transitions.add(ZoneOffsetTransition.of(startDate.get().getDate(),
                            offsetFrom.get().getOffset(), offsetTo.get().getOffset()));
                } else {
                    throw new CalendarException("Missing DTSTART property");
                }
            }
        }
        return transitions;
    }

    private List<ZoneOffsetTransitionRule> buildTransitionRules(List<Observance> observances, ZoneOffset standardOffset) throws ConstraintViolationException {
        List<ZoneOffsetTransitionRule> transitionRules = new ArrayList<>();
        for (Observance observance : observances) {
            Optional<RRule<?>> rrule = observance.getProperties().getFirst(Property.RRULE);
            TzOffsetFrom offsetFrom = observance.getProperties().getRequired(Property.TZOFFSETFROM);
            TzOffsetTo offsetTo = observance.getProperties().getRequired(Property.TZOFFSETTO);
            DtStart<LocalDateTime> startDate = observance.getProperties().getRequired(Property.DTSTART);

            if (rrule.isPresent()) {
                Month recurMonth = Month.of(rrule.get().getRecur().getMonthList().get(0));
                int dayOfMonth = rrule.get().getRecur().getDayList().get(0).getOffset();
                DayOfWeek dayOfWeek = WeekDay.getDayOfWeek(rrule.get().getRecur().getDayList().get(0));
                LocalTime time = LocalTime.from(startDate.getDate());
                boolean endOfDay = false;
                TimeDefinition timeDefinition = TimeDefinition.UTC;
                transitionRules.add(ZoneOffsetTransitionRule.of(recurMonth, dayOfMonth, dayOfWeek, time, endOfDay,
                        timeDefinition, standardOffset, offsetFrom.getOffset(), offsetTo.getOffset()));
            }
        }
        return transitionRules;
    }

    public ZoneRules build() throws ConstraintViolationException {
        Observance current = VTimeZone.getApplicableObservance(Instant.now(),
                vTimeZone.getObservances().get(Observance.STANDARD));

        // if no standard time use daylight time..
        if (current == null) {
            current = VTimeZone.getApplicableObservance(Instant.now(),
                    vTimeZone.getObservances().get(Observance.DAYLIGHT));
        }

        TzOffsetFrom offsetFrom = current.getProperties().getRequired(Property.TZOFFSETFROM);
        TzOffsetTo offsetTo = current.getProperties().getRequired(Property.TZOFFSETTO);

        ZoneOffset standardOffset = offsetTo.getOffset();
        ZoneOffset wallOffset = offsetFrom.getOffset();
        List<ZoneOffsetTransition> standardOffsetTransitions = buildTransitions(
                vTimeZone.getObservances().get(Observance.STANDARD));
        Collections.sort(standardOffsetTransitions);
        List<ZoneOffsetTransition> offsetTransitions = buildTransitions(
                vTimeZone.getObservances().get(Observance.DAYLIGHT));
        Collections.sort(offsetTransitions);
        List<ZoneOffsetTransitionRule> transitionRules = buildTransitionRules(
                vTimeZone.getObservances().getAll(), standardOffset);

        return ZoneRules.of(standardOffset, wallOffset, standardOffsetTransitions, offsetTransitions, transitionRules);
    }
}
