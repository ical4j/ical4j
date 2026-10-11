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
package net.fortuna.ical4j.validate;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link AbstractCalendarValidatorFactory} must not fail class initialisation when the
 * {@code META-INF/services} registration is absent, as happens when ProGuard/R8 or some
 * packaging tools strip service files (#125, #29, #115).
 */
public class AbstractCalendarValidatorFactoryTest {

    private static final String SERVICE_RESOURCE = "META-INF/services/" + CalendarValidatorFactory.class.getName();

    private static final Set<String> ISOLATED_CLASSES = Set.of(
            AbstractCalendarValidatorFactory.class.getName(),
            DefaultCalendarValidatorFactory.class.getName());

    /**
     * Re-defines the factory classes so their static initialiser runs again, and hides the
     * service registration from them.
     */
    private static class ServiceStrippingClassLoader extends ClassLoader {

        ServiceStrippingClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                if (ISOLATED_CLASSES.contains(name)) {
                    Class<?> loaded = findLoadedClass(name);
                    if (loaded == null) {
                        loaded = defineFromParent(name);
                    }
                    if (resolve) {
                        resolveClass(loaded);
                    }
                    return loaded;
                }
                return super.loadClass(name, resolve);
            }
        }

        private Class<?> defineFromParent(String name) throws ClassNotFoundException {
            String path = name.replace('.', '/') + ".class";
            try (InputStream in = getParent().getResourceAsStream(path)) {
                if (in == null) {
                    throw new ClassNotFoundException(name);
                }
                byte[] bytes = in.readAllBytes();
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            if (SERVICE_RESOURCE.equals(name)) {
                return Collections.emptyEnumeration();
            }
            return super.getResources(name);
        }

        @Override
        public URL getResource(String name) {
            if (SERVICE_RESOURCE.equals(name)) {
                return null;
            }
            return super.getResource(name);
        }
    }

    @Test
    public void testDefaultFactoryWithoutServiceRegistration() throws Exception {
        ClassLoader loader = new ServiceStrippingClassLoader(getClass().getClassLoader());
        Class<?> factoryClass = Class.forName(AbstractCalendarValidatorFactory.class.getName(), true, loader);

        Object instance = factoryClass.getMethod("getInstance").invoke(null);

        assertEquals(DefaultCalendarValidatorFactory.class.getName(), instance.getClass().getName());
        assertSame(loader, instance.getClass().getClassLoader());
    }

    @Test
    public void testServiceRegistrationIsHonoured() {
        assertEquals(DefaultCalendarValidatorFactory.class, AbstractCalendarValidatorFactory.getInstance().getClass());
    }
}
