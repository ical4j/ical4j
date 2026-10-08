package net.fortuna.ical4j.model;

import net.fortuna.ical4j.filter.predicate.PropertyEqualToRule;
import net.fortuna.ical4j.filter.predicate.PropertyExistsRule;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;

import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Support for operations applicable to a group of components. Typically, this class is used to manage
 * component revisions (whereby each revision is a separate component), and the resulting output of
 * such group functions.
 *
 * Example - Find the latest revision of an event:
 *
 * <pre>
 *     Calendar calendar = ...
 *     String uidString = ...
 *
 *     ComponentGroup<VEvent> group = new ComponentGroup(
 *          calendar.getComponents(Component.VEVENT),
 *          new Uid(uidString));
 *
 *     return group.getLatestRevision();
 * </pre>
 *
 * Created by fortuna on 20/07/2017.
 */
public class ComponentGroup<C extends Component> implements ComponentContainer<C> {

    private ComponentList<C> componentList;

    private final Uid uid;

    private final Predicate<C> componentPredicate;

    /**
     * Construct a component group filtered on {@link Uid}. Note that this will exclude any recurrence instances
     * as specified by the presence of a {@link RecurrenceId} property.
     *
     * @param uid the UID to filter on
     */
    public ComponentGroup(Uid uid) {
        this(new ArrayList<>(), uid);
    }

    /**
     * Construct a component group filtered on a specific recurrence of a {@link Uid}. Note that this will
     * only include revisions of a single recurrence instance
     * as specified by the {@link RecurrenceId} property.
     *
     * @param uid the UID to filter on
     */
    public ComponentGroup(Uid uid, RecurrenceId<?> recurrenceId) {
        this(new ArrayList<>(), uid, recurrenceId);
    }

    public ComponentGroup(List<C> components, Uid uid) {
        this(new ComponentList<>(components), uid);
    }

    public ComponentGroup(ComponentList<C> components, Uid uid) {
        this.uid = uid;
        this.componentPredicate = new PropertyEqualToRule<C>(uid)
                .and(new PropertyExistsRule<>(new RecurrenceId<>()).negate());
        this.componentList = components;
    }

    public ComponentGroup(List<C> components, Uid uid, RecurrenceId<?> recurrenceId) {
        this(new ComponentList<>(components), uid, recurrenceId);
    }

    public ComponentGroup(ComponentList<C> components, Uid uid, RecurrenceId<?> recurrenceId) {
        this.uid = uid;
        this.componentPredicate = new PropertyEqualToRule<C>(uid).and(new PropertyEqualToRule<>(recurrenceId));
        this.componentList = components;
    }

    @Override
    public ComponentList<C> getComponentList() {
        return componentList;
    }

    @Override
    public void setComponentList(ComponentList<C> components) {
        this.componentList = components;
    }

    @Override
    public <T extends ComponentContainer<C>> T add(C component) {
        if (!componentPredicate.test(component)) {
            throw new IllegalArgumentException("Incompatible component");
        }
        return ComponentContainer.super.add(component);
    }

    @Override
    public <T extends ComponentContainer<C>> T replace(C component) {
        if (!componentPredicate.test(component)) {
            throw new IllegalArgumentException("Incompatible component");
        }
        return ComponentContainer.super.replace(component);
    }

    /**
     * Apply filter to all components to create a subset containing components
     * matching the specified UID.
     *
     * @return
     */
    public List<C> getRevisions() {
        return getComponents().stream().filter(componentPredicate).collect(Collectors.toList());
    }

    /**
     * Returns the latest component revision based on ascending sequence number and modified date.
     *
     * @return
     */
    public C getLatestRevision() {
        List<C> revisions = new ArrayList<>(getRevisions());
        revisions.sort(new ComponentSequenceComparator());
        Collections.reverse(revisions);
        return revisions.iterator().next();
    }

    /**
     * Calculate all recurring periods for the specified date range. This method will take all
     * revisions into account when generating the set. Component revisions with a RECURRENCE_ID property are
     * processed last, as they override instances in the default recurrence set.
     *
     * <p>Every component sharing this group's {@link Uid} participates, including recurrence instances
     * (components with a {@link RecurrenceId}), irrespective of the group's own filter predicate. Each
     * recurrence instance replaces the master occurrence matching its RECURRENCE-ID (compared by temporal
     * value via {@link TemporalComparator}) with its own single occurrence derived from its DTSTART and
     * DTEND/DUE/DURATION, wherever that occurrence falls (RFC 5545 section 3.8.4.4). Any RRULE, RDATE,
     * EXDATE or EXRULE on a recurrence instance is ignored, so an instance that wrongly carries the master's
     * RRULE cannot regenerate the series. RANGE=THISANDFUTURE is not supported.</p>
     *
     * @param period
     * @return
     *
     * @see Component#calculateRecurrenceSet(Period)
     */
    public <T extends Temporal> List<Period<T>> calculateRecurrenceSet(final Period<? extends Temporal> period) {
        // Use set to exclude duplicates..
        Set<Period<T>> periods = new HashSet<>();
        List<Component> overrides = new ArrayList<>();

        // select by UID alone: the group predicate may exclude recurrence instances, but they are
        // required here to override the master occurrences..
        for (Component component : getComponents().stream()
                .filter(new PropertyEqualToRule<>(uid)).collect(Collectors.toList())) {
            if (component.getProperty(Property.RECURRENCE_ID).isPresent()) {
                overrides.add(component);
            } else {
                periods.addAll(component.calculateRecurrenceSet(period));
            }
        }

        List<Period<T>> finalPeriods = new ArrayList<>(periods);
        overrides.forEach(component -> {
            RecurrenceId<?> recurrenceId = component.getRequiredProperty(Property.RECURRENCE_ID);
            finalPeriods.removeIf(p -> TemporalComparator.INSTANCE.compare(p.getStart(), recurrenceId.getDate()) == 0);
            finalPeriods.addAll(overrideOccurrence(component, period));
        });

        // Natural sort of final list..
        Collections.sort(finalPeriods);
        return finalPeriods;
    }

    /**
     * Derive the single occurrence represented by a recurrence instance (a component with a RECURRENCE-ID),
     * i.e. its own DTSTART with the effective duration from DTEND/DUE/DURATION, clipped to the specified
     * period. Recurrence and exception properties on the instance are deliberately ignored.
     */
    @SuppressWarnings("unchecked")
    private static <T extends Temporal> Set<Period<T>> overrideOccurrence(Component override,
                                                                           Period<? extends Temporal> period) {
        final Optional<DtStart<T>> start = override.getProperty(Property.DTSTART);
        if (start.isEmpty()) {
            return Collections.emptySet();
        }
        Optional<DateProperty<T>> end = override.getProperty(Property.DTEND);
        if (end.isEmpty()) {
            end = override.getProperty(Property.DUE);
        }
        final Optional<Duration> duration = override.getProperty(Property.DURATION);

        final Set<Period<T>> occurrence = new RecurrenceSet.Builder<T>()
                .start(start.get().getDate())
                .end(end.map(DateProperty::getDate).orElse(null))
                .duration(duration.map(Duration::getDuration).orElse(null))
                .period(period)
                .build();
        occurrence.forEach(p -> p.setComponent(override));
        return occurrence;
    }
}
