package net.fortuna.ical4j.transform.property;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.transform.Rfc5545Rule;

/**
 * Incarnation of RFC5545 rule that applies to <code>Property</code> elements.
 * 
 * @author daniel grigore
 *
 * @param <T>
 *            subtype of {@link Property} class
 */
public interface Rfc5545PropertyRule<T extends Property> extends Rfc5545Rule<T> {

}
