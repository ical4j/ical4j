package net.fortuna.ical4j.util;

import java.io.InputStream;
import java.net.URL;

/**
 * @author fortuna
 *
 */
public class ResourceLoader {

	/**
	 * Load a resource via the thread context classloader. If security permissions don't allow
	 * this fallback to loading via current classloader.
	 * @param name a resource name
	 * @return a {@link URL} or null if resource is not found
	 */
	public static URL getResource(String name) {
		try {
			return Thread.currentThread().getContextClassLoader().getResource(name);
		}
		catch (SecurityException e) {
			return ResourceLoader.class.getResource("/" + name);
		}
	}

	/**
	 * Load a resource via the thread context classloader. If security permissions don't allow
	 * this fallback to loading via current classloader.
	 * @param name a resource name
	 * @return an {@link InputStream} or null if resource is not found
	 */
	public static InputStream getResourceAsStream(String name) {
		try {
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		}
		catch (SecurityException e) {
			return ResourceLoader.class.getResourceAsStream("/" + name);
		}
	}
}
