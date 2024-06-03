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
package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.property.immutable.ImmutableAction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.fortuna.ical4j.model.Parameter.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableAction.AUDIO;
import static net.fortuna.ical4j.model.property.immutable.ImmutableCalScale.GREGORIAN;
import static net.fortuna.ical4j.model.property.immutable.ImmutableClazz.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableStatus.*;
import static net.fortuna.ical4j.model.property.immutable.ImmutableTransp.OPAQUE;
import static net.fortuna.ical4j.model.property.immutable.ImmutableTransp.TRANSPARENT;
import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0;
import static net.fortuna.ical4j.validate.ValidationRule.ValidationType.*;

/**
 * $Id$ [15-May-2004]
 *
 * Defines methods for validating properties and property lists.
 *
 * @author Ben Fortuna
 */
public final class PropertyValidator<T extends Property> extends AbstractValidator<T> {

    private static final ValidationRule<Property> DATE_OR_DATETIME_VALUE = new ValidationRule<>(None, prop -> {
        Optional<Value> v = prop.getParameter(VALUE);
        return !(!v.isPresent() || Value.DATE.equals(v.get()) || Value.DATE_TIME.equals(v.get()));
    }, "MUST be specified as a DATE or DATE-TIME:", VALUE);

    private static final ValidationRule<Property> BINARY_VALUE = new ValidationRule<>(None, prop -> {
        Optional<Value> v = prop.getParameter(VALUE);
        return !(!v.isPresent() || Value.BINARY.equals(v.get()));
    }, "MUST be specified as a BINARY:", VALUE);

    /**
     * <pre>
     *           FORM #3: DATE WITH LOCAL TIME AND TIME ZONE REFERENCE
     *
     *       The date and local time with reference to time zone information is
     *       identified by the use the "TZID" property parameter to reference
     *       the appropriate time zone definition.  "TZID" is discussed in
     *       detail in Section 3.2.19.  For example, the following represents
     *       2:00 A.M. in New York on January 19, 1998:
     *
     *        TZID=America/New_York:19980119T020000
     * </pre>
     */
    public static final PropertyRuleSet<DateProperty<?>> DATE_PROP_RULE_SET = new PropertyRuleSet<>(
            new ValidationRule<>(OneOrLess, VALUE, Parameter.TZID), DATE_OR_DATETIME_VALUE);

    /**
     * <pre>
     *           FORM #2: DATE WITH UTC TIME
     *
     *       The date with UTC time, or absolute time, is identified by a LATIN
     *       CAPITAL LETTER Z suffix character, the UTC designator, appended to
     *       the time value.  For example, the following represents January 19,
     *       1998, at 0700 UTC:
     *
     *        19980119T070000Z
     *
     *       The "TZID" property parameter MUST NOT be applied to DATE-TIME
     *       properties whose time values are specified in UTC.
     * </pre>
     */
    public static final PropertyRuleSet<Property> UTC_PROP_RULE_SET = new PropertyRuleSet<>(
            new ValidationRule<>(None, Parameter.TZID),
            new ValidationRule<>(ValueMatch, ".+Z$"));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        action      = "ACTION" actionparam ":" actionvalue CRLF
     *
     *        actionparam = *(";" other-param)
     *
     *
     *        actionvalue = "AUDIO" / "DISPLAY" / "EMAIL"
     *                    / iana-token / x-name
     * </pre>
     */
    public static final Validator<Action> ACTION = new PropertyValidator<>(Property.ACTION,
            new ValidationRule<>(ValueMatch, "(?i)" + String.join("|", AUDIO.getValue(),
                    ImmutableAction.DISPLAY.getValue(), ImmutableAction.EMAIL.getValue(), "X-[A-Z]+")));


