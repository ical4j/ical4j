package net.fortuna.ical4j.model;

import java.lang.ref.WeakReference;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesProvider;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class ZoneRulesProviderImpl extends ZoneRulesProvider {

    // A weak reference should be sufficient as only properties
    // with a registry reference will use this custom provider..
    private final WeakReference<TimeZoneRegistry> timeZoneRegistry;

    public ZoneRulesProviderImpl(TimeZoneRegistry timeZoneRegistry) {
        Objects.requireNonNull(timeZoneRegistry, "timeZoneRegistry");
        this.timeZoneRegistry = new WeakReference<>(timeZoneRegistry);
    }

    @Override
    protected Set<String> provideZoneIds() {
        return Objects.requireNonNull(timeZoneRegistry.get()).getZoneRules().keySet();
    }

    @Override
    protected ZoneRules provideRules(String zoneId, boolean forCaching) {
        ZoneRules retVal = null;
        // don't allow caching of rules due to potential for dynamically loaded definitions..
        if (!forCaching) {
            if (Objects.requireNonNull(timeZoneRegistry.get()).getZoneRules().containsKey(zoneId)) {
                retVal = Objects.requireNonNull(timeZoneRegistry.get()).getZoneRules().get(zoneId);
            }
        }
        return retVal;
    }

    @Override
    protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
        NavigableMap<String, ZoneRules> retVal = new TreeMap<>();
        if (Objects.requireNonNull(timeZoneRegistry.get()).getZoneRules().containsKey(zoneId)) {
            retVal.put(zoneId, Objects.requireNonNull(timeZoneRegistry.get()).getZoneRules().get(zoneId));
        }
        return retVal;
    }

    @Override
    protected boolean provideRefresh() {
        return super.provideRefresh();
    }
}
