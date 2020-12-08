/**
 * Copyright (c) 2012, Ben Fortuna
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

import java.time.ZoneId;
import java.time.zone.ZoneRules;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * $Id$
 *
 * Created on 18/09/2005
 *
 * Implementors provide a list of timezone definitions applicable for use
 * with iCalendar objects.
 * @author Ben Fortuna
 */
public interface TimeZoneRegistry {

    Map<String, String> ZONE_IDS = new ConcurrentHashMap<>();

    Map<String, String> ZONE_ALIASES = new ConcurrentHashMap<>();

    static ZoneId getGlobalZoneId(String tzId) {
        Objects.requireNonNull(tzId, "tzId");
        // Ensure zone rules are loaded..
        Set<String> ids = ZoneId.getAvailableZoneIds();
        try {
            Class.forName(TimeZoneRegistryImpl.class.getCanonicalName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Optional<Map.Entry<String, String>> zoneId = ZONE_IDS.entrySet().stream().filter(entry ->
                entry.getValue().equals(tzId)).findFirst();
        if (zoneId.isPresent()) {
            return ZoneId.of(zoneId.get().getKey());
        } else {
            return ZoneId.of(tzId, ZONE_ALIASES);
        }
    }

    /**
     * Registers a new timezone for use with iCalendar objects. If a timezone
     * with the same identifier is already registered this timezone will take
     * precedence.
     * @param timezone a timezone to be registered for use with iCalendar
     * objects
     */
    void register(final TimeZone timezone);
    
    /**
     * Registers a new timezone for use with iCalendar objects. If a timezone
     * with the same identifier is already registered this timezone will take
     * precedence.
     * @param timezone a timezone to be registered for use with iCalendar
     * objects
     * @param update attempt to update the definition from any specified TZURL
     * property if true
     */
    void register(final TimeZone timezone, boolean update);
    
    /**
     * Clears all registered timezones.
     */
    void clear();
    
    /**
     * Returns a timezone with the specified identifier.
     * @param id a timezone identifier
     * @return a timezone matching the specified identifier. If no timezone
     * is registered with the specified identifier null is returned.
     */
    TimeZone getTimeZone(final String id);

    Map<String, ZoneRules> getZoneRules();

    ZoneId getZoneId(String tzId);

    String getTzId(String zoneId);
}
