package net.fortuna.ical4j.model;

import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesProvider;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class ZoneRulesProviderImpl extends ZoneRulesProvider {

    private final TimeZoneRegistry timeZoneRegistry;

    public ZoneRulesProviderImpl(TimeZoneRegistry timeZoneRegistry) {
        Objects.requireNonNull(timeZoneRegistry, "timeZoneRegistry");
        this.timeZoneRegistry = timeZoneRegistry;
    }

    @Override
    protected Set<String> provideZoneIds() {
        return timeZoneRegistry.getZoneRules().keySet();
    }

    @Override
    protected ZoneRules provideRules(String zoneId, boolean forCaching) {
        ZoneRules retVal = null;
        // don't allow caching of rules due to potential for dynamically loaded definitions..
        if (timeZoneRegistry.getZoneRules().containsKey(zoneId)) {
            retVal = timeZoneRegistry.getZoneRules().get(zoneId);
        }
        return retVal;
    }

    @Override
    protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
        NavigableMap<String, ZoneRules> retVal = new TreeMap<>();
        if (timeZoneRegistry.getZoneRules().containsKey(zoneId)) {
            retVal.put(zoneId, timeZoneRegistry.getZoneRules().get(zoneId));
        }
        return retVal;
    }

    @Override
    protected boolean provideRefresh() {
        return super.provideRefresh();
    }
}
