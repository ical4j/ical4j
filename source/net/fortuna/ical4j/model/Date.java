/*
 * $Id$
 *
 * Created on 26/06/2005
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
package net.fortuna.ical4j.model;

import java.text.ParseException;


/**
 * Base class for all representations of time values in RFC2445.
 *
 * @author Ben Fortuna
 */
public class Date extends Iso8601 {

    private static final long serialVersionUID = 7136072363141363141L;

    private static final String PATTERN = "yyyyMMdd";

    /**
     * Default constructor.
     */
    public Date() {
        super(PATTERN, PRECISION_DAY);
    }
    
    /**
     * Creates a new date instance with the specified precision. This
     * constructor is only intended for use by sub-classes.
     * @param precision
     */
    protected Date(final int precision) {
        super(PATTERN, precision);
    }

    /**
     * @param time
     */
    public Date(final long time) {
        super(time, PATTERN, PRECISION_DAY);
    }
    
    /**
     * Creates a new date instance with the specified precision. This
     * constructor is only intended for use by sub-classes.
     * @param time
     * @param precision
     */
    protected Date(final long time, final int precision) {
        super(time, PATTERN, precision);
    }

    /**
     * @param date
     */
    public Date(final java.util.Date date) {
        super(date.getTime(), PATTERN, PRECISION_DAY);
    }

    /**
     * @param value
     * @throws ParseException
     */
    public Date(final String value) throws ParseException {
        this();
        setTime(getFormat().parse(value).getTime());
    }
}
