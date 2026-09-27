/**
 * Copyright (c) 2026, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.zone.ZoneRulesProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Registers iCal4j's bundled timezone definitions as a {@link ZoneRulesProvider}, once, the first time global zone
 * resolution needs them ({@link TimeZoneRegistry#getGlobalZoneId(String)}).
 * <p>
 * The provider used to be registered through {@code META-INF/services}, which made the JDK load it while
 * {@code java.time} initialised: merely having iCal4j on the classpath added its zone ids to every JVM, and a JVM
 * started with a custom Java Flight Recorder repository failed to start at all (JDK-8359441, issue #808). Lazy
 * programmatic registration avoids both, and behaves the same whichever class loader loaded iCal4j.
 * <p>
 * Like {@link ZoneRulesProviderImpl}, a provider registered from a non-system class loader (e.g. a webapp) keeps
 * that class loader reachable from the JDK's provider registry until the JVM exits.
 */
final class GlobalZoneRules {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalZoneRules.class);

    private static final boolean REGISTERED = register(
            ids -> new DefaultZoneRulesProvider(new TimeZoneLoader(DefaultZoneRulesProvider.DEFAULT_RESOURCE_PREFIX),
                    ids), TimeZoneRegistry.ZONE_IDS);

    private GlobalZoneRules() {
    }

    /**
     * @return true if the bundled definitions were registered; false where the platform does not allow a custom
     * {@link ZoneRulesProvider}, in which case global resolution uses platform zones
     */
    static boolean isRegistered() {
        return REGISTERED;
    }

    /**
     * Constructs a provider over a private id map, registers it, and only then publishes the ids, so that a failed
     * registration never leaves ids behind that no provider can resolve.
     *
     * @param factory   creates the provider, writing its {@code ical4j~} ids into the map it is given
     * @param publishTo where the ids are published once registration succeeds
     * @return true if the provider was registered
     */
    static boolean register(Function<Map<String, String>, ? extends ZoneRulesProvider> factory,
                            Map<String, String> publishTo) {
        Map<String, String> privateIds = new ConcurrentHashMap<>();
        try {
            ZoneRulesProvider.registerProvider(factory.apply(privateIds));
        } catch (LinkageError | SecurityException e) {
            LOG.info("Bundled zone rules provider unavailable ({}: {}); global TZID resolution will use platform"
                    + " time zones", e.getClass().getName(), e.getMessage());
            return false;
        }
        publishTo.putAll(privateIds);
        return true;
    }
}
