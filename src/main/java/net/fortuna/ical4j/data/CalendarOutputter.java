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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

/**
 * <pre>
 * $Id$ [Apr 5, 2004]
 * </pre>
 *
 * Writes an iCalendar model to an output stream.
 * @author Ben Fortuna
 */
public class CalendarOutputter extends AbstractOutputter {

    /**
     * Default constructor.
     */
    public CalendarOutputter() {
        super();
    }

    /**
     * @param validating indicates whether to validate calendar when outputting to stream
     */
    public CalendarOutputter(final boolean validating) {
        super(validating);
    }

    /**
     * @param validating indicates whether to validate calendar when outputting to stream
     * @param foldLength maximum number of characters before a line is folded
     */
    public CalendarOutputter(final boolean validating, final int foldLength) {
        super(validating, foldLength);
    }

    /**
     * Outputs an iCalender string to the specified output stream.
     * @param calendar calendar to write to ouput stream
     * @param out an output stream
     * @throws IOException thrown when unable to write to output stream
     * @throws ValidationException where calendar validation fails
     */
    public final void output(final Calendar calendar, final OutputStream out)
            throws IOException, ValidationException {
        output(calendar, new OutputStreamWriter(out, DEFAULT_CHARSET));
    }

    /**
     * Outputs an iCalender string to the specified writer.
     * @param calendar calendar to write to writer
     * @param out a writer
     * @throws IOException thrown when unable to write to writer
     * @throws ValidationException where calendar validation fails
     */
    public final void output(final Calendar calendar, final Writer out)
            throws IOException, ValidationException {
        if (isValidating()) {
            calendar.validate();
        }

        final FoldingWriter writer = new FoldingWriter(out, foldLength);
        try {
            writer.write(calendar.toString());
        }
        finally {
            writer.close();
        }
    }
}
