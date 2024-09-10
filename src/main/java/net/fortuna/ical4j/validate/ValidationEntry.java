/*
 *  Copyright (c) 2022, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.validate;

import java.util.Comparator;
import java.util.Objects;

public class ValidationEntry implements Comparable<ValidationEntry> {

    public enum Severity {
        ERROR, WARNING, INFO
    }

    private final String message;

    private final Severity severity;

    private final String context;

    public ValidationEntry(String message, Severity severity, String context) {
        this.message = message;
        this.severity = severity;
        this.context = context;
    }

    public ValidationEntry(ValidationRule<?> rule, String context, String...instances) {
        this.message = rule.getMessage(instances);
        this.severity = rule.getSeverity();
        this.context = context;
    }

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "ValidationEntry{" +
                "message='" + message + '\'' +
                ", level=" + severity +
                ", context='" + context + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ValidationEntry) o;
        return Objects.equals(message, that.message) && severity == that.severity && Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, severity, context);
    }

    @Override
    public int compareTo(ValidationEntry o) {
        return Comparator.comparing(ValidationEntry::getContext)
                .thenComparing(ValidationEntry::getSeverity)
                .thenComparing(ValidationEntry::getMessage)
                .compare(this, o);
    }
}
