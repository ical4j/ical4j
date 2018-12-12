package net.fortuna.ical4j.model;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.HasPropertyRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;

import java.util.Collections;
import java.util.function.Predicate;

/**
 * Support for operations applicable to a group of components. Typically this class is used to manage
 * component revisions (whereby each revision is a separate component), and the resulting ouput of
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
public class ComponentGroup<T extends Component> {

    private final ComponentList<T> components;

    private final Filter<T> componentFilter;

    public ComponentGroup(ComponentList<T> components, Uid uid) {
        this(components, uid, null);
    }

    public ComponentGroup(ComponentList<T> components, Uid uid, RecurrenceId recurrenceId) {
        this.components = components;

        Predicate<T> componentPredicate;
        if (recurrenceId != null) {
            componentPredicate = new HasPropertyRule<T>(uid).and(new HasPropertyRule<T>(recurrenceId));
        } else {
            componentPredicate = new HasPropertyRule<T>(uid);
        }
        componentFilter = new Filter<>(componentPredicate);
    }

    /**
     * Apply filter to all components to create a subset containing components
     * matching the specified UID.
     *
     * @return
     */
    public ComponentList<T> getRevisions() {
        return (ComponentList<T>) componentFilter.filter(components);
    }

    /**
     * Returns the latest component revision based on ascending sequence number and modified date.
     *
     * @return
     */
    public T getLatestRevision() {
        ComponentList<T> revisions = getRevisions();
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
    public PeriodList calculateRecurrenceSet(final Period period) {
        PeriodList periods = new PeriodList();

        for (Component component : getRevisions()) {
            periods = periods.add(component.calculateRecurrenceSet(period));
        }

        return periods;
    }
}
