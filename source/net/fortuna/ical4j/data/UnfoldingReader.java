/*
 * $Id$ [06-Apr-2004]
 *
 * Copyright (c) 2004, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A reader which performs iCalendar unfolding as it reads.
 *
 * @author benfortuna
 */
public class UnfoldingReader extends PushbackReader {

    private static Log log = LogFactory.getLog(UnfoldingReader.class);

    /**
     * The pattern used to identify a fold in the iCalendar stream.
     * The unfolder may use a relaxed version of the pattern which does
     * not require the carriage return by specified the system property:
     * <code>ical4j.unfolding.relaxed=true</code>
     */
    private static final String FOLD_PATTERN = ("true".equals(System
            .getProperty("ical4j.unfolding.relaxed"))) ? "\n " : "\r\n ";

    private static final int BUFFER_SIZE = FOLD_PATTERN.length();

    private char[] buffer = new char[BUFFER_SIZE];

    /**
     * @param in
     *            a reader to read from
     */
    public UnfoldingReader(final Reader in) {

        super(in, BUFFER_SIZE);
    }

    /**
     * @see java.io.PushbackReader#read()
     */
    public final int read() throws IOException {

        int read = super.read(buffer);

        if (read >= 0) {
            if (!FOLD_PATTERN.equals(new String(buffer, 0, read))) {
                unread(buffer, 0, read);
            }
            else {
                log.debug("Unfolding..");
            }

            return super.read();
        }
        
        return read;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.Reader#read(char[])
     *
     * public int read(char[] arg0) throws IOException {
     *
     * super.read(buffer);
     *
     * if (!FOLD_PATTERN.equals(new String(buffer))) { unread(buffer); }
     *
     * return super.read(arg0); } /* (non-Javadoc)
     * @see java.io.PushbackReader#read(char[], int, int)
     *
     * public int read(char[] arg0, int arg1, int arg2) throws IOException {
     *
     * super.read(buffer);
     *
     * if (!FOLD_PATTERN.equals(new String(buffer))) { unread(buffer); }
     *
     * return super.read(arg0, arg1, arg2); }
     */
}