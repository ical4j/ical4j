package net.fortuna.ical4j.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ZoneIdPoolTest {

    @Test
    void poolRunsOutOfZoneIds() {
        var zoneRulesProvider = new ZoneRulesProviderImpl();
        var zonePoolId = zoneRulesProvider.getZoneIdPool();
        var timeZoneRegistry = new TimeZoneRegistryImpl();

        // Allocate all available zone IDs from ZoneIdPool
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        var zoneIds = new ArrayList<String>(zonePoolId.availableZoneIds());
        while (zonePoolId.availableZoneIds() > 0) {
            zoneIds.add(zonePoolId.allocate(timeZoneRegistry));
        }

        try {
            zonePoolId.allocate(timeZoneRegistry);
            fail("Expected exception");
        } catch (RuntimeException e) {
            assertEquals(RuntimeException.class, e.getClass());
            assertEquals("Ran out of available zone IDs", e.getMessage());
        }
    }
}
