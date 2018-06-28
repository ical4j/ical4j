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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * $Id$
 * <p/>
 * Created on 06/02/2008
 * <p/>
 * Provides configuration properties specified either as system properties
 * or in an ical4j.properties configuration file.
 *
 * @author Ben
 */
public final class Configurator {

    private static final Logger LOG = LoggerFactory.getLogger(Configurator.class);

    private static final Properties CONFIG = new Properties();

    static {
        try {
            CONFIG.load(ResourceLoader.getResourceAsStream("ical4j.properties"));
        } catch (IOException | NullPointerException e) {
            LOG.info("ical4j.properties not found.");
        }
    }

    /**
     * Constructor made private to enforce static nature.
     */
    private Configurator() {
    }

    /**
     * @param key a compatibility hint key
     * @return true if the specified compatibility hint is enabled, otherwise false
     */
    public static Optional<String> getProperty(final String key) {
        String property = CONFIG.getProperty(key);
        if (property == null) {
            property = System.getProperty(key);
        }
        return Optional.ofNullable(property);
    }

    public static Optional<Integer> getIntProperty(final String key) {
        Optional<String> property = getProperty(key);
        if (property.isPresent()) {
            try {
                int intValue = Integer.parseInt(property.get());
                return Optional.of(intValue);
            } catch (NumberFormatException nfe) {
                LOG.info(String.format("Invalid configuration value: %s", key), nfe);
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public static <T extends Enum<T>> Optional<T> getEnumProperty(Class<T> clazz, String key) {
        Optional<String> property = getProperty(key);
        if (property.isPresent()) {
            try {
                return Optional.of(Enum.valueOf(clazz, property.get()));
            } catch (IllegalArgumentException iae) {
                LOG.info(String.format("Invalid configuration value: %s", key), iae);
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> getObjectProperty(String key) {
        Optional<String> property = getProperty(key);
        if (property.isPresent()) {
            try {
                return Optional.of((T) Class.forName(property.get()).newInstance());
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                LOG.info(String.format("Invalid configuration value: %s", key), e);
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
