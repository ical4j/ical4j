package net.fortuna.ical4j

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicInteger

/**
 * Provides a local tzurl server for tests that exercise timezone definition updates.
 *
 * The server runs inside the test JVM and serves the bundled definitions from the classpath, mapping a TZURL
 * path such as {@code /zoneinfo/Europe/London} to the resource {@code zoneinfo/Europe/London.ics}. It needs no
 * network access or container runtime, so tests that extend this class are deterministic and run everywhere.
 *
 * Previously this class started the {@code benfortuna/tzurl} image with Testcontainers. That image doesn't
 * serve definitions: it redirects every request to {@code https://static.tzurl.org}, and Java's HTTP client
 * doesn't follow an http-to-https redirect, so every update quietly fell back to the bundled definition.
 */
abstract class AbstractTzurlIntegrationTest extends Specification {

    protected static final List<String> SERVED_PREFIXES = ['zoneinfo', 'zoneinfo-global']

    @Shared
    HttpServer tzurl

    /**
     * Number of definitions served since the server started, so tests can confirm an update was fetched.
     */
    @Shared
    AtomicInteger tzurlRequests = new AtomicInteger()

    def setupSpec() {
        tzurl = HttpServer.create(new InetSocketAddress(InetAddress.loopbackAddress, 0), 0)
        tzurl.createContext('/') { HttpExchange exchange -> serve(exchange) }
        tzurl.start()
    }

    def cleanupSpec() {
        tzurl?.stop(0)
    }

    def setup() {
        System.setProperty('net.fortuna.ical4j.timezone.update.scheme', 'http')
        System.setProperty('net.fortuna.ical4j.timezone.update.host', tzurl.address.hostString)
        System.setProperty('net.fortuna.ical4j.timezone.update.port', tzurl.address.port as String)
    }

    def cleanup() {
        System.clearProperty('net.fortuna.ical4j.timezone.update.scheme')
        System.clearProperty('net.fortuna.ical4j.timezone.update.host')
        System.clearProperty('net.fortuna.ical4j.timezone.update.port')
    }

    protected void serve(HttpExchange exchange) {
        try {
            String path = exchange.requestURI.path
            String prefix = path.split('/').find { it }
            byte[] body = null
            if (exchange.requestMethod == 'GET' && prefix in SERVED_PREFIXES && !path.contains('..')) {
                body = AbstractTzurlIntegrationTest.getResourceAsStream(path + '.ics')?.bytes
            }
            if (body == null) {
                exchange.sendResponseHeaders(404, -1)
                return
            }
            tzurlRequests.incrementAndGet()
            exchange.responseHeaders.add('Content-Type', 'text/calendar; charset=utf-8')
            exchange.sendResponseHeaders(200, body.length)
            exchange.responseBody.withStream { it.write(body) }
        } finally {
            exchange.close()
        }
    }
}
