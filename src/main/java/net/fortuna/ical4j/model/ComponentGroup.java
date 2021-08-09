package net.fortuna.ical4j.model;

import net.fortuna.ical4j.filter.predicate.PropertyEqualToRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Uid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private final Predicate<T> componentPredicate;

    public ComponentGroup(ComponentList<T> components, Uid uid) {
        this(components, uid, null);
    }

    public ComponentGroup(ComponentList<T> components, Uid uid, RecurrenceId recurrenceId) {
        this.components = components;

        if (recurrenceId != null) {
            componentPredicate = new PropertyEqualToRule<T>(uid).and(new PropertyEqualToRule<>(recurrenceId));
        } else {
            componentPredicate = new PropertyEqualToRule<>(uid);
        }
    }

    /**
     * Apply filter to all components to create a subset containing components
     * matching the specified UID.
     *
     * @return
     */
    public ComponentList<T> getRevisions() {
        return new ComponentList<T>(components.stream().filter(componentPredicate).collect(Collectors.toList()));
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
        List<Component> replacements = new ArrayList<>();

        for (Component component : getRevisions()) {
            if (!component.getProperties(Property.RECURRENCE_ID).isEmpty()) {
                replacements.add(component);
            } else {
                periods = periods.add(component.calculateRecurrenceSet(period));
            }
        }

        PeriodList finalPeriods = periods;
        replacements.forEach(component -> {
            RecurrenceId recurrenceId = component.getProperty(Property.RECURRENCE_ID);
            finalPeriods.removeIf(p -> p.getStart().equals(recurrenceId.getDate()));
            component.calculateRecurrenceSet(period).stream()
                    .filter(p -> p.getStart().equals(recurrenceId.getDate()))
                    .forEach(finalPeriods::add);
        });

        return periods;
    }
}
