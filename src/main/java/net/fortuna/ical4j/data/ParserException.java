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

import java.text.MessageFormat;

/**
 * <pre>
 * $Id$ [Apr 5, 2004]
 * </pre>
 *
 * An exception thrown when an error occurs in parsing iCalendar data.
 * @author Ben Fortuna
 */
public class ParserException extends Exception {

    private static final long serialVersionUID = 6116644246112002214L;

    private static final String ERROR_MESSAGE_PATTERN = "Error at line {0}:";

    private int lineNo;

    /**
     * @param lineNo line number where parsing error ocurred
     */
    public ParserException(final int lineNo) {
        this.lineNo = lineNo;
    }

    /**
     * Constructor with message.
     * @param message a descriptive message for the exception
     * @param lineNo line number where parsing error ocurred
     */
    public ParserException(final String message, final int lineNo) {
        super(MessageFormat.format(ERROR_MESSAGE_PATTERN, new Object[] { new Integer(lineNo)}) + message);
        this.lineNo = lineNo;
    }

    /**
     * Constructor with message and cause.
     * @param message a descriptive message for the exception
     * @param lineNo line number where parsing error ocurred
     * @param cause a throwable that is the cause of this exception
     */
    public ParserException(final String message, final int lineNo,
            final Throwable cause) {

        super(MessageFormat.format(ERROR_MESSAGE_PATTERN, new Object[] { new Integer(lineNo)}) + message, cause);
        this.lineNo = lineNo;
    }

    /**
     * @return the lineNo
     */
    public final int getLineNo() {
        return lineNo;
    }
}
