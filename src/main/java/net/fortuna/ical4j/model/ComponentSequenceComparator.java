package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.Sequence;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;

/**
 * A comparator to determine natural ordering of component instances based on
 * sequence information.
 *
 * See <a href="https://tools.ietf.org/html/rfc5546#section-2.1.5">RFC5446 - Message Sequencing</a>
 * for further details.
 */
public class ComponentSequenceComparator implements Comparator<Component> {

    @Override
    public int compare(Component o1, Component o2) {
        int retVal = 0;

        Sequence defaultSequence = new Sequence(0);
        Sequence sequence1 = ObjectUtils.defaultIfNull((Sequence) o1.getProperty(Property.SEQUENCE), defaultSequence);
        Sequence sequence2 = ObjectUtils.defaultIfNull((Sequence) o2.getProperty(Property.SEQUENCE), defaultSequence);

        retVal = sequence1.compareTo(sequence2);
        if (retVal == 0) {
            DtStamp defaultDtStamp = new DtStamp(new DateTime(0));
            DtStamp dtStamp1 = ObjectUtils.defaultIfNull((DtStamp) o1.getProperty(Property.DTSTAMP), defaultDtStamp);
            DtStamp dtStamp2 = ObjectUtils.defaultIfNull((DtStamp) o2.getProperty(Property.DTSTAMP), defaultDtStamp);

            retVal = dtStamp1.compareTo(dtStamp2);
        }
        return retVal;
    }
}
