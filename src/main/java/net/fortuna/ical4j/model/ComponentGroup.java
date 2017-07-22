package net.fortuna.ical4j.model;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.HasPropertyRule;
import net.fortuna.ical4j.model.property.Uid;

/**
 * Support for operations applicable to a group of components. Typically this class is used to manage
 * component revisions (whereby each revision is a separate component), and the resulting ouput of
 * such group functins.
 *
 * Created by fortuna on 20/07/2017.
 */
public class ComponentGroup<T extends Component> {

    private final ComponentList<T> components;

    private final Uid uid;

    private final Filter<T> uidFilter;

    public ComponentGroup(ComponentList<T> components, Uid uid) {
        this.components = components;
        this.uid = uid;
        uidFilter = new Filter<>(new HasPropertyRule<T>(uid));
    }

    /**
     * Apply filter to all components to create a subset containing components
     * matching the specified UID.
     *
     * @return
     */
    public ComponentList<T> getRevisions() {
        return (ComponentList<T>) uidFilter.filter(components);
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
            periods.add(component.calculateRecurrenceSet(period));
        }

        return periods;
    }
}
