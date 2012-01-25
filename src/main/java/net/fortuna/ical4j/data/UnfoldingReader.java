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

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Arrays;

import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <pre>
 * $Id$ [06-Apr-2004]
 * </pre>
 *
 * A reader which performs iCalendar unfolding as it reads. Note that unfolding rules may be "relaxed" to allow
 * unfolding of non-conformant *.ics files. By specifying the system property "ical4j.unfolding.relaxed=true" iCalendar
 * files created with Mozilla Calendar/Sunbird may be correctly unfolded.
 * 
 * To wrap this reader with a {@link java.io.BufferedReader} you must ensure you specify an identical buffer size
 * to that used in the {@link java.io.BufferedReader}.
 * 
 * @author Ben Fortuna
 */
public class UnfoldingReader extends PushbackReader {

    private Log log = LogFactory.getLog(UnfoldingReader.class);

    /**
     * The pattern used to identify a fold in an iCalendar data stream.
     */
    private static final char[] DEFAULT_FOLD_PATTERN_1 = { '\r', '\n', ' ' };
    
    /** 
     * The pattern used to identify a fold in Microsoft Outlook 2007. 
     */ 
    private static final char[] DEFAULT_FOLD_PATTERN_2 = { '\r', '\n', '\t' };

    /**
     * The pattern used to identify a fold in Mozilla Calendar/Sunbird and KOrganizer.
     */
    private static final char[] RELAXED_FOLD_PATTERN_1 = { '\n', ' ' };
    
    /** 
     * The pattern used to identify a fold in Microsoft Outlook 2007. 
     */ 
    private static final char[] RELAXED_FOLD_PATTERN_2 = { '\n', '\t' };
    
    private char[][] patterns;

    private char[][] buffers;

    private int linesUnfolded;
    
    private int maxPatternLength = 0;

    /**
     * Creates a new unfolding reader instance. Relaxed unfolding flag is read from system property.
     * @param in the reader to unfold from
     */
    public UnfoldingReader(final Reader in) {
        this(in, DEFAULT_FOLD_PATTERN_1.length, CompatibilityHints
                .isHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING));
    }
    
    /**
     * @param in reader source for data
     * @param size the buffer size
     */
    public UnfoldingReader(final Reader in, int size) {
        this(in, size, CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING));
    }

    /**
     * @param in reader source for data
     * @param relaxed indicates whether relaxed unfolding is enabled
     */
    public UnfoldingReader(final Reader in, boolean relaxed) {
        this(in, DEFAULT_FOLD_PATTERN_1.length, relaxed); 
    }

    /**
     * Creates a new unfolding reader instance.
     * @param in a reader to read from
     * @param size the buffer size
     * @param relaxed specifies whether unfolding is relaxed
     */
    public UnfoldingReader(final Reader in, int size, final boolean relaxed) {
        super(in, size);
        if (relaxed) {
            patterns = new char[4][];
            patterns[0] = DEFAULT_FOLD_PATTERN_1;
            patterns[1] = DEFAULT_FOLD_PATTERN_2;
            patterns[2] = RELAXED_FOLD_PATTERN_1;
            patterns[3] = RELAXED_FOLD_PATTERN_2;
        }
        else {
            patterns = new char[2][];
            patterns[0] = DEFAULT_FOLD_PATTERN_1;
            patterns[1] = DEFAULT_FOLD_PATTERN_2;
        }
        buffers = new char[patterns.length][];
        for (int i = 0; i < patterns.length; i++) {
            buffers[i] = new char[patterns[i].length];
            maxPatternLength = Math.max(maxPatternLength, patterns[i].length);
        }
    }

    /**
     * @return number of lines unfolded so far while reading
     */
    public final int getLinesUnfolded() {
        return linesUnfolded;
    }

    /**
     * {@inheritDoc}
     */
    public final int read() throws IOException {
        final int c = super.read();
        boolean doUnfold = false;
        for (int i = 0; i < patterns.length; i++) {
            if (c == patterns[i][0]) {
                doUnfold = true;
                break;
            }
        }
        if (!doUnfold) {
            return c;
        }
        else {
            unread(c);
        }

        unfold();

        return super.read();
    }
    
    /**
     * {@inheritDoc}
     */
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        final int read = super.read(cbuf, off, len);
        boolean doUnfold = false;
        for (int i = 0; i < patterns.length; i++) {
            if (read > 0 && cbuf[0] == patterns[i][0]) {
                doUnfold = true;
                break;
            }
            else {
                for (int j = 0; j < read; j++) {
                    if (cbuf[j] == patterns[i][0]) {
                        unread(cbuf, j, read - j);
                        return j;
                    }
                }
            }
        }
        if (!doUnfold) {
            return read;
        }
        else {
            unread(cbuf, off, read);
        }

        unfold();

        return super.read(cbuf, off, maxPatternLength);
    }
    
    private void unfold() throws IOException {
        // need to loop since one line fold might be directly followed by another
        boolean didUnfold;
        do {
            didUnfold = false;

            for (int i = 0; i < buffers.length; i++) {
                int read = 0;             
                while (read < buffers[i].length) {
                    final int partialRead = super.read(buffers[i], read, buffers[i].length - read);
                    if (partialRead < 0) {
                        break;
                    }
                    read += partialRead;
                }
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
//                else {
//                    return read;
//                }
            }
        }
        while (didUnfold);
    }
}
