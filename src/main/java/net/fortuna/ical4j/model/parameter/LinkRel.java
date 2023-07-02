/*
 *  Copyright (c) 2022, Ben Fortuna
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

package net.fortuna.ical4j.model.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * <pre>
 *     Purpose:
 *     This property specifies the relationship of data referenced by a LINK property.
 * Format Definition:
 *
 *     This parameter is defined by the following notation:
 *
 * linkrelparam = "LINKREL" "="
 *                 (DQUOTE uri DQUOTE
 *                / iana-token)   ; Other IANA registered type
 *
 * Description:
 *
 *     This parameter MUST be specified on all LINK properties and define the type of reference. This allows programs consuming this data to automatically scan for references they support. There is no default relation type.
 * Any link relation in the link registry established by [RFC8288], or new link relations, may be used. It is expected that link relation types seeing significant usage in calendaring will have the calendaring usage described in an RFC.
 * </pre>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9253.html#name-link-relation">rfc9253</a>
 */
public class LinkRel extends Parameter {

    private static final String PARAM_NAME = "LINKREL";

    private final URI uri;

    public LinkRel(String value) throws URISyntaxException {
        this(Uris.create(Strings.unquote(value)));
    }

    public LinkRel(URI uri) {
        super(PARAM_NAME, new Factory());
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public String getValue() {
        return Uris.decode(Strings.valueOf(getUri()));
    }

    public static class Factory extends Content.Factory implements ParameterFactory<LinkRel> {

        public Factory() {
            super(PARAM_NAME);
        }

        @Override
        public LinkRel createParameter(String value) throws URISyntaxException {
            return new LinkRel(value);
        }
    }
}
