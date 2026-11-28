/**
 * Copyright (c) 2026, Ben Fortuna
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

package net.fortuna.ical4j.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.validate.RecurValidator;
import net.fortuna.ical4j.validate.ValidationException;

import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Locale;

/**
 * 14 — Recur(String, experimental) / Recur#toString (RecurFormatter) / RecurValidator under several default locales.
 * Parsing throws only IllegalArgumentException / DateTimeParseException; an accepted rule's text re-parses into an
 * equal rule with the same text; the rule and its text do not depend on the default locale (ASCII digits only);
 * RecurValidator returns or throws ValidationException.
 */
public class RecurRuleRoundTripFuzzer {

    private static final Locale[] LOCALES = {Locale.ROOT, Locale.forLanguageTag("en-US"),
            Locale.forLanguageTag("fr-FR"), Locale.forLanguageTag("th-TH-u-ca-buddhist"),
            Locale.forLanguageTag("ja-JP-u-ca-japanese"), Locale.forLanguageTag("ar-SA-u-nu-arab"),
            Locale.forLanguageTag("tr-TR"), Locale.forLanguageTag("hi-IN-u-nu-deva")};

    private static final Locale ORIGINAL = Locale.getDefault();

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    private static Recur<Temporal> parse(String rule, boolean experimental) {
        try {
            return new Recur<>(rule, experimental);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return null;
        }
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider d) {
        boolean experimental = d.consumeBoolean();
        Locale locale = d.pickValue(LOCALES);
        String rule = Rules.rule(d);
        Ical.debug("rule", rule);
        Ical.debug("locale", locale + ", experimental " + experimental);

        Recur<Temporal> root;
        String rootText;
        Recur<Temporal> local;
        String localText;
        try {
            Locale.setDefault(Locale.ROOT);
            root = parse(rule, experimental);
            rootText = root == null ? null : root.toString();
            Locale.setDefault(locale);
            local = parse(rule, experimental);
            localText = local == null ? null : local.toString();
            if (local != null) {
                try {
                    new RecurValidator().validate(local);
                } catch (ValidationException e) {
                    // documented
                }
            }
        } finally {
            Locale.setDefault(ORIGINAL);
        }

        if ((root == null) != (local == null)) {
            throw new AssertionError("rule [" + rule + "] is " + (root == null ? "rejected" : "accepted")
                    + " under the ROOT locale but " + (local == null ? "rejected" : "accepted") + " under " + locale);
        }
        if (root == null) {
            return;
        }
        if (!root.equals(local) || !rootText.equals(localText)) {
            throw new AssertionError("rule [" + rule + "] depends on the default locale: [" + rootText + "] under ROOT, ["
                    + localText + "] under " + locale);
        }
        for (int i = 0; i < localText.length(); i++) {
            char c = localText.charAt(i);
            if (c > 127) {
                throw new AssertionError("rule [" + rule + "] is written with non-ASCII [" + localText + "] under "
                        + locale);
            }
        }

        Recur<Temporal> again;
        try {
            again = new Recur<>(rootText, experimental);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new AssertionError("rule [" + rule + "] is written [" + rootText + "], which does not re-parse: "
                    + e.getMessage(), e);
        }
        if (!again.equals(root)) {
            throw new AssertionError("rule [" + rule + "] is written [" + rootText + "], which re-parses into another "
                    + "rule, written [" + again + "]");
        }
        if (!again.toString().equals(rootText)) {
            throw new AssertionError("rule [" + rule + "] written [" + rootText + "] is re-written [" + again + "]");
        }
    }
}
