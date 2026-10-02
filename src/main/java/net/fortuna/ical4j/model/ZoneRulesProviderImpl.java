package net.fortuna.ical4j.model;

import net.fortuna.ical4j.util.Configurator;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesProvider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A custom implementation of {@link ZoneRulesProvider} that delegates to a {@link TimeZoneRegistry}.
 * This implementation maintains a pool of globally unique zone IDs that can be allocated to
 * {@link TimeZoneRegistry} instances to avoid conflicts with the standard Java zone rules.
 * @see TimeZoneRegistry
 * @see TimeZoneRegistryImpl
 * @author Ben Fortuna
 */
public class ZoneRulesProviderImpl extends ZoneRulesProvider {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ZoneRulesProviderImpl.class);

    /**
     * The installed provider, or {@code null} where the platform does not allow a custom
     * {@link ZoneRulesProvider} to be constructed or registered. Android, for example, blocks both
     * the {@code ZoneRulesProvider} constructor and {@link ZoneRulesProvider#registerProvider} as
     * hidden APIs.
     *
     * @deprecated this field is {@code null} when the provider is unavailable; use
     * {@link #getInstance()} or {@link #isAvailable()} instead.
     */
    @Deprecated
    public static final @Nullable ZoneRulesProviderImpl INSTANCE;
    static {
        ZoneRulesProviderImpl instance;
        try {
            instance = new ZoneRulesProviderImpl();
            ZoneRulesProvider.registerProvider(instance);
        } catch (LinkageError | SecurityException e) {
            // an unregistered provider is of no use, so treat a failed registration like a failed construction
            instance = null;
            LOG.info("Custom zone rules provider unavailable ({}: {}); VTIMEZONE definitions will resolve to"
                    + " platform time zones and their own rules will not be applied", e.getClass().getName(),
                    e.getMessage());
        }
        INSTANCE = instance;
    }

    /**
     * @return true if the custom provider was constructed and registered, so that zone IDs allocated from
     * {@link #getZoneIdPool()} resolve through {@link java.time.ZoneId#of(String)}
     */
    public static boolean isAvailable() {
        return INSTANCE != null;
    }

    /**
     * @return the installed provider, or an empty optional where the platform does not allow one
     */
    public static Optional<ZoneRulesProviderImpl> getInstance() {
        return Optional.ofNullable(INSTANCE);
    }

    private final Set<String> registeredZoneIds;

    private final Map<String, WeakReference<TimeZoneRegistry>> allocatedZoneIds;

    private final ZoneIdPool zoneIdPool;

    private final AtomicBoolean refresh = new AtomicBoolean(false);

    public ZoneRulesProviderImpl() {
        // zone ids are registered JVM-wide, so give each provider instance its own prefix: otherwise a second copy of
        // iCal4j (another webapp, or a redeploy while the old provider is still registered) can't register at all
        String token = UUID.randomUUID().toString().substring(0, 8);
        Set<String> globalZoneIds = new HashSet<>();
        for (int i = 0; i < Configurator.getIntProperty("net.fortuna.ical4j.timezone.id.pool.size").orElse(1500); i++) {
            globalZoneIds.add("ical4j-local-" + token + "-" + i);
        }
        this.registeredZoneIds = Collections.unmodifiableSet(globalZoneIds);
        this.allocatedZoneIds = new ConcurrentHashMap<>(512, 0.75f, 2);
        this.zoneIdPool = new ZoneIdPool();
    }

    public ZoneIdPool getZoneIdPool() {
        return zoneIdPool;
    }

    @Override
    protected Set<String> provideZoneIds() {
        return registeredZoneIds;
    }

    @Override
    protected @Nullable ZoneRules provideRules(String zoneId, boolean forCaching) {
        ZoneRules retVal = null;
        // don't allow caching of rules due to potential for dynamically loaded definitions..
        if (!forCaching) {
            WeakReference<TimeZoneRegistry> registryRef = allocatedZoneIds.get(zoneId);
            if (registryRef != null) {
                TimeZoneRegistry registry = Objects.requireNonNull(registryRef.get());
                retVal = registry.getZoneRules().get(zoneId);
            }
        }
        return retVal;
    }

    @Override
    protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
        NavigableMap<String, ZoneRules> retVal = new TreeMap<>();
        retVal.put(zoneId, provideRules(zoneId, false));
        return retVal;
    }

    @Override
    protected boolean provideRefresh() {
        return refresh.getAndSet(false);
    }

    public class ZoneIdPool {
        private final Queue<String> availableZoneIds;

        public ZoneIdPool() {
            this.availableZoneIds = new LinkedList<>(registeredZoneIds);
        }

        public synchronized String allocate(TimeZoneRegistry registry) {
            cleanup();
            String zoneId = availableZoneIds.poll();
            if (zoneId == null) {
                throw new RuntimeException("Ran out of available zone IDs");
            }

            allocatedZoneIds.put(zoneId, new WeakReference<>(registry));
            if (LOG.isTraceEnabled()) {
                LOG.trace("Allocated zone ID: {} ({} remaining)", zoneId, availableZoneIds());
            }
            refresh.set(true);
            return zoneId;
        }

        public synchronized void release(String zoneId) {
            WeakReference<TimeZoneRegistry> registry = allocatedZoneIds.remove(zoneId);
            if (registry != null) {
                availableZoneIds.offer(zoneId);
            }
        }

        public synchronized void cleanup() {
            for (Map.Entry<String, WeakReference<TimeZoneRegistry>> entry : allocatedZoneIds.entrySet()) {
                if (entry.getValue().get() == null) {
                    // registry has been GC'd, release associated zone id
                    release(entry.getKey());
                }
            }
        }

        public int availableZoneIds() {
            return availableZoneIds.size();
        }
    }
}
