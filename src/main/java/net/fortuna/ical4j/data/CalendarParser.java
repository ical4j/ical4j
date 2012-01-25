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
package net.fortuna.ical4j.data;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

/**
 * <pre>
 * $Id$
 * 
 * Created [Nov 5, 2004]
 * </pre>
 *
 * Implementors provide iCalendar parsing functionality.
 * @author Ben Fortuna
 */
public interface CalendarParser {

    /**
     * Parse the iCalendar data from the specified input stream.
     * @param in an input stream from which to read iCalendar data
     * @param handler the content handler to notify during parsing
     * @throws IOException thrown when unable to read from the specified stream
     * @throws ParserException thrown if an error occurs during parsing
     */
    void parse(InputStream in, ContentHandler handler) throws IOException,
            ParserException;

    /**
     * Parse the iCalendar data from the specified reader.
     * @param in a reader from which to read iCalendar data
     * @param handler the content handler to notify during parsing
     * @throws IOException thrown when unable to read from the specified reader
     * @throws ParserException thrown if an error occurs during parsing
     */
    void parse(Reader in, ContentHandler handler) throws IOException,
            ParserException;
}
