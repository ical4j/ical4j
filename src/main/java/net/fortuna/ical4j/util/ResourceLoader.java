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

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author fortuna
 *
 */
public class ResourceLoader {

	private static final Log LOG = LogFactory.getLog(ResourceLoader.class);
	
	/**
	 * Load a resource via the thread context classloader. If security permissions don't allow
	 * this fallback to loading via current classloader.
	 * @param name a resource name
	 * @return a {@link URL} or null if resource is not found
	 */
	public static URL getResource(String name) {
		URL resource = null;
		try {
			resource = Thread.currentThread().getContextClassLoader().getResource(name);
		}
		catch (SecurityException e) {
			LOG.info("Unable to access context classloader, using default. " + e.getMessage());
		}
		if (resource == null) {
			resource = ResourceLoader.class.getResource("/" + name);
		}
		return resource;
	}

	/**
	 * Load a resource via the thread context classloader. If security permissions don't allow
	 * this fallback to loading via current classloader.
	 * @param name a resource name
	 * @return an {@link InputStream} or null if resource is not found
	 */
	public static InputStream getResourceAsStream(String name) {
		InputStream stream = null;
		try {
			stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		}
		catch (SecurityException e) {
			LOG.info("Unable to access context classloader, using default. " + e.getMessage());
		}
		if (stream == null) {
			stream = ResourceLoader.class.getResourceAsStream("/" + name);
		}
		return stream;
	}
}
