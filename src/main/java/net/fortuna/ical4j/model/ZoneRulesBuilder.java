package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.RRule;

import java.time.*;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.time.zone.ZoneOffsetTransitionRule.TimeDefinition;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            if (!observance.getOffsetFrom().getOffset().equals(observance.getOffsetTo().getOffset())) {
                transitions.add(ZoneOffsetTransition.of(observance.getStartDate().getDate(),
                        observance.getOffsetFrom().getOffset(), observance.getOffsetTo().getOffset()));
            }
        }
        return transitions;
    }

    private List<ZoneOffsetTransitionRule> buildTransitionRules(List<Observance> observances, ZoneOffset standardOffset) {
        List<ZoneOffsetTransitionRule> transitionRules = new ArrayList<>();
        for (Observance observance : observances) {
            RRule rrule = observance.getProperty(Property.RRULE);
            if (rrule != null) {
                Month recurMonth = Month.of(rrule.getRecur().getMonthList().get(0));
                int dayOfMonth = rrule.getRecur().getDayList().get(0).getOffset();
                DayOfWeek dayOfWeek = WeekDay.getDayOfWeek(rrule.getRecur().getDayList().get(0));
                LocalTime time = LocalTime.from(observance.getStartDate().getDate());
                boolean endOfDay = false;
                TimeDefinition timeDefinition = TimeDefinition.UTC;
                transitionRules.add(ZoneOffsetTransitionRule.of(recurMonth, dayOfMonth, dayOfWeek, time, endOfDay,
                        timeDefinition, standardOffset, observance.getOffsetFrom().getOffset(),
                        observance.getOffsetTo().getOffset()));
            }
        }
        return transitionRules;
    }

    public ZoneRules build() {
        Observance current = vTimeZone.getApplicableObservance(Instant.now(),
                vTimeZone.getObservances().getComponents(Observance.STANDARD));
        ZoneOffset standardOffset = current.getOffsetTo().getOffset();
        ZoneOffset wallOffset = current.getOffsetFrom().getOffset();
        List<ZoneOffsetTransition> standardOffsetTransitions = buildTransitions(
                vTimeZone.getObservances().getComponents(Observance.STANDARD));
        Collections.sort(standardOffsetTransitions);
        List<ZoneOffsetTransition> offsetTransitions = buildTransitions(
                vTimeZone.getObservances().getComponents(Observance.DAYLIGHT));
        Collections.sort(offsetTransitions);
        List<ZoneOffsetTransitionRule> transitionRules = buildTransitionRules(
                vTimeZone.getObservances(), standardOffset);

        return ZoneRules.of(standardOffset, wallOffset, standardOffsetTransitions, offsetTransitions, transitionRules);
    }
}
