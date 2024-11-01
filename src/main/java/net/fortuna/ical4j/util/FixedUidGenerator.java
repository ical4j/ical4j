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
package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.TemporalAdapter;
import net.fortuna.ical4j.model.property.Uid;

import java.net.SocketException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * $Id$
 *
 * Created on 11/03/2007
 *
 * Generates {@link Uid} properties in a similar fashion to that recommended in section 4.8.4.7 of the specification.
 * @author Ben Fortuna
 */
public class FixedUidGenerator implements UidGenerator {

    private final String pid;

    private final String hostName;

    private static long lastMillis;

    /**
     * @param pid a unique process identifier for the host machine
     * @throws SocketException where host information cannot be retrieved
     */
    public FixedUidGenerator(String pid) throws SocketException {
        this(new InetAddressHostInfo(), pid);
    }

    /**
     * @param hostInfo custom host information
     * @param pid a unique process identifier for the host machine
     */
    public FixedUidGenerator(HostInfo hostInfo, String pid) {
        this.hostName = hostInfo == null ? null : hostInfo.getHostName();
        this.pid = pid;
    }

    @Override
    public Uid generateUid() {
        final var b = new StringBuilder();
        b.append(uniqueTimestamp());
        b.append('-');
        b.append(pid);
        if (hostName != null) {
            b.append('@');
            b.append(hostName);
        }
        return new Uid(b.toString());
    }

    /**
     * Generates a timestamp guaranteed to be unique for the current JVM instance.
     * @return a {@link TemporalAdapter<Instant>} instance representing a unique timestamp
     */
    private static TemporalAdapter<Instant> uniqueTimestamp() {
        long currentMillis;
        synchronized (FixedUidGenerator.class) {
            currentMillis = System.currentTimeMillis();
            // guarantee uniqueness by ensuring timestamp is always greater
            // than the previous..
            if (currentMillis < lastMillis) {
                currentMillis = lastMillis;
            }
            if (currentMillis - lastMillis < TimeUnit.SECONDS.toMillis(1)) {
                currentMillis += TimeUnit.SECONDS.toMillis(1);
            }
            lastMillis = currentMillis;
        }
        return new TemporalAdapter<>(Instant.ofEpochMilli(currentMillis));
    }
}
