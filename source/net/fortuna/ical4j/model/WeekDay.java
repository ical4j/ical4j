/*
 * $Id$
 * 
 * Created: 19/12/2004
 *
 * Copyright (c) 2004, Ben Fortuna
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;

/**
 * Defines a day of the week with a possible offset related to
 * a MONTHLY or YEARLY occurrence.
 * @author benfortuna
 */
public class WeekDay implements Serializable {
    
    private static final long serialVersionUID = -4412000990022011469L;

    public static final String SU = "SU";

    public static final String MO = "MO";

    public static final String TU = "TU";

    public static final String WE = "WE";

    public static final String TH = "TH";

    public static final String FR = "FR";

    public static final String SA = "SA";

    private String day;
    
    private int offset;
    
    /**
     * @param value
     */
    public WeekDay(final String value) {
        try {
            offset = Integer.parseInt(value.substring(0, value.length() - 2));
        }
        catch (NumberFormatException nfe) {
            offset = 0;
        }
        day = value.substring(value.length() - 2);
    }
    
    /**
     * @param day
     * @param offset
     */
    public WeekDay(final String day, final int offset) {
        this.day = day;
        this.offset = offset;
    }
    
    /**
     * @return Returns the day.
     */
    public final String getDay() {
        return day;
    }
    
    /**
     * @return Returns the offset.
     */
    public final int getOffset() {
        return offset;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer b = new StringBuffer();
        if (getOffset() != 0) {
            b.append(getOffset());
        }
        b.append(getDay());
        return b.toString();
    }
}
