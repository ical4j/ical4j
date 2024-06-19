/*
 *  Copyright (c) 2023, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.model;

/**
 * Registered Link Relation Types.
 * @see <a href="https://www.iana.org/assignments/link-relations/link-relations.xhtml">link-relations</a>
 */
public enum LinkRelationType {

    about, acl, alternate, amphtml, appendix, apple_touch_icon, apple_touch_startup_image,
    archives, author, blocked_by, bookmark, canonical, chapter, cite_as, collection, contents,
    convertedfrom, copyright, create_form, current, describedby, describes, disclosure, dns_prefetch,
    duplicate, edit, edit_form, edit_media, enclosure, external, first, glossary, help, hosts,
    hub, icon, index, intervalafter, intervalbefore, intervalcontains, intervaldisjoint, intervalduring,
    intervalequals, intervalfinishedby, intervalfinishes, intervalin, intervalmeets, intervalmetby,
    intervaloverlappedby, intervaloverlaps, intervalstartedby, intervalstarts, item, last, latest_version,
    license, linkset, lrdd, manifest, mask_icon, media_feed, memento, micropub, modulepreload, monitor,
    monitor_group, next, next_archive, nofollow, noopener, noreferrer, opener, openid2$local_id,
    openid2$provider, original, p3pv1, payment, pingback, preconnect, predecessor_version, prefetch,
    preload, prerender, prev, preview, previous, prev_archive, privacy_policy, profile, publication,
    related, restconf, replies, ruleinput, search, section, self, service, service_desc, service_doc,
    service_meta, sip_trunking_capability, sponsored, start, status, stylesheet, subsection,
    successor_version, sunset, tag, terms_of_service, timegate, timemap, type, ugc, up, version_history,
    via, webmention, working_copy, working_copy_of;

    public static LinkRelationType from(String linkRelationTypeString) {
        return Enum.valueOf(LinkRelationType.class, linkRelationTypeString.replace("-", "_")
                .replace(".", "$"));
    }

    @Override
    public String toString() {
        return super.toString().replace("_", "-").replace("$", ".");
    }
}
