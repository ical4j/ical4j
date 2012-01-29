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

import java.nio.charset.Charset;

import net.fortuna.ical4j.util.CompatibilityHints;

/**
 * Base class for model outputters.
 * 
 * <pre>
 * $Id$
 *
 * Created on 29/12/2008
 * </pre>
 * 
 * @author Ben
 *
 */
public abstract class AbstractOutputter {

    /**
     * The default character set used to generate output.
     */
    protected static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private boolean validating;

    /**
     * The maximum line length allowed.
     */
    protected int foldLength;

    /**
     * Default constructor.
     */
    public AbstractOutputter() {
        this(true);
    }

    /**
     * @param validating indicates whether to validate calendar when outputting to stream
     */
    public AbstractOutputter(final boolean validating) {
        this(validating, CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY)
                ? FoldingWriter.MAX_FOLD_LENGTH
                        : FoldingWriter.REDUCED_FOLD_LENGTH);
    }

    /**
     * @param validating indicates whether to validate calendar when outputting to stream
     * @param foldLength maximum number of characters before a line is folded
     */
    public AbstractOutputter(final boolean validating, final int foldLength) {
        this.validating = validating;
        this.foldLength = foldLength;
    }

    /**
     * @return Returns the validating.
     */
    public final boolean isValidating() {
        return validating;
    }

    /**
     * @param validating The validating to set.
     */
    public final void setValidating(final boolean validating) {
        this.validating = validating;
    }
}
