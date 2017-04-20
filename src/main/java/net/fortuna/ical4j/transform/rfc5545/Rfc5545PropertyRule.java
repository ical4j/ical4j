package net.fortuna.ical4j.transform.rfc5545;

import net.fortuna.ical4j.model.Property;

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
