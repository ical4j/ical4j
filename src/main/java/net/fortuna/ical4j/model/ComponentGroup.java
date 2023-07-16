package net.fortuna.ical4j.model;

import net.fortuna.ical4j.filter.predicate.PropertyEqualToRule;
import net.fortuna.ical4j.filter.predicate.PropertyExistsRule;
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
        this.componentPredicate = new PropertyEqualToRule<C>(uid)
                .and(new PropertyExistsRule<>(new RecurrenceId<>()).negate());
        this.componentList = new ComponentList<>(components);
    }

    public ComponentGroup(List<C> components, Uid uid, RecurrenceId<?> recurrenceId) {
        this.componentPredicate = new PropertyEqualToRule<C>(uid).and(new PropertyEqualToRule<>(recurrenceId));
        this.componentList = new ComponentList<>(components);
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
    public ComponentContainer<C> add(C component) {
        if (!componentPredicate.test(component)) {
            throw new IllegalArgumentException("Incompatible component");
        }
        return ComponentContainer.super.add(component);
    }

    @Override
    public ComponentContainer<C> replace(C component) {
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
            if (component.getProperty(Property.RECURRENCE_ID).isPresent()) {
                overrides.add(component);
            } else {
                periods.addAll(component.calculateRecurrenceSet(period));
            }
        }

        List<Period<T>> finalPeriods = new ArrayList<>(periods);
        overrides.forEach(component -> {
            RecurrenceId<?> recurrenceId = component.getRequiredProperty(Property.RECURRENCE_ID);
            finalPeriods.removeIf(p -> p.getStart().equals(recurrenceId.getDate()));
            component.calculateRecurrenceSet(period).stream()
                    .filter(p -> p.getStart().equals(recurrenceId.getDate()))
                    .forEach(finalPeriods::add);
        });

        // Natural sort of final list..
        Collections.sort(finalPeriods);
        return finalPeriods;
    }
}
