package net.fortuna.ical4j.model;

import net.fortuna.ical4j.data.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesProvider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A default {@link ZoneRulesProvider} implementation for included timezone definitions. To avoid conflicting with
 * the standard Java zone rules this provider maintains an internal map of local zone ids to globally unique ids.
 *
 * NOTE: Globally unique zone identifiers are transient and will be regenerated for each instance of this class. They
 * are only used to support registration and use of alternative definitions in the scope of this library.
 */
public class DefaultZoneRulesProvider extends ZoneRulesProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultZoneRulesProvider.class);

    private static final String DEFAULT_RESOURCE_PREFIX = "zoneinfo/";

    private final TimeZoneLoader zoneLoader;

    private final Map<String, ZoneRules> zoneRulesMap;

    public DefaultZoneRulesProvider() {
        this(new TimeZoneLoader(DEFAULT_RESOURCE_PREFIX), TimeZoneRegistry.ZONE_IDS);
    }

    public DefaultZoneRulesProvider(TimeZoneLoader timeZoneLoader, Map<String, String> zoneIdMap) {
        this.zoneLoader = timeZoneLoader;
        for (var id : zoneLoader.getAvailableIDs()) {
            zoneIdMap.put("ical4j~" + UUID.randomUUID(), id);
        }
        this.zoneRulesMap = new ConcurrentHashMap<>();
    }

    @Override
    protected Set<String> provideZoneIds() {
        return TimeZoneRegistry.ZONE_IDS.keySet();
    }

    @Override
    protected ZoneRules provideRules(String zoneId, boolean forCaching) {
        ZoneRules retVal = null;
        if (zoneRulesMap.containsKey(zoneId)) {
            retVal = zoneRulesMap.get(zoneId);
        } else {
            try {
                var localZoneId = TimeZoneRegistry.ZONE_IDS.get(zoneId);
                var vTimeZone = zoneLoader.loadVTimeZone(localZoneId);
                retVal = new ZoneRulesBuilder().vTimeZone(vTimeZone).build();
                zoneRulesMap.put(zoneId, retVal);
            } catch (IOException | ParserException e) {
                LOG.error("Error loading zone rules", e);
            }
        }
        return retVal;
    }

    @Override
    protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
        NavigableMap<String, ZoneRules> retVal = new TreeMap<>();
        if (zoneRulesMap.containsKey(zoneId)) {
            retVal.put(zoneId, zoneRulesMap.get(zoneId));
        }
        return retVal;
    }

    @Override
    protected boolean provideRefresh() {
        return super.provideRefresh();
    }
}
