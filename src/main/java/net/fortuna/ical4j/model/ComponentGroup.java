package net.fortuna.ical4j.model;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.HasPropertyRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;

import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private final ComponentList<C> components;

    private final Filter<C> componentFilter;

    public ComponentGroup(ComponentList<C> components, Uid uid) {
        this(components, uid, null);
    }

    public ComponentGroup(ComponentList<C> components, Uid uid, RecurrenceId recurrenceId) {
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
    public ComponentList<C> getRevisions() {
        return (ComponentList<C>) componentFilter.filter(components);
    }

    /**
     * Returns the latest component revision based on ascending sequence number and modified date.
     *
     * @return
     */
    public C getLatestRevision() {
        ComponentList<C> revisions = getRevisions();
        revisions.sort(new ComponentSequenceComparator());
        Collections.reverse(revisions);
        return revisions.iterator().next();
    }

    /**
     * Calculate all recurring periods for the specified date range. This method will take all
     * revisions into account when generating the set.
     *
     * @param period
     * @return
     *
     * @see Component#calculateRecurrenceSet(Period)
     */
    public <T extends Temporal> List<Period<T>> calculateRecurrenceSet(final Period<T> period) {
        // Use set to exclude duplicates..
        Set<Period<T>> periods = new HashSet<>();
        List<Component> replacements = new ArrayList<>();

        for (Component component : getRevisions()) {
            if (!component.getProperties(Property.RECURRENCE_ID).isEmpty()) {
                replacements.add(component);
            } else {
                periods.addAll(component.calculateRecurrenceSet(period));
            }
        }

        List<Period<T>> finalPeriods = new ArrayList<>(periods);
        replacements.forEach(component -> {
            Optional<RecurrenceId<?>> recurrenceId = component.getProperty(Property.RECURRENCE_ID);
            List<Period<T>> match = finalPeriods.stream().filter(p -> p.getStart().equals(recurrenceId.get().getDate()))
                    .collect(Collectors.toList());
            finalPeriods.removeAll(match);

            finalPeriods.addAll(component.calculateRecurrenceSet(period));
        });

        // Natural sort of final list..
        Collections.sort(finalPeriods);
        return finalPeriods;
    }
}
