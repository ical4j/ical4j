/**
 * Copyright (c) 2008, Ben Fortuna
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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.Uid;

/**
 * $Id$
 *
 * Created on 11/03/2007
 *
 * Generates {@link Uid} properties in a similar fashion to that recommended in section 4.8.4.7 of the specification.
 * @author Ben Fortuna
 */
public class UidGenerator {

    private String pid;

    private InetAddress hostAddress;

    private static long lastMillis;

    /**
     * Default constructor.
     */
    public UidGenerator(String pid) throws SocketException {
        this(findNonLoopbackAddress(), pid);
    }

    /**
     * @param hostAddress
     */
    public UidGenerator(InetAddress hostAddress, String pid) {
        this.hostAddress = hostAddress;
        this.pid = pid;
    }

    /**
     * @return
     */
    public Uid generateUid() {
        final StringBuffer b = new StringBuffer();
        b.append(uniqueTimestamp());
        b.append('-');
        b.append(pid);
        if (hostAddress != null) {
            b.append('@');
            b.append(hostAddress.getHostName());
        }
        return new Uid(b.toString());
    }

    /**
     * Generates a timestamp guaranteed to be unique for the current JVM instance.
     * @return a {@link DateTime} instance representing a unique timestamp
     */
    private static DateTime uniqueTimestamp() {
        long currentMillis;
        synchronized (UidGenerator.class) {
            currentMillis = System.currentTimeMillis();
            // guarantee uniqueness by ensuring timestamp is always greater
            // than the previous..
            if (currentMillis < lastMillis) {
                currentMillis = lastMillis;
            }
            if (currentMillis - lastMillis < Dates.MILLIS_PER_SECOND) {
                currentMillis += Dates.MILLIS_PER_SECOND;
            }
            lastMillis = currentMillis;
        }
        final DateTime timestamp = new DateTime(currentMillis);
        timestamp.setUtc(true);
        return timestamp;
    }

    /**
     * Find a non loopback address for this machine on which to start the server.
     * @return a non loopback address
     * @throws SocketException if a socket error occurs
     */
    private static InetAddress findNonLoopbackAddress() throws SocketException {
        final Enumeration enumInterfaceAddress = NetworkInterface.getNetworkInterfaces();
        while (enumInterfaceAddress.hasMoreElements()) {
            final NetworkInterface netIf = (NetworkInterface) enumInterfaceAddress.nextElement();

            // Iterate over inet address
            final Enumeration enumInetAdress = netIf.getInetAddresses();
            while (enumInetAdress.hasMoreElements()) {
                final InetAddress address = (InetAddress) enumInetAdress.nextElement();
                if (!address.isLoopbackAddress()) {
                    return address;
                }
            }
        }
        return null;
    }
}
