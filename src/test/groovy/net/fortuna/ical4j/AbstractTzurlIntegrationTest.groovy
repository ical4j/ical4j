package net.fortuna.ical4j


import org.testcontainers.containers.GenericContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

/**
 * Provides local tzurl instance for tests that require timezone definition updates.
 */
@Testcontainers
abstract class AbstractTzurlIntegrationTest extends Specification {

    @Shared
    GenericContainer tzurl = new GenericContainer('benfortuna/tzurl')
            .withExposedPorts(80)

    def setup() {
        System.setProperty('net.fortuna.ical4j.timezone.update.host', tzurl.containerIpAddress)
        System.setProperty('net.fortuna.ical4j.timezone.update.port', tzurl.getMappedPort(80) as String)
    }

    def cleanup() {
        System.clearProperty('net.fortuna.ical4j.timezone.update.host')
        System.clearProperty('net.fortuna.ical4j.timezone.update.port')
    }
}
