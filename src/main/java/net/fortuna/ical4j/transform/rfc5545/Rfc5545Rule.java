package net.fortuna.ical4j.transform.rfc5545;

/**
 * Incarnation of a RFC5545 rule.
 * 
 * @author daniel grigore
 * @author corneliu dobrota
 *
 * @param <T>
 *            type of the element this rule can be applied to
 */
public interface Rfc5545Rule<T> {

    /**
     * Applies this rule to the specified element.
     * 
     * @param element
     */
    void applyTo(T element);

    /**
     * Gets the class of the elements this rule can be applied to.
     * 
     * @return the class of the elements this rule can be applied to
     */
    Class<T> getSupportedType();
}
