package net.fortuna.ical4j.model;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.HasPropertyRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;

import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Predicate;

/**
 * Support for operations applicable to a group of components. Typically this class is used to manage
 * component revisions (whereby each revision is a separate component), and the resulting output of
 * such group functions.
 *
 * Example - Find latest revision of an event:
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
public class ComponentGroup<C extends Component> {

    private final List<C> components;

    private final Filter<C> componentFilter;

    public ComponentGroup(List<C> components, Uid uid) {
        this(components, uid, null);
    }

    public ComponentGroup(List<C> components, Uid uid, RecurrenceId recurrenceId) {
        this.components = components;

        Predicate<C> componentPredicate;
        if (recurrenceId != null) {
            componentPredicate = new HasPropertyRule<C>(uid).and(new HasPropertyRule<C>(recurrenceId));
        } else {
            componentPredicate = new HasPropertyRule<C>(uid);
        }
        componentFilter = new Filter<>(componentPredicate);
    }

    /**
     * Apply filter to all components to create a subset containing components
     * matching the specified UID.
     *
     * @return
     */
    public List<C> getRevisions() {
        return (List<C>) componentFilter.filter(components);
    }

    /**
     * Returns the latest component revision based on ascending sequence number and modified date.
     *
     * @return
     */
    public C getLatestRevision() {
        List<C> revisions = getRevisions();
        revisions.sort(new ComponentSequenceComparator());
        Collections.reverse(revisions);
        return revisions.iterator().next();
    }

    /**
     * Calculate all recurring periods for the specified date range. This method will take all
     * revisions into account when generating the set. Component revisions with a RECURRENCE_ID property are
     * processed last, as they override instances in the default recurrence set.
     *
     * @param period
     * @return
     *
     * @see Component#calculateRecurrenceSet(Period)
     */
    public <T extends Temporal> List<Period<T>> calculateRecurrenceSet(final Period<T> period) {
        // Use set to exclude duplicates..
        Set<Period<T>> periods = new HashSet<>();
        List<Component> overrides = new ArrayList<>();

        for (Component component : getRevisions()) {
            if (component.getProperties().getFirst(Property.RECURRENCE_ID).isPresent()) {
                overrides.add(component);
            } else {
                periods.addAll(component.calculateRecurrenceSet(period));
            }
        }

        List<Period<T>> finalPeriods = new ArrayList<>(periods);
        overrides.forEach(component -> {
            try {
                RecurrenceId<?> recurrenceId = component.getProperties().getRequired(Property.RECURRENCE_ID);
                finalPeriods.removeIf(p -> p.getStart().equals(recurrenceId.getDate()));
                component.calculateRecurrenceSet(period).stream()
                        .filter(p -> p.getStart().equals(recurrenceId.getDate()))
                        .forEach(finalPeriods::add);
            } catch (ConstraintViolationException cve) {
                // Should never reach as we previously determined existence of RECURRENCE_ID property..
            }
        });

        // Natural sort of final list..
        Collections.sort(finalPeriods);
        return finalPeriods;
    }
}
