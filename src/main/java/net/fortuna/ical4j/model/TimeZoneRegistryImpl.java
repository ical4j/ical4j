/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * <p>
 * o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p>
 * o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p>
 * o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p>
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

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.TzUrl;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * $Id$
 * <p/>
 * Created on 18/09/2005
 * <p/>
 * The default implementation of a <code>TimeZoneRegistry</code>. This implementation will search the classpath for
 * applicable VTimeZone definitions used to back the provided TimeZone instances.
 *
 * @author Ben Fortuna
 */
public class TimeZoneRegistryImpl implements TimeZoneRegistry {

  private static final String DEFAULT_RESOURCE_PREFIX = "zoneinfo/";

  private static final Pattern TZ_ID_SUFFIX = Pattern.compile("(?<=/)[^/]*/[^/]*$");

  private static final String UPDATE_ENABLED = "net.fortuna.ical4j.timezone.update.enabled";
  private static final String UPDATE_CONNECT_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.connect";
  private static final String UPDATE_READ_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.read";
  private static final String UPDATE_PROXY_ENABLED = "net.fortuna.ical4j.timezone.update.proxy.enabled";
  private static final String UPDATE_PROXY_TYPE = "net.fortuna.ical4j.timezone.update.proxy.type";
  private static final String UPDATE_PROXY_HOST = "net.fortuna.ical4j.timezone.update.proxy.host";
  private static final String UPDATE_PROXY_PORT = "net.fortuna.ical4j.timezone.update.proxy.port";

  private static Proxy proxy = null;

  private static final Map<String, TimeZone> DEFAULT_TIMEZONES = new ConcurrentHashMap<String, TimeZone>();

  private static final Properties ALIASES = new Properties();

  static {
    InputStream aliasInputStream = null;
    try {
      aliasInputStream = ResourceLoader.getResourceAsStream("net/fortuna/ical4j/model/tz.alias");
      ALIASES.load(aliasInputStream);
    } catch (IOException ioe) {
      LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn(
          "Error loading timezone aliases: " + ioe.getMessage());
    } finally {
      if (aliasInputStream != null) {
        try {
          aliasInputStream.close();
        } catch (IOException e) {
          LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn(
              "Error closing resource stream: " + e.getMessage());
        }
      }
    }

    try {
      aliasInputStream = ResourceLoader.getResourceAsStream("tz.alias");
      ALIASES.load(aliasInputStream);
    } catch (Exception e) {
      LoggerFactory.getLogger(TimeZoneRegistryImpl.class).debug(
          "Error loading custom timezone aliases: " + e.getMessage());
    } finally {
      if (aliasInputStream != null) {
        try {
          aliasInputStream.close();
        } catch (IOException e) {
          LoggerFactory.getLogger(TimeZoneRegistryImpl.class).warn(
              "Error closing resource stream: " + e.getMessage());
        }
      }
    }
    try {
      if ("true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED))) {
        final Proxy.Type type = Proxy.Type.valueOf(Configurator.getProperty(UPDATE_PROXY_TYPE));
        final String proxyHost = Configurator.getProperty(UPDATE_PROXY_HOST);
        final int proxyPort = Integer.parseInt(Configurator.getProperty(UPDATE_PROXY_PORT));
        proxy = new Proxy(type, new InetSocketAddress(proxyHost, proxyPort));
      }
    } catch (Throwable e) {
      LoggerFactory.getLogger(TimeZoneRegistryImpl.class).debug(
          "Error loading proxy server configuration: " + e.getMessage());
    }
  }

  private Map<String, TimeZone> timezones;

  private String resourcePrefix;

  /**
   * Default constructor.
   */
  public TimeZoneRegistryImpl() {
    this(DEFAULT_RESOURCE_PREFIX);
  }

  /**
   * Creates a new instance using the specified resource prefix.
   *
   * @param resourcePrefix a prefix prepended to classpath resource lookups for default timezones
   */
  public TimeZoneRegistryImpl(final String resourcePrefix) {
    this.resourcePrefix = resourcePrefix;
    timezones = new ConcurrentHashMap<String, TimeZone>();
  }

  /**
   * {@inheritDoc}
   */
  public final void register(final TimeZone timezone) {
    // for now we only apply updates to included definitions by default..
    register(timezone, false);
  }

