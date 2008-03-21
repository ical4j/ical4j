/*
 * $Id$ [06-Apr-2004]
 *
 * Copyright (c) 2005, Ben Fortuna
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

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Arrays;

import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A reader which performs iCalendar unfolding as it reads. Note that unfolding rules may be "relaxed" to allow
 * unfolding of non-conformant *.ics files. By specifying the system property "ical4j.unfolding.relaxed=true" iCalendar
 * files created with Mozilla Calendar/Sunbird may be correctly unfolded.
 * @author Ben Fortuna
 */
public class UnfoldingReader extends PushbackReader {

    private Log log = LogFactory.getLog(UnfoldingReader.class);

    /**
     * The pattern used to identify a fold in an iCalendar data stream.
     */
    private static final char[] DEFAULT_FOLD_PATTERN = { '\r', '\n', ' ' };

    /**
     * The pattern used to identify a fold in Mozilla Calendar/Sunbird and KOrganizer.
     */
    private static final char[] RELAXED_FOLD_PATTERN_1 = { '\n', ' ' };
    
    /** 
     * The pattern used to identify a fold in Microsoft Outlook 2007. 
     */ 
    private static final char[] RELAXED_FOLD_PATTERN_2 = { '\r', '\n', '\t' };
    
    /** 
     * The pattern used to identify a fold in Microsoft Outlook 2007. 
     */ 
    private static final char[] RELAXED_FOLD_PATTERN_3 = { '\n', '\t' };
    
    private char[][] patterns;

    private char[][] buffers;

    private int linesUnfolded;

    /**
     * Creates a new unfolding reader instance. Relaxed unfolding flag is read from system property.
     * @param in the reader to unfold from
     */
    public UnfoldingReader(final Reader in) {
        this(in, CompatibilityHints
                .isHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING));
    }

    /**
     * Creates a new unfolding reader instance.
     * @param in a reader to read from
     * @param relaxed specifies whether unfolding is relaxed
     */
    public UnfoldingReader(final Reader in, final boolean relaxed) {
        super(in, DEFAULT_FOLD_PATTERN.length);
        if (relaxed) {
            patterns = new char[4][];
            patterns[0] = DEFAULT_FOLD_PATTERN;
            patterns[1] = RELAXED_FOLD_PATTERN_1;
            patterns[2] = RELAXED_FOLD_PATTERN_2;
            patterns[3] = RELAXED_FOLD_PATTERN_3;
        }
        else {
            patterns = new char[1][];
            patterns[0] = DEFAULT_FOLD_PATTERN;
        }
        buffers = new char[patterns.length][];
        for (int i = 0; i < patterns.length; i++) {
            buffers[i] = new char[patterns[i].length];
        }
    }

    /**
     * @return number of lines unfolded so far while reading
     */
    public final int getLinesUnfolded() {
        return linesUnfolded;
    }

    /**
     * @see java.io.PushbackReader#read()
     */
    public final int read() throws IOException {
        int c = super.read();
        boolean doUnfold = false;
        for (int i = 0; i < patterns.length; i++) {
            if (c == patterns[i][0]) {
                doUnfold = true;
            }
        }
        if (!doUnfold) {
            return c;
        }
        else {
            unread(c);
        }

        boolean didUnfold;

        // need to loop since one line fold might be directly followed by another
        do {
            didUnfold = false;

            for (int i = 0; i < buffers.length; i++) {
                int read = super.read(buffers[i]);
                if (read > 0) {
                    if (!Arrays.equals(patterns[i], buffers[i])) {
                        unread(buffers[i], 0, read);
                    }
                    else {
                        if (log.isTraceEnabled()) {
                            log.trace("Unfolding...");
                        }
                        linesUnfolded++;
                        didUnfold = true;
                    }
                }
                else {
                    return read;
                }
            }
        }
        while (didUnfold);

        return super.read();
    }
}
