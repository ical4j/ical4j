package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;

import java.time.Month;
import java.time.*;
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
            return new TemporalComparator().compare(o1Start.getDate(), o2Start.getDate());
        });

        for (Standard observance : sorted) {
            // ignore transitions that have no effect..
            Optional<TzOffsetFrom> offsetFrom = observance.getProperty(Property.TZOFFSETFROM);
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

        Observance next = null;

        // reverse iterate observances to calculate historical occurrences
        List<Observance> sorted = new ArrayList<>(observances);
        sorted.sort((o1, o2) -> {
            DtStart<LocalDateTime> o1Start = o1.getRequiredProperty("DTSTART");
            DtStart<LocalDateTime> o2Start = o2.getRequiredProperty("DTSTART");
            return new TemporalComparator().compare(o1Start.getDate(), o2Start.getDate());
        });
        Collections.reverse(sorted);

        for (Observance observance : sorted) {
            // ignore transitions that have no effect..
            Optional<TzOffsetFrom> offsetFrom = observance.getProperty(Property.TZOFFSETFROM);
            TzOffsetTo offsetTo = observance.getRequiredProperty(Property.TZOFFSETTO);

            if (offsetFrom.isPresent() && !offsetFrom.get().getOffset().equals(offsetTo.getOffset())) {
                Period<LocalDateTime> span;
                DtStart<LocalDateTime> start = observance.getRequiredProperty("DTSTART");
                // if no next observance use current date for period end..
                if (next == null) {
                    span = new Period<>(start.getDate(), LocalDateTime.now());
                } else {
                    DtStart<LocalDateTime> nextStart = next.getRequiredProperty("DTSTART");
                    span = new Period<>(start.getDate(), nextStart.getDate());
                }

                observance.calculateRecurrenceSet(span).forEach( p -> {
                    transitions.add(ZoneOffsetTransition.of(p.getStart(),
                            offsetFrom.get().getOffset(), offsetTo.getOffset()));
                });

                next = observance;
            }
        }
        return transitions;
    }

    private Set<ZoneOffsetTransitionRule> buildTransitionRules(List<Observance> observances, ZoneOffset standardOffset) throws ConstraintViolationException {
        Set<ZoneOffsetTransitionRule> transitionRules = new HashSet<>();

        for (Observance observance : observances) {
            Optional<RRule<?>> rrule = observance.getProperty(Property.RRULE);
            TzOffsetFrom offsetFrom = observance.getRequiredProperty(Property.TZOFFSETFROM);
            TzOffsetTo offsetTo = observance.getRequiredProperty(Property.TZOFFSETTO);
            DtStart<LocalDateTime> startDate = observance.getRequiredProperty(Property.DTSTART);

            // ignore invalid rules
            if (rrule.isPresent() && !rrule.get().getRecur().getMonthList().isEmpty()) {
                Month recurMonth = java.time.Month.of(rrule.get().getRecur().getMonthList().get(0).getMonthOfYear());
                int dayOfMonth = rrule.get().getRecur().getDayList().get(0).getOffset();
                if (dayOfMonth == 0) {
                    dayOfMonth = rrule.get().getRecur().getMonthDayList().get(0);
                }
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
        Observance currentStandard = VTimeZone.getApplicableObservance(Instant.now(),
                vTimeZone.getComponents(Observance.STANDARD));

        Observance currentDaylight = VTimeZone.getApplicableObservance(Instant.now(),
                vTimeZone.getComponents(Observance.DAYLIGHT));

        // if no standard time use daylight time..
        if (currentStandard == null) {
            currentStandard = currentDaylight;
        }

        TzOffsetFrom offsetFrom = currentStandard.getRequiredProperty(Property.TZOFFSETFROM);
        TzOffsetTo offsetTo = currentStandard.getRequiredProperty(Property.TZOFFSETTO);

        ZoneOffset standardOffset = offsetTo.getOffset();
        ZoneOffset wallOffset = offsetFrom.getOffset();

        List<Standard> stdObservances = vTimeZone.getComponents(Observance.STANDARD);
        List<ZoneOffsetTransition> standardOffsetTransitions = buildStandardOffsetTransitions(stdObservances);
        Collections.sort(standardOffsetTransitions);

        List<ZoneOffsetTransition> standardTransitions = buildDSTTransitions(
                vTimeZone.getComponents(Observance.STANDARD));
        List<ZoneOffsetTransition> offsetTransitions = buildDSTTransitions(
                vTimeZone.getComponents(Observance.DAYLIGHT));
        offsetTransitions.addAll(standardTransitions);
        Collections.sort(offsetTransitions);

        //XXX: only create transition rules from the latest definitions..
//        Set<ZoneOffsetTransitionRule> transitionRules = buildTransitionRules(
//                vTimeZone.getObservances(), standardOffset);
        //xxx: order of transition rules is significant..
        List<Observance> latestObservances = Arrays.asList(currentDaylight, currentStandard);
        Set<ZoneOffsetTransitionRule> transitionRules = buildTransitionRules(latestObservances.stream()
                .filter(Objects::nonNull).collect(Collectors.toList()), standardOffset);

        return ZoneRules.of(standardOffset, wallOffset, standardOffsetTransitions, offsetTransitions,
                new ArrayList<>(transitionRules));
    }
}
