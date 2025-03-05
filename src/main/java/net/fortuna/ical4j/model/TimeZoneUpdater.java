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
import java.net.*;
import java.util.Optional;

import static net.fortuna.ical4j.model.Property.TZURL;

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

    private static final String UPDATE_SCHEME_OVERRIDE = "net.fortuna.ical4j.timezone.update.scheme";
    private static final String UPDATE_HOST_OVERRIDE = "net.fortuna.ical4j.timezone.update.host";
    private static final String UPDATE_PORT_OVERRIDE = "net.fortuna.ical4j.timezone.update.port";

    private static final String SECURE_CONNECTION_ENABLED = "net.fortuna.ical4j.timezone.update.connection.secure";

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
        final int connectTimeout = Configurator.getIntProperty(UPDATE_CONNECT_TIMEOUT).orElse(10_000);
        final int readTimeout = Configurator.getIntProperty(UPDATE_READ_TIMEOUT).orElse(10_000);

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
            final Optional<TzUrl> tzUrl = vTimeZone.getProperty(TZURL);
            if (tzUrl.isPresent()) {
                try {
                    boolean secureScheme = "true".equals(Configurator.getProperty(SECURE_CONNECTION_ENABLED).orElse("false"));
                    var updateUrl = new UrlBuilder(tzUrl.get().getUri())
                            .withScheme(Configurator.getProperty(UPDATE_SCHEME_OVERRIDE).orElse(secureScheme ? "https" : null))
                            .withHost(Configurator.getProperty(UPDATE_HOST_OVERRIDE).orElse(null))
                            .withPort(Configurator.getIntProperty(UPDATE_PORT_OVERRIDE).orElse(-1)).toUrl();
                    var connection = openConnection(updateUrl);

                    final var builder = new CalendarBuilder();
                    final var calendar = builder.build(connection.getInputStream());
                    final Optional<VTimeZone> updatedVTimeZone = calendar.getComponent(Component.VTIMEZONE);
                    if (updatedVTimeZone.isPresent()) {
                        return updatedVTimeZone.get();
                    }
                } catch (IOException | ParserException | URISyntaxException e) {
                    LoggerFactory.getLogger(TimeZoneUpdater.class).warn("Error updating timezone definition", e);
                }
            }
        }
        return vTimeZone;
    }

    private static class UrlBuilder {

        private final URI base;

        private String scheme;

        private String host;

        private int port;

        public UrlBuilder(URI base) {
            this.base = base;
        }

        public UrlBuilder withScheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public UrlBuilder withHost(String host) {
            this.host = host;
            return this;
        }

        public UrlBuilder withPort(int port) {
            this.port = port;
            return this;
        }

        URL toUrl() throws MalformedURLException, URISyntaxException {
            var uri = base;
            if (scheme != null) {
                uri = new URI(scheme, uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(),
                        uri.getFragment());
            }
            if (host != null) {
                uri = new URI(uri.getScheme(), uri.getUserInfo(), host, uri.getPort(), uri.getPath(), uri.getQuery(),
                        uri.getFragment());
            }
            if (port > 0) {
                uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), port, uri.getPath(), uri.getQuery(),
                        uri.getFragment());
            }

            return uri.toURL();
        }
    }
}
