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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <pre>
 * $Id$ [Apr 6, 2004]
 * </pre>
 *
 * A writer that performs iCalendar folding as it writes.
 * @author Ben Fortuna
 */
public class FoldingWriter extends FilterWriter {

    /**
     * reduced to 73 to be consistent with Apple iCal..
     */
    public static final int REDUCED_FOLD_LENGTH = 73;

    /**
     * Lines of text SHOULD NOT be longer than 75 octets, excluding the line break.
     */
    public static final int MAX_FOLD_LENGTH = 75;

    private static final char[] FOLD_PATTERN = { '\r', '\n', ' ' };

    private final Log log = LogFactory.getLog(FoldingWriter.class);

    private int lineLength;

    private final int foldLength;

    /**
     * @param writer a writer to write output to
     * @param foldLength the maximum line length
     */
    public FoldingWriter(final Writer writer, final int foldLength) {
        super(writer);
        this.foldLength = Math.min(foldLength, MAX_FOLD_LENGTH);
    }

    /**
     * @param writer a writer to write output to
     */
    public FoldingWriter(final Writer writer) {
        this(writer, REDUCED_FOLD_LENGTH);
    }

    /**
     * {@inheritDoc}
     */
    public final void write(final int c) throws IOException {

        /*
         * super.write(c); if (c == '\n') { lineLength = 0; } else { lineLength += 1; } if (lineLength >= FOLD_LENGTH) {
         * super.write(FOLD_PATTERN); }
         */
        write(new char[] { (char) c }, 0, 1);
    }

    /**
     * {@inheritDoc}
     */
    public final void write(final char[] buffer, final int offset,
            final int length) throws IOException {
        final int maxIndex = offset + length - 1;
        for (int i = offset; i <= maxIndex; i++) {

            // debugging..
            if (log.isTraceEnabled()) {
                log.trace("char [" + buffer[i] + "], line length ["
                        + lineLength + "]");
            }

            // check for fold first so we don't unnecessarily fold after
            // no more data..
            if (lineLength >= foldLength) {
                super.write(FOLD_PATTERN, 0, FOLD_PATTERN.length);

                // re-initialise to 1 to account for the space in fold pattern..
                lineLength = 1;
            }

            super.write(buffer[i]);

            if (buffer[i] == '\r' || buffer[i] == '\n') {
                lineLength = 0;
            }
            else {
                lineLength += 1;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void write(final String str, final int off, final int len)
            throws IOException {
        write(str.toCharArray(), off, len);
    }

    /*
     * (non-Javadoc)
     * @see java.io.FilterWriter#write(java.lang.String, int, int) public void write(String arg0, int arg1, int arg2)
     * throws IOException { super.write(arg0, arg1, arg2); if (arg0.indexOf('\n') >= 0) { lineLength = 0; } else {
     * lineLength += 1; } fold(); }
     */

    /*
     * (non-Javadoc)
     * @see java.io.Writer#write(java.lang.String) public void write(String arg0) throws IOException {
     *  /* if (lineLength +
     * arg0.length() >= FOLD_LENGTH) { super.write(arg0.substring(0,FOLD_LENGTH-lineLength-1));
     * super.write(FOLD_PATTERN); super.write(arg0.substring(FOLD_LENGTH-lineLength)); } else { super.write(arg0); } if
     * (arg0.indexOf('\n') >= 0) { lineLength = 0; } else { lineLength += 1; } fold(); char[] chars =
     * arg0.toCharArray(); for (int i=0; i <chars.length; i++) { write(chars[i]); } }
     */
}
