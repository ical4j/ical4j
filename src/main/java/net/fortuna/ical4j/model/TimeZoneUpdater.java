/*
 *  Copyright (c) 2021, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.model;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.TzUrl;
import net.fortuna.ical4j.util.Configurator;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * Support for updating timezone definitions.
 */
public class TimeZoneUpdater {

    private static final String UPDATE_ENABLED = "net.fortuna.ical4j.timezone.update.enabled";
    private static final String UPDATE_CONNECT_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.connect";
    private static final String UPDATE_READ_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.read";
    private static final String UPDATE_PROXY_ENABLED = "net.fortuna.ical4j.timezone.update.proxy.enabled";
    private static final String UPDATE_PROXY_TYPE = "net.fortuna.ical4j.timezone.update.proxy.type";
    private static final String UPDATE_PROXY_HOST = "net.fortuna.ical4j.timezone.update.proxy.host";
    private static final String UPDATE_PROXY_PORT = "net.fortuna.ical4j.timezone.update.proxy.port";

    private Proxy proxy = null;

    public TimeZoneUpdater() {
        // Proxy configuration..
        try {
            if ("true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED).orElse("false"))) {
                final Proxy.Type type = Configurator.getEnumProperty(Proxy.Type.class, UPDATE_PROXY_TYPE).orElse(Proxy.Type.DIRECT);
                final String proxyHost = Configurator.getProperty(UPDATE_PROXY_HOST).orElse("");
                final int proxyPort = Configurator.getIntProperty(UPDATE_PROXY_PORT).orElse(-1);
                proxy = new Proxy(type, new InetSocketAddress(proxyHost, proxyPort));
            }
        } catch (Throwable e) {
            LoggerFactory.getLogger(TimeZoneUpdater.class).warn(
                    "Error loading proxy server configuration: " + e.getMessage());
        }
    }

    public boolean isEnabled() {
        return !"false".equals(Configurator.getProperty(UPDATE_ENABLED).orElse("true"));
    }

    public URLConnection openConnection(URL url) throws IOException {
        final int connectTimeout = Configurator.getIntProperty(UPDATE_CONNECT_TIMEOUT).orElse(0);
        final int readTimeout = Configurator.getIntProperty(UPDATE_READ_TIMEOUT).orElse(0);

        URLConnection connection;
        if ("true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED).orElse("false")) && proxy != null) {
            connection = url.openConnection(proxy);
        } else {
            connection = url.openConnection();
        }

        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        return connection;
    }

    /**
     * @param vTimeZone
     * @return
     */
    public VTimeZone updateDefinition(VTimeZone vTimeZone) {
        if (isEnabled() && vTimeZone != null) {
            final TzUrl tzUrl = vTimeZone.getTimeZoneUrl();
            if (tzUrl != null) {
                try {
                    URLConnection connection = openConnection(tzUrl.getUri().toURL());

                    final CalendarBuilder builder = new CalendarBuilder();
                    final Calendar calendar = builder.build(connection.getInputStream());
                    final VTimeZone updatedVTimeZone = calendar.getComponent(Component.VTIMEZONE);
                    if (updatedVTimeZone != null) {
                        return updatedVTimeZone;
                    }
                } catch (IOException | ParserException e) {
                    LoggerFactory.getLogger(TimeZoneLoader.class).warn("Error updating timezone definition", e);
                }
            }
        }
        return vTimeZone;
    }
}