  /**
   * {@inheritDoc}
   */
  public final void register(final TimeZone timezone, boolean update) {
    if (update) {
      // load any available updates for the timezone..
      timezones.put(timezone.getID(), new TimeZone(updateDefinition(timezone.getVTimeZone())));
    } else {
      timezones.put(timezone.getID(), timezone);
    }
  }

  /**
   * {@inheritDoc}
   */
  public final void clear() {
    timezones.clear();
  }

  /**
   * {@inheritDoc}
   */
  public final TimeZone getTimeZone(final String id) {
    if (id == null) {
      return null;
    }

    TimeZone timezone = timezones.get(id);
    if (timezone == null) {
      timezone = DEFAULT_TIMEZONES.get(id);
      if (timezone == null) {
        // if timezone not found with identifier, try loading an alias..
        final String alias = ALIASES.getProperty(id);
        if (alias != null) {
          return getTimeZone(alias);
        } else {
          synchronized (DEFAULT_TIMEZONES) {
            // check again as it may be loaded now..
            timezone = DEFAULT_TIMEZONES.get(id);
            if (timezone == null) {
              try {
                final VTimeZone vTimeZone = loadVTimeZone(id);
                if (vTimeZone != null) {
                  // XXX: temporary kludge..
                  // ((TzId) vTimeZone.getProperties().getProperty(Property.TZID)).setValue(id);
                  timezone = new TimeZone(vTimeZone);
                  DEFAULT_TIMEZONES.put(timezone.getID(), timezone);
                } else if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING)) {
                  // strip global part of id and match on default tz..
                  Matcher matcher = TZ_ID_SUFFIX.matcher(id);
                  if (matcher.find()) {
                    return getTimeZone(matcher.group());
                  }
                }
              } catch (Exception e) {
                Logger log = LoggerFactory.getLogger(TimeZoneRegistryImpl.class);
                log.warn("Error occurred loading VTimeZone", e);
              }
            }
          }
        }
      }
    }
    return timezone;
  }

  /**
   * Loads an existing VTimeZone from the classpath corresponding to the specified Java timezone.
   */
  private VTimeZone loadVTimeZone(final String id) throws IOException, ParserException {
    final URL resource = ResourceLoader.getResource(resourcePrefix + id + ".ics");
    if (resource != null) {
      final CalendarBuilder builder = new CalendarBuilder();
      final Calendar calendar = builder.build(resource.openStream());
      final VTimeZone vTimeZone = (VTimeZone) calendar.getComponent(Component.VTIMEZONE);
      // load any available updates for the timezone.. can be explicility disabled via configuration
      if (!"false".equals(Configurator.getProperty(UPDATE_ENABLED))) {
        return updateDefinition(vTimeZone);
      }
      return vTimeZone;
    }
    return null;
  }

  /**
   * @param vTimeZone
   * @return
   */
  private VTimeZone updateDefinition(VTimeZone vTimeZone) {
    final TzUrl tzUrl = vTimeZone.getTimeZoneUrl();
    if (tzUrl != null) {
      try {
        final String connectTimeoutProperty = Configurator.getProperty(UPDATE_CONNECT_TIMEOUT);
        final String readTimeoutProperty = Configurator.getProperty(UPDATE_READ_TIMEOUT);

        final int connectTimeout = connectTimeoutProperty != null ? Integer.parseInt(connectTimeoutProperty) : 0;
        final int readTimeout = readTimeoutProperty != null ? Integer.parseInt(readTimeoutProperty) : 0;

        URLConnection connection;
        URL url = tzUrl.getUri().toURL();

        if ("true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED)) && proxy != null) {
          connection = url.openConnection(proxy);
        } else {
          connection = url.openConnection();
        }

        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);

        final CalendarBuilder builder = new CalendarBuilder();

        final Calendar calendar = builder.build(connection.getInputStream());
        final VTimeZone updatedVTimeZone = (VTimeZone) calendar.getComponent(Component.VTIMEZONE);
        if (updatedVTimeZone != null) {
          return updatedVTimeZone;
        }
      } catch (Exception e) {
        Logger log = LoggerFactory.getLogger(TimeZoneRegistryImpl.class);
        log.warn("Unable to retrieve updates for timezone: " + vTimeZone.getTimeZoneId().getValue(), e);
      }
    }
    return vTimeZone;
  }
}
