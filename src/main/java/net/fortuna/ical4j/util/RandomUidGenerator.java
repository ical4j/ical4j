package net.fortuna.ical4j.util;

import net.fortuna.ical4j.model.property.Uid;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * This {@link UidGenerator} uses the Java {@link UUID#randomUUID()} implementation to generate
 * UIDs.
 *
 * For ultimate security you can also use the secure flag to generate UIDs that are less crackable
 * (from <a href="https://neilmadden.blog/2018/08/30/moving-away-from-uuids/">Moving away from UUIDs</a>).
 *
 * Extract from RFC7986:
 *
 * <pre>
 * The description of the "UID" property in [RFC5545] contains some
 * recommendations on how the value can be constructed.  In particular,
 * it suggests use of host names, IP addresses, and domain names to
 * construct the value.  However, this is no longer considered good
 * practice, particularly from a security and privacy standpoint, since
 * use of such values can leak key information about a calendar user or
 * their client and network environment.  This specification updates
 * [RFC5545] by stating that "UID" values MUST NOT include any data that
 * might identify a user, host, domain, or any other security- or
 * privacy-sensitive information.  It is RECOMMENDED that calendar user
 * agents now generate "UID" values that are hex-encoded random
 * Universally Unique Identifier (UUID) values as defined in
 * Sections 4.4 and 4.5 of [RFC4122].
 *
 * The following is an example of such a property value:
 *
 * UID:5FC53010-1267-4F8E-BC28-1D7AE55A7C99
 *
 * </pre>
 */
public class RandomUidGenerator implements UidGenerator {

    private final boolean secure;

    private final SecureRandom random;
    private final Base64.Encoder encoder;

    public RandomUidGenerator() {
        this(false);
    }

    public RandomUidGenerator(boolean secure) {
        this.secure = secure;
        if (secure) {
            random = new SecureRandom();
            encoder = Base64.getUrlEncoder().withoutPadding();
        } else {
            random = null;
            encoder = null;
        }
    }

    public boolean isSecure() {
        return secure;
    }

    @Override
    public Uid generateUid() {
        if (secure) {
            var buffer = new byte[20];
            random.nextBytes(buffer);
            return new Uid(encoder.encodeToString(buffer));
        } else {
            return new Uid(UUID.randomUUID().toString());
        }
    }
}
