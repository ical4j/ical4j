package net.fortuna.ical4j.transform.component;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.transform.Rfc5545Rule;

/**
 * Incarnation of RFC5545 rule that applies to <code>Component</code> elements.
 * 
 * @author daniel grigore
 *
 * @param <T>
 *            subtype of {@link Component} class
 */
public interface Rfc5545ComponentRule<T extends Component> extends Rfc5545Rule<T> {

}