    /**
     * <pre>
     * Format Definition:  This property is defined by the following
     *       notation:
     *
     *        attach     = "ATTACH" attachparam ( ":" uri ) /
     *                     (
     *                       ";" "ENCODING" "=" "BASE64"
     *                       ";" "VALUE" "=" "BINARY"
     *                       ":" binary
     *                     )
     *                     CRLF
     *
     *        attachparam = *(
     *                    ;
     *                    ; The following is OPTIONAL for a URI value,
     *                    ; RECOMMENDED for a BINARY value,
     *                    ; and MUST NOT occur more than once.
     *                    ;
     *                    (";" fmttypeparam) /
     *                    ;
     *                    ; The following is OPTIONAL,
     *                    ; and MAY occur more than once.
     *                    ;
     *                    (";" other-param)
     *                    ;
     *                    )
     *                    </pre>
     */
    public static final Validator<Attach> ATTACH_URI = new PropertyValidator<>(Property.ATTACH,
            new ValidationRule<>(OneOrLess, FMTTYPE));

    /**
     * @see PropertyValidator#ATTACH_URI
     */
    public static final Validator<Attach> ATTACH_BIN = new PropertyValidator<>(Property.ATTACH,
            new ValidationRule<>(OneOrLess, FMTTYPE),
            new ValidationRule<>(One, VALUE, ENCODING),
            new ValidationRule<>(One, attach -> Encoding.BASE64.equals(attach.getParameter(ENCODING)),
                    "ENCODING=BASE64 for binary attachments",ENCODING),
            BINARY_VALUE);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        attendee   = "ATTENDEE" attparam ":" cal-address CRLF
     *
     *        attparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" cutypeparam) / (";" memberparam) /
     *                   (";" roleparam) / (";" partstatparam) /
     *                   (";" rsvpparam) / (";" deltoparam) /
     *                   (";" delfromparam) / (";" sentbyparam) /
     *                   (";" cnparam) / (";" dirparam) /
     *                   (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    public static final Validator<Attendee> ATTENDEE = new PropertyValidator<>(Property.ATTENDEE,
            new ValidationRule<>(OneOrLess, CUTYPE, MEMBER, ROLE, PARTSTAT,
                    RSVP, DELEGATED_TO, DELEGATED_FROM, SENT_BY, CN, DIR, LANGUAGE, SCHEDULE_AGENT, SCHEDULE_STATUS));

    public static final Validator<BusyType> BUSY_TYPE = new PropertyValidator<>(Property.BUSYTYPE,
            new ValidationRule<>(ValueMatch, "(?i)" + String.join("|", BusyType.VALUE_BUSY,
                    BusyType.VALUE_BUSY_TENTATIVE, BusyType.VALUE_BUSY_UNAVAILABLE)));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        calscale   = "CALSCALE" calparam ":" calvalue CRLF
     *
     *        calparam   = *(";" other-param)
     *
     *        calvalue   = "GREGORIAN"
     * </pre>
     */
    public static final Validator<CalScale> CALSCALE = new PropertyValidator<>(Property.CALSCALE,
            new ValidationRule<>(ValueMatch, "(?i)" + GREGORIAN.getValue()));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        categories = "CATEGORIES" catparam ":" text *("," text)
     *                     CRLF
     *
     *        catparam   = *(
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" languageparam ) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    public static final Validator<Categories> CATEGORIES = new PropertyValidator<>(Property.CATEGORIES,
            new ValidationRule<>(OneOrLess, LANGUAGE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        class      = "CLASS" classparam ":" classvalue CRLF
     *
     *        classparam = *(";" other-param)
     *
     *        classvalue = "PUBLIC" / "PRIVATE" / "CONFIDENTIAL" / iana-token
     *                   / x-name
     *        ;Default is PUBLIC
     * </pre>
     */
    public static final Validator<Clazz> CLAZZ = new PropertyValidator<>(Property.CLASS,
            new ValidationRule<>(ValueMatch, "(?i)" + String.join("|", PUBLIC.getValue(), PRIVATE.getValue(),
                    CONFIDENTIAL.getValue())));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        comment    = "COMMENT" commparam ":" text CRLF
     *
     *        commparam  = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" altrepparam) / (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    public static final Validator<Comment> COMMENT = new PropertyValidator<>(Property.COMMENT,
            new ValidationRule<>(OneOrLess, ALTREP, LANGUAGE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        completed  = "COMPLETED" compparam ":" date-time CRLF
     *
     *        compparam  = *(";" other-param)
     * </pre>
     */
    public static final Validator<Completed> COMPLETED = new PropertyValidator<>(Property.COMPLETED);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        contact    = "CONTACT" contparam ":" text CRLF
     *
     *        contparam  = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" altrepparam) / (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    public static final Validator<Contact> CONTACT = new PropertyValidator<>(Property.CONTACT,
            new ValidationRule<>(ValidationRule.ValidationType.OneOrLess, ALTREP, LANGUAGE));

    public static final Validator<Country> COUNTRY = new PropertyValidator<>(Property.COUNTRY,
            new ValidationRule<>(OneOrLess, ABBREV));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        created    = "CREATED" creaparam ":" date-time CRLF
     *
     *        creaparam  = *(";" other-param)
     * </pre>
     */
    public static final Validator<Created> CREATED = new PropertyValidator<>(Property.CREATED);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        description = "DESCRIPTION" descparam ":" text CRLF
     *
     *        descparam   = *(
     *                    ;
     *                    ; The following are OPTIONAL,
     *                    ; but MUST NOT occur more than once.
     *                    ;
     *                    (";" altrepparam) / (";" languageparam) /
     *                    ;
     *                    ; The following is OPTIONAL,
     *                    ; and MAY occur more than once.
     *                    ;
     *                    (";" other-param)
     *                    ;
     *                    )
     * </pre>
     */
    public static final Validator<Description> DESCRIPTION = new PropertyValidator<>(Property.DESCRIPTION,
            new ValidationRule<>(OneOrLess, ALTREP, LANGUAGE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        dtend      = "DTEND" dtendparam ":" dtendval CRLF
     *
     *        dtendparam = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
     *                   (";" tzidparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        dtendval   = date-time / date
     *        ;Value MUST match value type
     * </pre>
     */
    public static final Validator<DtEnd<?>> DTEND = new PropertyValidator<>(Property.DTEND,
            Collections.singletonList(DATE_PROP_RULE_SET));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        dtstamp    = "DTSTAMP" stmparam ":" date-time CRLF
     *
     *        stmparam   = *(";" other-param)
     * </pre>
     */
    public static final Validator<DtStamp> DTSTAMP = new PropertyValidator<>(Property.DTSTAMP);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        dtstart    = "DTSTART" dtstparam ":" dtstval CRLF
     *
     *        dtstparam  = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
     *                   (";" tzidparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        dtstval    = date-time / date
     *        ;Value MUST match value type
     * </pre>
     */
    public static final Validator<DtStart<?>> DTSTART = new PropertyValidator<>(Property.DTSTART,
            Collections.singletonList(DATE_PROP_RULE_SET));

    /**
     * <pre>
     *       Format Definition:  This property is defined by the following
     *       notation:
     *
     *        due        = "DUE" dueparam ":" dueval CRLF
     *
     *        dueparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
     *                   (";" tzidparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        dueval     = date-time / date
     *        ;Value MUST match value type
     * </pre>
     */
    public static final Validator<Due<?>> DUE = new PropertyValidator<>(Property.DUE,
            Collections.singletonList(DATE_PROP_RULE_SET));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        duration   = "DURATION" durparam ":" dur-value CRLF
     *                     ;consisting of a positive duration of time.
     *
     *        durparam   = *(";" other-param)
     * </pre>
     */
    public static final Validator<Duration> DURATION = new PropertyValidator<>(Property.DURATION);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        exdate     = "EXDATE" exdtparam ":" exdtval *("," exdtval) CRLF
     *
     *        exdtparam  = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
     *                   ;
     *                   (";" tzidparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        exdtval    = date-time / date
     *        ;Value MUST match value type
     * </pre>
     */
    public static final Validator<ExDate<?>> EXDATE = new PropertyValidator<>(Property.EXDATE,
            new ValidationRule<>(OneOrLess, VALUE, Parameter.TZID),
            DATE_OR_DATETIME_VALUE);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        freebusy   = "FREEBUSY" fbparam ":" fbvalue CRLF
     *
     *        fbparam    = *(
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" fbtypeparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        fbvalue    = period *("," period)
     *        ;Time value MUST be in the UTC time format.
     * </pre>
     */
    public static final Validator<FreeBusy> FREEBUSY = new PropertyValidator<>(Property.FREEBUSY,
            new ValidationRule<>(OneOrLess, FBTYPE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        geo        = "GEO" geoparam ":" geovalue CRLF
     *
     *        geoparam   = *(";" other-param)
     *
     *        geovalue   = float ";" float
     *        ;Latitude and Longitude components
     * </pre>
     */
    public static final Validator<Geo> GEO = new PropertyValidator<>(Property.GEO);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        last-mod   = "LAST-MODIFIED" lstparam ":" date-time CRLF
     *
     *        lstparam   = *(";" other-param)
     * </pre>
     */
    public static final Validator<LastModified> LAST_MODIFIED = new PropertyValidator<LastModified>(Property.LAST_MODIFIED,
            Collections.singletonList(UTC_PROP_RULE_SET));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        location   = "LOCATION"  locparam ":" text CRLF
     *
     *        locparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" altrepparam) / (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    public static final Validator<Location> LOCATION = new PropertyValidator<>(Property.LOCATION,
            new ValidationRule<>(OneOrLess, ALTREP, LANGUAGE));

    public static final Validator<LocationType> LOCATION_TYPE = new PropertyValidator<>(Property.LOCATION_TYPE,
            new ValidationRule<>(OneOrLess, LANGUAGE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        method     = "METHOD" metparam ":" metvalue CRLF
     *
     *        metparam   = *(";" other-param)
     *
     *        metvalue   = iana-token
     * </pre>
     */
    public static final Validator<Method> METHOD = new PropertyValidator<>(Property.METHOD,
            new ValidationRule<>(ValueMatch, "(?i)" + String.join("|", Method.VALUE_PUBLISH,
                    Method.VALUE_REQUEST, Method.VALUE_REPLY, Method.VALUE_ADD, Method.VALUE_CANCEL,
                    Method.VALUE_COUNTER, Method.VALUE_DECLINECOUNTER, Method.VALUE_REFRESH)));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        organizer  = "ORGANIZER" orgparam ":"
     *                     cal-address CRLF
     *
     *        orgparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" cnparam) / (";" dirparam) / (";" sentbyparam) /
     *                   (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    public static final Validator<Organizer> ORGANIZER = new PropertyValidator<>(Property.ORGANIZER,
            new ValidationRule<>(OneOrLess, CN, DIR, SENT_BY, LANGUAGE, SCHEDULE_STATUS));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        percent = "PERCENT-COMPLETE" pctparam ":" integer CRLF
     *
     *        pctparam   = *(";" other-param)
     * </pre>
     */
    public static final Validator<PercentComplete> PERCENT_COMPLETE = new PropertyValidator<>(Property.PERCENT_COMPLETE,
            new ValidationRule<>(ValueMatch, "[0-9]{1,2}|100"));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        priority   = "PRIORITY" prioparam ":" priovalue CRLF
     *        ;Default is zero (i.e., undefined).
     *
     *        prioparam  = *(";" other-param)
     *
     *        priovalue   = integer       ;Must be in the range [0..9]
     *           ; All other values are reserved for future use.
     * </pre>
     */
    public static final Validator<Priority> PRIORITY = new PropertyValidator<>(Property.PRIORITY,
            new ValidationRule<>(ValueMatch, "[0-9]"));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        prodid     = "PRODID" pidparam ":" pidvalue CRLF
     *
     *        pidparam   = *(";" other-param)
     *
     *        pidvalue   = text
     *        ;Any text that describes the product and version
     *        ;and that is generally assured of being unique.
     * </pre>
     */
    public static final Validator<ProdId> PROD_ID = new PropertyValidator<>(Property.PRODID);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        rdate      = "RDATE" rdtparam ":" rdtval *("," rdtval) CRLF
     *
     *        rdtparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE" / "PERIOD")) /
     *                   (";" tzidparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        rdtval     = date-time / date / period
     *        ;Value MUST match value type
     * </pre>
     */
    public static final Validator<RDate<?>> RDATE = new PropertyValidator<>(Property.RDATE,
            new ValidationRule<>(OneOrLess, VALUE, Parameter.TZID),
            new ValidationRule<>(None, rdate -> {
                Optional<Value> v = rdate.getParameter(VALUE);
                return !(!v.isPresent() || Value.DATE.equals(v.get()) || Value.DATE_TIME.equals(v.get())
                        || Value.PERIOD.equals(v.get()));
            }, VALUE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        recurid    = "RECURRENCE-ID" ridparam ":" ridval CRLF
     *
     *        ridparam   = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" ("DATE-TIME" / "DATE")) /
     *                   (";" tzidparam) / (";" rangeparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        ridval     = date-time / date
     *        ;Value MUST match value type
     * </pre>
     */
    public static final Validator<RecurrenceId<?>> RECURRENCE_ID = new PropertyValidator<>(Property.RECURRENCE_ID,
            new ValidationRule<>(OneOrLess, VALUE, Parameter.TZID, RANGE),
            DATE_OR_DATETIME_VALUE);

    public static final Validator<Region> REGION = new PropertyValidator<>(Property.REGION,
            new ValidationRule<>(OneOrLess, ABBREV));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        related    = "RELATED-TO" relparam ":" text CRLF
     *
     *        relparam   = *(
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" reltypeparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    public static final Validator<RelatedTo> RELATED_TO = new PropertyValidator<>(Property.RELATED_TO,
            new ValidationRule<>(OneOrLess, RELTYPE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        repeat  = "REPEAT" repparam ":" integer CRLF
     *        ;Default is "0", zero.
     *
     *        repparam   = *(";" other-param)
     * </pre>
     */
    public static final Validator<Repeat> REPEAT = new PropertyValidator<>(Property.REPEAT,
            new ValidationRule<>(ValueMatch, "[0-9]+"));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        rstatus    = "REQUEST-STATUS" rstatparam ":"
     *                     statcode ";" statdesc [";" extdata]
     *
     *        rstatparam = *(
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     *
     *        statcode   = 1*DIGIT 1*2("." 1*DIGIT)
     *        ;Hierarchical, numeric return status code
     *
     *        statdesc   = text
     *        ;Textual status description
     *
     *        extdata    = text
     *        ;Textual exception data.  For example, the offending property
     *        ;name and value or complete property line.
     * </pre>
     */
    public static final Validator<RequestStatus> REQUEST_STATUS = new PropertyValidator<>(Property.REQUEST_STATUS,
            new ValidationRule<>(OneOrLess, LANGUAGE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        resources  = "RESOURCES" resrcparam ":" text *("," text) CRLF
     *
     *        resrcparam = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" altrepparam) / (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    public static final Validator<Resources> RESOURCES = new PropertyValidator<>(Property.RESOURCES,
            new ValidationRule<>(OneOrLess, ALTREP, LANGUAGE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        rrule      = "RRULE" rrulparam ":" recur CRLF
     *
     *        rrulparam  = *(";" other-param)
     * </pre>
     */
    public static final Validator<RRule> RRULE = new PropertyValidator<>(Property.RRULE,
            new ValidationRule<>(None, Parameter.TZID));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        seq = "SEQUENCE" seqparam ":" integer CRLF
     *        ; Default is "0"
     *
     *        seqparam   = *(";" other-param)
     * </pre>
     */
    public static final Validator<Sequence> SEQUENCE = new PropertyValidator<>(Property.SEQUENCE,
            new ValidationRule<>(ValueMatch, "[0-9]+"));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        status          = "STATUS" statparam ":" statvalue CRLF
     *
     *        statparam       = *(";" other-param)
     *
     *        statvalue       = (statvalue-event
     *                        /  statvalue-todo
     *                        /  statvalue-jour)
     *
     *        statvalue-event = "TENTATIVE"    ;Indicates event is tentative.
     *                        / "CONFIRMED"    ;Indicates event is definite.
     *                        / "CANCELLED"    ;Indicates event was cancelled.
     *        ;Status values for a "VEVENT"
     *
     *        statvalue-todo  = "NEEDS-ACTION" ;Indicates to-do needs action.
     *                        / "COMPLETED"    ;Indicates to-do completed.
     *                        / "IN-PROCESS"   ;Indicates to-do in process of.
     *                        / "CANCELLED"    ;Indicates to-do was cancelled.
     *        ;Status values for "VTODO".
     *
     *        statvalue-jour  = "DRAFT"        ;Indicates journal is draft.
     *                        / "FINAL"        ;Indicates journal is final.
     *                        / "CANCELLED"    ;Indicates journal is removed.
     *       ;Status values for "VJOURNAL".
     * </pre>
     */
    public static final Validator<Status> STATUS = new PropertyValidator<>(Property.STATUS,
        new ValidationRule<>(ValueMatch, "(?i)" + String.join("|", VEVENT_TENTATIVE.getValue(),
                VEVENT_CONFIRMED.getValue(), VEVENT_CANCELLED.getValue(),
                VTODO_NEEDS_ACTION.getValue(), VTODO_COMPLETED.getValue(),
                VTODO_IN_PROCESS.getValue(), VTODO_CANCELLED.getValue(),
                VJOURNAL_DRAFT.getValue(), VJOURNAL_FINAL.getValue(), VJOURNAL_CANCELLED.getValue())));

    public static final Validator<StructuredData> STRUCTURED_DATA = new PropertyValidator<>(Property.STRUCTURED_DATA,
            new ValidationRule<>(OneOrLess, FMTTYPE, SCHEMA));

    public static final Validator<StyledDescription> STYLED_DESCRIPTION = new PropertyValidator<>(Property.STYLED_DESCRIPTION,
            new ValidationRule<>(OneOrLess, ALTREP, FMTTYPE, LANGUAGE));

    public static final Validator<Xml> XML = new PropertyValidator<>(Property.XML,
            new ValidationRule<>(None, ENCODING, VALUE));

    public static final Validator<Xml> XML_BIN = new PropertyValidator<>(Property.XML,
            new ValidationRule<>(One, VALUE, ENCODING),
            new ValidationRule<>(One, xml -> Encoding.BASE64.equals(xml.getParameter(ENCODING)),
                    "ENCODING=BASE64 for binary attachments", ENCODING),
            BINARY_VALUE);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        summary    = "SUMMARY" summparam ":" text CRLF
     *
     *        summparam  = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" altrepparam) / (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    public static final Validator<Summary> SUMMARY = new PropertyValidator<>(Property.SUMMARY,
            new ValidationRule<>(OneOrLess, ALTREP, LANGUAGE));

    public static final Validator<Tel> TEL = new PropertyValidator<>(Property.TEL,
            new ValidationRule<>(OneOrLess, TYPE));

    /**
     * <pre>
     *       Format Definition:  This property is defined by the following
     *       notation:
     *
     *        transp     = "TRANSP" transparam ":" transvalue CRLF
     *
     *        transparam = *(";" other-param)
     *
     *        transvalue = "OPAQUE"
     *                    ;Blocks or opaque on busy time searches.
     *                    / "TRANSPARENT"
     *                    ;Transparent on busy time searches.
     *        ;Default value is OPAQUE
     * </pre>
     */
    public static final Validator<Transp> TRANSP = new PropertyValidator<>(Property.TRANSP,
            new ValidationRule<>(ValueMatch, "(?i)" + String.join("|", OPAQUE.getValue(),
                    TRANSPARENT.getValue())));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        trigger    = "TRIGGER" (trigrel / trigabs) CRLF
     *
     *        trigabs    = *(
     *                   ;
     *                   ; The following is REQUIRED,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" "DATE-TIME") /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   ) ":" date-time
     * </pre>
     */
    public static final Validator<Trigger> TRIGGER_ABS = new PropertyValidator<>(Property.TRIGGER,
            new ValidationRule<>(One, Parameter.VALUE),
            new ValidationRule<>(None, Parameter.RELATED),
            new ValidationRule<>(None, trigger -> {
                Optional<Value> v = trigger.getParameter(VALUE);
                return !(!v.isPresent() || Value.DATE_TIME.equals(v.get()));
            }, "MUST be specified as a UTC-formatted DATE-TIME:", VALUE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        trigger    = "TRIGGER" (trigrel / trigabs) CRLF
     *
     *        trigrel    = *(
     *                   ;
     *                   ; The following are OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" "VALUE" "=" "DURATION") /
     *                   (";" trigrelparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   ) ":"  dur-value
     * </pre>
     */
    public static final Validator<Trigger> TRIGGER_REL = new PropertyValidator<>(Property.TRIGGER,
            new ValidationRule<>(OneOrLess, Parameter.VALUE, Parameter.RELATED),
            new ValidationRule<>(None, trigger -> {
                Optional<Value> v = trigger.getParameter(VALUE);
                return !(!v.isPresent() || Value.DURATION.equals(v.get()));
            }, "MUST be specified as a DURATION:", VALUE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        tzid       = "TZID" tzidpropparam ":" [tzidprefix] text CRLF
     *
     *        tzidpropparam      = *(";" other-param)
     *
     *        ;tzidprefix        = "/"
     *        ; Defined previously. Just listed here for reader convenience.
     * </pre>
     */
    public static final Validator<TzId> TZID = new PropertyValidator<>(Property.TZID);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        tzname     = "TZNAME" tznparam ":" text CRLF
     *
     *        tznparam   = *(
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; but MUST NOT occur more than once.
     *                   ;
     *                   (";" languageparam) /
     *                   ;
     *                   ; The following is OPTIONAL,
     *                   ; and MAY occur more than once.
     *                   ;
     *                   (";" other-param)
     *                   ;
     *                   )
     * </pre>
     */
    public static final Validator<TzName> TZNAME = new PropertyValidator<>(Property.TZNAME,
            new ValidationRule<>(OneOrLess, LANGUAGE));

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        tzoffsetfrom       = "TZOFFSETFROM" frmparam ":" utc-offset
     *                             CRLF
     *
     *        frmparam   = *(";" other-param)
     * </pre>
     */
    public static final Validator<TzOffsetFrom> TZOFFSETFROM = new PropertyValidator<>(Property.TZOFFSETFROM);

    /**
     * <pre>
     *     Format Definition:  This property is defined by the following
     *       notation:
     *
     *        tzoffsetto = "TZOFFSETTO" toparam ":" utc-offset CRLF
     *
     *        toparam    = *(";" other-param)
     * </pre>
     */
    public static final Validator<TzOffsetTo> TZOFFSETTO = new PropertyValidator<>(Property.TZOFFSETTO);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        tzurl      = "TZURL" tzurlparam ":" uri CRLF
     *
     *        tzurlparam = *(";" other-param)
     * </pre>
     */
    public static final Validator<TzUrl> TZURL = new PropertyValidator<>(Property.TZURL);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        uid        = "UID" uidparam ":" text CRLF
     *
     *        uidparam   = *(";" other-param)
     * </pre>
     */
    public static final Validator<Uid> UID = new PropertyValidator<>(Property.UID);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        url        = "URL" urlparam ":" uri CRLF
     *
     *        urlparam   = *(";" other-param)
     * </pre>
     */
    public static final Validator<Url> URL = new PropertyValidator<>(Property.URL);

    /**
     * <pre>
     *        Format Definition:  This property is defined by the following
     *       notation:
     *
     *        version    = "VERSION" verparam ":" vervalue CRLF
     *
     *        verparam   = *(";" other-param)
     *
     *        vervalue   = "2.0"         ;This memo
     *                   / maxver
     *                   / (minver ";" maxver)
     *
     *        minver     = &lt;A IANA-registered iCalendar version identifier&gt;
     *        ;Minimum iCalendar version needed to parse the iCalendar object.
     *
     *        maxver     = &lt;A IANA-registered iCalendar version identifier&gt;
     *        ;Maximum iCalendar version needed to parse the iCalendar object.
     * </pre>
     */
    public static final Validator<Version> VERSION = new PropertyValidator<>(Property.VERSION,
            new ValidationRule<>(ValueMatch, "(?i)" + VERSION_2_0.getValue()));

    @SafeVarargs
    public PropertyValidator(String context, ValidationRule<? super T>... rules) {
        super(context, new PropertyRuleSet<>(rules));
    }

    public PropertyValidator(String context, List<PropertyRuleSet<? super T>> rulesets) {
        super(context, rulesets.toArray(PropertyRuleSet[]::new));
    }
}
