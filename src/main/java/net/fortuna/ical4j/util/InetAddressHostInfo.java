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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author fortuna
 *
 */
public class InetAddressHostInfo implements HostInfo {

    private final InetAddress hostAddress;
    
    /**
     * @throws SocketException where an error occurs identifying the host address
     */
    public InetAddressHostInfo() throws SocketException {
        this(findNonLoopbackAddress());
    }
    
    /**
     * @param address a host address
     */
    public InetAddressHostInfo(InetAddress address) {
        this.hostAddress = address;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getHostName() {
        return hostAddress.getHostName();
    }

    /**
     * Find a non loopback address for this machine on which to start the server.
     * @return a non loopback address
     * @throws SocketException if a socket error occurs
     */
    private static InetAddress findNonLoopbackAddress() throws SocketException {
        final Enumeration<NetworkInterface> enumInterfaceAddress = NetworkInterface.getNetworkInterfaces();
        while (enumInterfaceAddress.hasMoreElements()) {
            final NetworkInterface netIf = enumInterfaceAddress.nextElement();

            // Iterate over inet address
            final Enumeration<InetAddress> enumInetAdress = netIf.getInetAddresses();
            while (enumInetAdress.hasMoreElements()) {
                final InetAddress address = enumInetAdress.nextElement();
                if (!address.isLoopbackAddress()) {
                    return address;
                }
            }
        }
        return null;
    }

}
