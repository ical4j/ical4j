package net.fortuna.ical4j.model

import spock.lang.Specification

class TimeZoneLoaderAvailableIdsSpec extends Specification {

    ClassLoader previous = Thread.currentThread().contextClassLoader

    def cleanup() {
        Thread.currentThread().contextClassLoader = previous
    }

    def 'available ids are read from the bundled resource'() {
        given: 'a timezone loader for the bundled definitions'
        TimeZoneLoader loader = ['zoneinfo/']

        when: 'the available ids are listed'
        def ids = loader.availableIDs

        then: 'well-known ids are present'
        ids.contains('Australia/Melbourne')
        ids.contains('Europe/London')
    }

    def 'the available ids resource stream is closed after reading'() {
        given: 'a context class loader that records whether the resource stream is closed'
        def closed = false
        def tracking = new ClassLoader(previous) {
            @Override
            InputStream getResourceAsStream(String name) {
                def stream = super.getResourceAsStream(name)
                if (stream == null || !name.endsWith('tz.availableIds')) {
                    return stream
                }
                return new FilterInputStream(stream) {
                    @Override
                    void close() throws IOException {
                        closed = true
                        super.close()
                    }
                }
            }
        }
        Thread.currentThread().contextClassLoader = tracking

        and: 'a timezone loader for the bundled definitions'
        TimeZoneLoader loader = ['zoneinfo/']

        when: 'the available ids are listed'
        def ids = loader.availableIDs

        then: 'the ids were read and the stream was closed'
        ids.length > 0
        closed
    }
}
