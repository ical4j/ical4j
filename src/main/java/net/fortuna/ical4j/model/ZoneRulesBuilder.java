package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.time.zone.ZoneOffsetTransitionRule.TimeDefinition;
import java.time.zone.ZoneRules;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Construct a {@link java.time.zone.ZoneRules} instance from a {@link net.fortuna.ical4j.model.component.VTimeZone}.
 */
public class ZoneRulesBuilder {

    private VTimeZone vTimeZone;

    public ZoneRulesBuilder vTimeZone(VTimeZone vTimeZone) {
        this.vTimeZone = vTimeZone;
        return this;
    }

    /**
     * Build a list of transitions for the recognised standard offset. For example, where the standard UTC
     * offset changes from -7 to -8 permanently.
     * @param observances
     * @return
     */
    private List<ZoneOffsetTransition> buildStandardOffsetTransitions(List<Standard> observances) {
        List<ZoneOffsetTransition> transitions = new ArrayList<>();

        // sort and iterate comparing the standard offset for each observance
        ZoneOffset prevOffset = null;

        // reverse iterate observances to calculate historical occurrences
        List<Standard> sorted = new ArrayList<>(observances);
        sorted.sort((o1, o2) -> {
            DtStart<LocalDateTime> o1Start = o1.getRequiredProperty("DTSTART");
            DtStart<LocalDateTime> o2Start = o2.getRequiredProperty("DTSTART");
            return TemporalComparator.INSTANCE.compare(o1Start.getDate(), o2Start.getDate());
        });

        for (var observance : sorted) {
            // ignore transitions that have no effect..
            Optional<TzOffsetFrom> offsetFrom = observance.getTimeZoneOffsetFrom();
            TzOffsetTo offsetTo = observance.getRequiredProperty(Property.TZOFFSETTO);
            DtStart<LocalDateTime> start = observance.getRequiredProperty("DTSTART");

            if (prevOffset != null && offsetFrom.isPresent()) {
                if (!offsetTo.getOffset().equals(prevOffset)) {
                    transitions.add(ZoneOffsetTransition.of(start.getDate(), prevOffset, offsetTo.getOffset()));
                }
            }
            prevOffset = offsetTo.getOffset();
        }

        return transitions;
    }

    /**
     * Build a list of transitions for DST changes. These are typically temporary offset changes every six months.
     * @param observances
     * @return
     */
    private List<ZoneOffsetTransition> buildDSTTransitions(List<Observance> observances) {
        List<ZoneOffsetTransition> transitions = new ArrayList<>();

        for (Observance observance : observances) {
            Optional<TzOffsetFrom> offsetFrom = observance.getTimeZoneOffsetFrom();
            TzOffsetTo offsetTo = observance.getRequiredProperty(Property.TZOFFSETTO);

            // ignore transitions that have no effect..
            if (offsetFrom.isPresent() && !offsetFrom.get().getOffset().equals(offsetTo.getOffset())) {
                final DtStart<Temporal> start = observance.getRequiredProperty("DTSTART");
                LocalDateTime startDate;
                if (!(start.getDate() instanceof LocalDateTime)) {
                    if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
                        startDate = LocalDateTime.from(start.getDate());
                    } else {
                        throw new RuntimeException("VTIMEZONE start date must be specified in local time");
                    }
                } else {
                    startDate = (LocalDateTime) start.getDate();
                }
                final LocalDateTime periodEnd = LocalDateTime.now().plusYears(5);
                observance.calculateRecurrenceSet(new Period<>(startDate, periodEnd)).forEach( p -> {
                    transitions.add(ZoneOffsetTransition.of(LocalDateTime.from(p.getStart()),
                            offsetFrom.get().getOffset(), offsetTo.getOffset()));
                });
            }
        }
        return transitions;
    }

    private List<ZoneOffsetTransitionRule> buildTransitionRules(List<Observance> observances, ZoneOffset standardOffset) throws ConstraintViolationException {
        List<ZoneOffsetTransitionRule> transitionRules = new ArrayList<>();

        for (Observance observance : observances) {
            Optional<RRule<?>> rrule = observance.getProperty(Property.RRULE);
            TzOffsetFrom offsetFrom = observance.getRequiredProperty(Property.TZOFFSETFROM);
            TzOffsetTo offsetTo = observance.getRequiredProperty(Property.TZOFFSETTO);
            DtStart<LocalDateTime> startDate = observance.getRequiredProperty(Property.DTSTART);

            // ignore invalid rules
            if (rrule.isPresent() && !rrule.get().getRecur().getMonthList().isEmpty()) {
                var recurMonth = java.time.Month.of(rrule.get().getRecur().getMonthList().get(0).getMonthOfYear());
                int dayOfMonth = rrule.get().getRecur().getDayList().get(0).getOffset();
                if (dayOfMonth == 0) {
                    dayOfMonth = rrule.get().getRecur().getMonthDayList().get(0);
                }
                var dayOfWeek = WeekDay.getDayOfWeek(rrule.get().getRecur().getDayList().get(0));
                var time = LocalTime.from(startDate.getDate());
                boolean endOfDay = false;
                var timeDefinition = TimeDefinition.UTC;
                transitionRules.add(ZoneOffsetTransitionRule.of(recurMonth, dayOfMonth, dayOfWeek, time, endOfDay,
                        timeDefinition, standardOffset, offsetFrom.getOffset(), offsetTo.getOffset()));
            }
        }
        return transitionRules;
    }

    public ZoneRules build() throws ConstraintViolationException {
        var now = Instant.now();
        var currentStandard = VTimeZone.getApplicableObservance(now,
                vTimeZone.getComponents(Observance.STANDARD));

        var currentDaylight = VTimeZone.getApplicableObservance(now,
                vTimeZone.getComponents(Observance.DAYLIGHT));

        // if no standard time use daylight time..
        if (currentStandard == null) {
            currentStandard = currentDaylight;
        }

        TzOffsetFrom offsetFrom = currentStandard.getRequiredProperty(Property.TZOFFSETFROM);
        TzOffsetTo offsetTo = currentStandard.getRequiredProperty(Property.TZOFFSETTO);

        var standardOffset = offsetTo.getOffset();
        var wallOffset = offsetFrom.getOffset();

        List<Standard> stdObservances = vTimeZone.getComponents(Observance.STANDARD);
        List<ZoneOffsetTransition> standardOffsetTransitions = buildStandardOffsetTransitions(stdObservances);
        Collections.sort(standardOffsetTransitions);

        List<ZoneOffsetTransition> offsetTransitions = buildDSTTransitions(vTimeZone.getObservances());
        Collections.sort(offsetTransitions);

        // only create transition rules from the latest definitions..
        // NOTE: order of transition rules is significant.. if currently in DST next transition should be
        // to standard time..
        List<Observance> latestObservances = new ArrayList<>();
        if (vTimeZone.getApplicableObservance(now).equals(currentDaylight)) {
            latestObservances.add(currentStandard);
            latestObservances.add(currentDaylight);
        } else {
            latestObservances.add(currentDaylight);
            latestObservances.add(currentStandard);
        }
        latestObservances = latestObservances.stream().filter(Objects::nonNull).collect(Collectors.toList());

        List<ZoneOffsetTransitionRule> transitionRules = buildTransitionRules(latestObservances, standardOffset);

        return ZoneRules.of(standardOffset, wallOffset, standardOffsetTransitions, offsetTransitions, transitionRules);
    }
}
