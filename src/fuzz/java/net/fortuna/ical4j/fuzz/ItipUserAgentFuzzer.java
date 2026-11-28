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

import net.fortuna.ical4j.agent.VEventUserAgent;
import net.fortuna.ical4j.agent.VToDoUserAgent;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Uid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 11 — the iTIP user agents on a received calendar (byte 0 as in 01, byte 1 the operation). An operation returns a
 * calendar or throws IllegalArgumentException (wrong source METHOD, mixed UIDs, unsupported method) or the
 * RuntimeException("One or more components has errors…") of AbstractUserAgent#validate; anything else is a defect.
 * The input calendar is unchanged (the agents work on request.copy()); the result has the operation's METHOD, keeps
 * every UID, changes SEQUENCE only for the methods that increment it, and re-parses after toString().
 */
public class ItipUserAgentFuzzer {

    private static final String[] OPERATIONS = {"REPLY", "COUNTER", "DECLINECOUNTER", "DELEGATE", "REFRESH",
            "CANCEL", "TODO-REPLY"};
    private static final String[] PRODIDS = {"-//twake-fuzz//agent//EN", "-//Linagora//Twake Mail//EN",
            "x\r\ny", "\"quoted;prodid:\""};
    private static final Supplier<Uid> UIDS = () -> new Uid("generated@twake-fuzz");
    private static final Organizer ORGANIZER = new Organizer(URI.create("mailto:organizer@twake-fuzz.example"));

    public static void fuzzerInitialize() {
        Ical.hints(false);
    }

    private static String method(String op) {
        switch (op) {
            case "TODO-REPLY":
                return "REPLY";
            case "DELEGATE":
                return "REQUEST";
            default:
                return op;
        }
    }

    private static int sequence(CalendarComponent c) {
        Optional<Property> s = c.getProperty(Property.SEQUENCE);
        if (s.isEmpty()) {
            return -1;
        }
        try {
            return ((Sequence) s.get()).getSequenceNo();
        } catch (ClassCastException | NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    public static void fuzzerTestOneInput(byte[] input) {
        if (input.length < 2) {
            return;
        }
        int selector = input[0] & 0xff;
        Ical.hints((selector & 1) != 0);
        String op = OPERATIONS[(input[1] & 0xff) % OPERATIONS.length];
        ProdId prodId = new ProdId(PRODIDS[((input[1] & 0xff) / OPERATIONS.length) % PRODIDS.length]);
        Calendar request;
        try {
            request = Ical.builder((selector & 2) != 0).build(new ByteArrayInputStream(input, 2, input.length - 2));
        } catch (ParserException | IOException e) {
            return;
        }
        if (request == null) {
            return;
        }
        String before;
        try {
            before = request.toString();
        } catch (DateTimeException e) {
            return; // scenario 01's finding
        }
        Ical.debug("operation", op);
        try {
            request.copy();
        } catch (UnsupportedOperationException e) {
            return; // Calendar#copy fails on non-X- custom properties / components: scenario 13's finding
        }

        VEventUserAgent agent = new VEventUserAgent(prodId, ORGANIZER, UIDS);
        List<VEvent> events = request.getComponents(CalendarComponent.VEVENT);
        Calendar result;
        try {
            switch (op) {
                case "REPLY":
                    result = agent.reply(request);
                    break;
                case "COUNTER":
                    result = agent.counter(request);
                    break;
                case "DECLINECOUNTER":
                    result = agent.declineCounter(request);
                    break;
                case "DELEGATE":
                    result = agent.delegate(request);
                    break;
                case "REFRESH":
                    if (events.isEmpty()) {
                        return;
                    }
                    result = agent.refresh(events.get(0));
                    break;
                case "CANCEL":
                    if (events.isEmpty()) {
                        return;
                    }
                    // cancel wraps the given components without copying them: copies are passed (as a consumer would)
                    List<VEvent> copies = new ArrayList<>();
                    for (VEvent e : events) {
                        copies.add((VEvent) e.copy());
                    }
                    result = agent.cancel(copies.toArray(new VEvent[0]));
                    break;
                default:
                    result = new VToDoUserAgent(prodId, ORGANIZER, UIDS).reply(request);
                    break;
            }
        } catch (IllegalArgumentException e) {
            return;
        } catch (UnsupportedOperationException e) {
            // Calendar#copy (scenario 13) fails in a parallelStream: the ForkJoin frames differ at each run and would
            // flood the findings, so the defect is rethrown with the ical4j frames only
            throw Ical.withoutJdkFrames(op + ": UnsupportedOperationException", e);
        } catch (DateTimeException e) {
            // DTSTAMP/SEQUENCE handling or the agent's validate() reading a lazily parsed date: one signature per
            // ical4j path, whatever JDK frames the bad value goes through
            throw Ical.withoutJdkFrames(op + ": DateTimeException", e);
        } catch (RuntimeException e) {
            if (e.getClass() == RuntimeException.class && String.valueOf(e.getMessage())
                    .startsWith("One or more components has errors")) {
                return;
            }
            throw e;
        }

        String after = request.toString();
        if (!after.equals(before)) {
            throw new AssertionError(op + " changed the input calendar:\n" + Ical.abbreviate(before, 1500)
                    + "\n--- became ---\n" + Ical.abbreviate(after, 1500));
        }

        Optional<Property> method = result.getProperty(Property.METHOD);
        if (method.isEmpty() || !method.get().getValue().equals(method(op))) {
            throw new AssertionError(op + " returned METHOD " + method.map(Property::getValue).orElse("<none>"));
        }

        // every UID of the input is kept (CANCEL and REFRESH only carry the events)
        Set<String> uids = new HashSet<>();
        for (CalendarComponent c : result.getComponents()) {
            c.getProperty(Property.UID).ifPresent(u -> uids.add(((Property) u).getValue()));
        }
        List<? extends CalendarComponent> sources = op.equals("REFRESH") ? events.subList(0, 1)
                : op.equals("CANCEL") ? events : request.getComponents();
        for (CalendarComponent c : sources) {
            if (c instanceof VTimeZone) {
                continue;
            }
            Optional<Property> uid = c.getProperty(Property.UID);
            if (uid.isPresent() && !uids.contains(uid.get().getValue())) {
                throw new AssertionError(op + " lost the UID " + uid.get().getValue() + " of a " + c.getName());
            }
        }

        // SEQUENCE: incremented by CANCEL only among these operations, unchanged by the others
        if (!op.equals("CANCEL") && result.getComponents().size() == sources.size()) {
            for (int i = 0; i < sources.size(); i++) {
                CalendarComponent s = sources.get(i);
                CalendarComponent r = result.getComponents().get(i);
                if (s.getName().equals(r.getName()) && sequence(s) != sequence(r)
                        && !(s instanceof VTimeZone)) {
                    throw new AssertionError(op + " changed the SEQUENCE of a " + s.getName() + " from "
                            + sequence(s) + " to " + sequence(r));
                }
            }
        }

        String text = result.toString();
        Calendar reparsed;
        try {
            reparsed = Ical.parse(text, false);
        } catch (ParserException | IOException e) {
            throw new AssertionError(op + " result does not re-parse: " + e.getMessage() + "\n"
                    + Ical.abbreviate(text, 1500), e);
        }
        if (reparsed == null) {
            throw new AssertionError(op + " result re-parses to no calendar");
        }
        // (a value that drifts on re-parse is scenario 05's oracle, not repeated here)
    }
}
