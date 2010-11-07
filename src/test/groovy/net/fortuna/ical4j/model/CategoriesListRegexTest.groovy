/**
 * 
 */
package net.fortuna.ical4j.model

import groovy.util.GroovyTestCase;

/**
 * @author fortuna
 *
 */
class CategoriesListRegexTest extends GroovyTestCase {

	void testRegexMatch() {
		def pattern = /([^\\](?:\\{2})),|([^\\]),/
		
		assert 'a,' =~ pattern
		assert 'a\\\\,' =~ pattern
		assert !('a\\,b' =~ pattern)
		assert 'a\\\\,' =~ pattern
		assert !('a\\\\\\,' =~ pattern)
	}
}
