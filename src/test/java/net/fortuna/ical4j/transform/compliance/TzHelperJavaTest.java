package net.fortuna.ical4j.transform.compliance;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.RDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class TzHelperJavaTest {
    @Test
    void correctTzParameterFrom_without_TZID_parameter() {
        Property property = new RDate<>(new DateList<>(LocalDateTime.now()));

        TzHelper.correctTzParameterFrom(property);

        assertTrue(property.getParameter(Parameter.TZID).isEmpty());
    }

    @Test
    void correctTzParameterFrom_with_known_TZID_parameter() {
        Property property = new RDate<>(new ParameterList(singletonList(new TzId("Europe/Berlin"))), new DateList<>());

        TzHelper.correctTzParameterFrom(property);

        assertEquals("Europe/Berlin", property.getRequiredParameter(Parameter.TZID).getValue());
    }

    @Test
    void correctTzParameterFrom_with_known_TZID_parameter_that_is_mapped() {
        Property property = new RDate<>(
                new ParameterList(singletonList(new TzId("Mitteleuropäische Zeit"))),
                new DateList<>());

        TzHelper.correctTzParameterFrom(property);

        assertEquals("Europe/Vienna", property.getRequiredParameter(Parameter.TZID).getValue());
    }

    @Test
    void correctTzParameterFrom_with_unknown_TZID_parameter() {
        Property property = new RDate<>(new ParameterList(singletonList(new TzId("unknown"))), new DateList<>());

        TzHelper.correctTzParameterFrom(property);

        assertTrue(property.getParameter(Parameter.TZID).isEmpty());
    }

    @Test
    void correctTzParameterFrom_with_known_TZID_parameter_using_double_quotes() {
        Property property = new RDate<>(
                new ParameterList(singletonList(new TzId("\"Europe/Paris\""))),
                new DateList<>());

        TzHelper.correctTzParameterFrom(property);

        assertEquals("Europe/Paris", property.getRequiredParameter(Parameter.TZID).getValue());
    }
}
