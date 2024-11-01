/*
 *  Copyright (c) 2021, Ben Fortuna
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

import java.util.*;

/**
 * Encapsulates the result of applying validation rules to iCalendar content.
 */
public final class ValidationResult {

    public static final ValidationResult EMPTY = new ValidationResult(Collections.emptySet());

    private final Set<ValidationEntry> entries;

    public ValidationResult(ValidationEntry...entries) {
        this(Arrays.asList(entries));
    }

    public ValidationResult(Collection<ValidationEntry> entries) {
        this.entries = new TreeSet<>(entries);
    }

    public Set<ValidationEntry> getEntries() {
        return entries;
    }

    public boolean hasErrors() {
        return entries.stream().anyMatch(e -> e.getSeverity() == ValidationEntry.Severity.ERROR);
    }

    public ValidationResult merge(ValidationResult result) {
        if (!result.getEntries().isEmpty()) {
            Set<ValidationEntry> merged = new TreeSet<>(entries);
            merged.addAll(result.getEntries());
            return new ValidationResult(merged);
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "entries=" + entries +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ValidationResult) o;
        return Objects.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }
}
