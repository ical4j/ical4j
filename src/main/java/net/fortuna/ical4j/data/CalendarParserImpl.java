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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.ParseException;

/**
 * <pre>
 * $Id$
 *
 *  Created [Nov 5, 2004]
 * </pre>
 * <p/>
 * The default implementation of a calendar parser.
 *
 * @author Ben Fortuna
 */
public class CalendarParserImpl implements CalendarParser {
	private static final int IGNORE_BEGINNING_NON_WORD_COUNT = 10;
	
    private static final int WORD_CHAR_START = 32;

    private static final int WORD_CHAR_END = 255;

    private static final int WHITESPACE_CHAR_START = 0;

    private static final int WHITESPACE_CHAR_END = 20;

    private static final String UNEXPECTED_TOKEN_MESSAGE = "Expected [{0}], read [{1}]";

    private Logger log = LoggerFactory.getLogger(CalendarParserImpl.class);

    private final ComponentListParser componentListParser = new ComponentListParser();

    private final ComponentParser componentParser = new ComponentParser();

    private final PropertyListParser propertyListParser = new PropertyListParser();

    private final PropertyParser propertyParser = new PropertyParser();

    private final ParameterListParser paramListParser = new ParameterListParser();

    private final ParameterParser paramParser = new ParameterParser();

    private final boolean absorbWhitespaceEnabled;

    public CalendarParserImpl() {
        this(CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING));
    }

    public CalendarParserImpl(boolean absorbWhitespaceEnabled) {
        this.absorbWhitespaceEnabled = absorbWhitespaceEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void parse(final InputStream in, final ContentHandler handler)
            throws IOException, ParserException {
        parse(new InputStreamReader(in), handler);
    }

    /**
     * Parses an iCalendar VCALENDAR from the specified stream tokeniser.
     * 
     * @param tokeniser
     * @param in
     * @param handler
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws ParserException
     */
    private void parseCalendar(final StreamTokenizer tokeniser, Reader in,
            final ContentHandler handler) throws IOException, ParseException,
            URISyntaxException, ParserException {

        assertToken(tokeniser, in, ':');

        assertToken(tokeniser, in, Calendar.VCALENDAR, true, false);

        assertToken(tokeniser, in, StreamTokenizer.TT_EOL);

        handler.startCalendar();

        absorbWhitespace(tokeniser, in);

        // parse calendar properties..
        propertyListParser.parse(tokeniser, in, handler);

        // parse components..
        componentListParser.parse(tokeniser, in, handler);

        // END:VCALENDAR
        // assertToken(tokeniser,Calendar.END);

        assertToken(tokeniser, in, ':');

        assertToken(tokeniser, in, Calendar.VCALENDAR, true, false);

        handler.endCalendar();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final void parse(final Reader in, final ContentHandler handler)
            throws IOException, ParserException {

        final StreamTokenizer tokeniser = new StreamTokenizer(in);
        try {
            tokeniser.resetSyntax();
            tokeniser.wordChars(WORD_CHAR_START, WORD_CHAR_END);
            tokeniser.whitespaceChars(WHITESPACE_CHAR_START,
                    WHITESPACE_CHAR_END);
            tokeniser.ordinaryChar(':');
            tokeniser.ordinaryChar(';');
            tokeniser.ordinaryChar('=');
            tokeniser.ordinaryChar('\t');
            tokeniser.eolIsSignificant(true);
            tokeniser.whitespaceChars(0, 0);
            tokeniser.quoteChar('"');

            parseCalendarList(tokeniser, in, handler);
        } catch (IOException | ParseException | URISyntaxException | RuntimeException e) {

            if (e instanceof IOException) {
                throw (IOException) e;
            }
            if (e instanceof ParserException) {
                throw (ParserException) e;
            } else {
                throw new ParserException(e.getMessage(), getLineNumber(tokeniser, in), e);
            }
        }
    }

    /**
     * Parses multiple VCALENDARs from the specified stream tokeniser.
     * 
     * @param tokeniser
     * @param handler
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws ParserException
     */
    private void parseCalendarList(final StreamTokenizer tokeniser, Reader in, 
            final ContentHandler handler) throws IOException, ParseException,
            URISyntaxException, ParserException {

        // BEGIN:VCALENDAR
        int ntok = assertToken(tokeniser, in, Calendar.BEGIN, false, true);
        while (ntok != StreamTokenizer.TT_EOF) {
            parseCalendar(tokeniser, in, handler);
            absorbWhitespace(tokeniser, in);
            ntok = nextToken(tokeniser, in, true);
        }
    }

    /**
     * Parses an iCalendar property list from the specified stream tokeniser.
     */
    private class PropertyListParser {

        /**
         * @param tokeniser
         * @throws IOException
         * @throws ParseException
         * @throws URISyntaxException
         * @throws URISyntaxException
         * @throws ParserException
         */
        public void parse(final StreamTokenizer tokeniser, Reader in,
                          final ContentHandler handler) throws IOException, ParseException,
                URISyntaxException, ParserException {

            assertToken(tokeniser, in, StreamTokenizer.TT_WORD);

            while (/*
                     * !Component.BEGIN.equals(tokeniser.sval) &&
                     */!Component.END.equals(tokeniser.sval)) {
                // check for timezones observances or vevent/vtodo alarms..
                if (Component.BEGIN.equals(tokeniser.sval)) {
                    componentParser.parse(tokeniser, in, handler);
                } else if (tokeniser.sval != null) {
                    propertyParser.parse(tokeniser, in, handler);
                } else if (absorbWhitespaceEnabled) {
                    absorbWhitespace(tokeniser, in);
                } else {
                    throw new ParserException("Invalid property name", getLineNumber(tokeniser, in));
                }
                nextToken(tokeniser, in, false);
                // assertToken(tokeniser, StreamTokenizer.TT_WORD);
            }
        }
    }

    /**
     * Parses an iCalendar property from the specified stream tokeniser.
     */
    private class PropertyParser {

        private static final String PARSE_DEBUG_MESSAGE = "Property [{0}]";

        private static final String PARSE_EXCEPTION_MESSAGE = "Property [{0}]";

        /**
         * @param tokeniser
         * @throws IOException
         * @throws ParserException
         * @throws URISyntaxException
         * @throws ParseException
         */
        private void parse(final StreamTokenizer tokeniser, Reader in,
                           final ContentHandler handler) throws IOException, ParserException,
                URISyntaxException, ParseException {

            final String name = tokeniser.sval;
            // debugging..
            if (log.isDebugEnabled()) {
                log.debug(MessageFormat.format(PARSE_DEBUG_MESSAGE, name));
            }

            handler.startProperty(name);

            paramListParser.parse(tokeniser, in, handler);

            // it appears that control tokens (ie. ':') are allowed
            // after the first instance on a line is used.. as such
            // we must continue appending to value until EOL is
            // reached..
            // assertToken(tokeniser, StreamTokenizer.TT_WORD);

            // String value = tokeniser.sval;
            final StringBuilder value = new StringBuilder();

            // assertToken(tokeniser,StreamTokenizer.TT_EOL);

            // DQUOTE is ordinary char for property value
            // From sec 4.3.11 of rfc-2445:
            // text       = *(TSAFE-CHAR / ":" / DQUOTE / ESCAPED-CHAR)
            //
            tokeniser.ordinaryChar('"');
            int nextToken = nextToken(tokeniser, in);

            while (nextToken != StreamTokenizer.TT_EOL) {

                if (tokeniser.ttype == StreamTokenizer.TT_WORD) {
                    value.append(tokeniser.sval);
                } else {
                    value.append((char) tokeniser.ttype);
                }

                nextToken = nextToken(tokeniser, in);
            }

            // reset DQUOTE to be quote char
            tokeniser.quoteChar('"');

            try {
                handler.propertyValue(value.toString());
            } catch (ParseException e) {
                final ParseException eNew = new ParseException("[" + name + "] "
                        + e.getMessage(), e.getErrorOffset());
                eNew.initCause(e);
                throw eNew;
            }

            handler.endProperty(name);

        }
    }

    /**
     * Parses a list of iCalendar parameters by parsing the specified stream tokeniser.
     */
    private class ParameterListParser {

        /**
         * @param tokeniser
         * @throws IOException
         * @throws ParserException
         * @throws URISyntaxException
         */
        public void parse(final StreamTokenizer tokeniser, Reader in,
                          final ContentHandler handler) throws IOException, ParserException,
                URISyntaxException {

            while (nextToken(tokeniser, in) == ';') {
                paramParser.parse(tokeniser, in, handler);
            }
        }
    }

    private class ParameterParser {

        /**
         * @param tokeniser
         * @param handler
         * @throws IOException
         * @throws ParserException
         * @throws URISyntaxException
         */
        private void parse(final StreamTokenizer tokeniser, Reader in,
                           final ContentHandler handler) throws IOException, ParserException,
                URISyntaxException {

            assertToken(tokeniser, in, StreamTokenizer.TT_WORD);

            final String paramName = tokeniser.sval;

            // debugging..
            if (log.isDebugEnabled()) {
                log.debug("Parameter [" + paramName + "]");
            }

            assertToken(tokeniser, in, '=');

            final StringBuilder paramValue = new StringBuilder();

            // preserve quote chars..
            if (nextToken(tokeniser, in) == '"') {
                paramValue.append('"');
                paramValue.append(tokeniser.sval);
                paramValue.append('"');
            } else if (tokeniser.sval != null) {
                paramValue.append(tokeniser.sval);
                // check for additional words to account for equals (=) in param-value
                int nextToken = nextToken(tokeniser, in);

                while (nextToken != ';' && nextToken != ':' && nextToken != ',') {

                    if (tokeniser.ttype == StreamTokenizer.TT_WORD) {
                        paramValue.append(tokeniser.sval);
                    } else {
                        paramValue.append((char) tokeniser.ttype);
                    }

                    nextToken = nextToken(tokeniser, in);
                }
                tokeniser.pushBack();
            } else if (tokeniser.sval == null) {
                tokeniser.pushBack();
            }

            try {
                handler.parameter(paramName, paramValue.toString());
            } catch (ClassCastException cce) {
                throw new ParserException("Error parsing parameter", getLineNumber(tokeniser, in), cce);
            }
        }
    }

    /**
     * Parses an iCalendar component list from the specified stream tokeniser.
     */
    private class ComponentListParser {

        /**
         * @param tokeniser
         * @throws IOException
         * @throws ParseException
         * @throws URISyntaxException
         * @throws ParserException
         */
        private void parse(final StreamTokenizer tokeniser, Reader in,
                           final ContentHandler handler) throws IOException, ParseException,
                URISyntaxException, ParserException {

            while (Component.BEGIN.equals(tokeniser.sval)) {
                componentParser.parse(tokeniser, in, handler);
                absorbWhitespace(tokeniser, in);
                nextToken(tokeniser, in, false);
                // assertToken(tokeniser, StreamTokenizer.TT_WORD);
            }
        }
    }

    /**
     * Parses an iCalendar component from the specified stream tokeniser.
     */
    private class ComponentParser {

        /**
         * @param tokeniser
         * @throws IOException
         * @throws ParseException
         * @throws URISyntaxException
         * @throws ParserException
         */
        private void parse(final StreamTokenizer tokeniser, Reader in,
                           final ContentHandler handler) throws IOException, ParseException,
                URISyntaxException, ParserException {

            assertToken(tokeniser, in, ':');

            assertToken(tokeniser, in, StreamTokenizer.TT_WORD);

            final String name = tokeniser.sval;

            handler.startComponent(name);

            assertToken(tokeniser, in, StreamTokenizer.TT_EOL);
            absorbWhitespace(tokeniser, in);

            propertyListParser.parse(tokeniser, in, handler);

            /*
             * // a special case for VTIMEZONE component which contains
             * // sub-components.. 
             * if (Component.VTIMEZONE.equals(name)) {
             *     parseComponentList(tokeniser, handler);
             * }
             * // VEVENT/VTODO components may optionally have embedded VALARM
             * // components.. 
             * else if ((Component.VEVENT.equals(name) || Component.VTODO.equals(name))
             *         &amp;&amp; Component.BEGIN.equals(tokeniser.sval)) {
             *     parseComponentList(tokeniser, handler);
             * }
             */

            assertToken(tokeniser, in, ':');

            assertToken(tokeniser, in, name);

            assertToken(tokeniser, in, StreamTokenizer.TT_EOL);

            handler.endComponent(name);
        }
    }

    /**
     * Asserts that the next token in the stream matches the specified token.
     *
     * @param tokeniser stream tokeniser to perform assertion on
     * @param token     expected token
     * @return int value of the ttype field of the tokeniser
     * @throws IOException     when unable to read from stream
     * @throws ParserException when next token in the stream does not match the expected token
     */
    private int assertToken(final StreamTokenizer tokeniser, Reader in, final int token)
            throws IOException, ParserException {

        int ntok = nextToken(tokeniser, in);
        if (ntok != token) {
            throw new ParserException(MessageFormat.format(UNEXPECTED_TOKEN_MESSAGE, token, tokeniser.ttype), getLineNumber(tokeniser, in));
        }

        if (log.isDebugEnabled()) {
            log.debug("[" + token + "]");
        }
        return ntok;
    }

    /**
     * Asserts that the next token in the stream matches the specified token. This method is case-sensitive.
     *
     * @param tokeniser
     * @param token
     * @return int value of the ttype field of the tokeniser
     * @throws IOException
     * @throws ParserException
     */
    private int assertToken(final StreamTokenizer tokeniser, Reader in, final String token)
            throws IOException, ParserException {
        return assertToken(tokeniser, in, token, false, false);
    }

    /**
     * Asserts that the next token in the stream matches the specified token.
     *
     * @param tokeniser stream tokeniser to perform assertion on
     * @param in
     * @param token     expected token
     * @param ignoreCase
     * @param isBeginToken
     * @return int value of the ttype field of the tokeniser
     * @throws IOException     when unable to read from stream
     * @throws ParserException when next token in the stream does not match the expected token
     */
    private int assertToken(final StreamTokenizer tokeniser, Reader in,
            final String token, final boolean ignoreCase, final boolean isBeginToken) throws IOException,
            ParserException {

        // ensure next token is a word token..
        String sval;
        int ntok;
        if(isBeginToken) {
            ntok = skipNewLines(tokeniser, in, token);
            sval = getSvalIgnoringBom(tokeniser, in, token);
        } else {
            ntok = assertToken(tokeniser, in, StreamTokenizer.TT_WORD);
            sval = tokeniser.sval;
        }

        if (ignoreCase) {
            if (!token.equalsIgnoreCase(sval)) {
                throw new ParserException(MessageFormat.format(UNEXPECTED_TOKEN_MESSAGE, token, sval), getLineNumber(tokeniser, in));
            }
        }
        else if (!token.equals(sval)) {
            throw new ParserException(MessageFormat.format(UNEXPECTED_TOKEN_MESSAGE, token, sval), getLineNumber(tokeniser, in));
        }

        if (log.isDebugEnabled()) {
            log.debug("[" + token + "]");
        }
        return ntok;
    }

    /**
     * Skip newlines and linefeed at the beginning.
     * 
     * @param tokeniser
     * @param in
     * @param token
     * @return int value of the ttype field of the tokeniser
     * @throws ParserException
     * @throws IOException
     */
    private int skipNewLines(StreamTokenizer tokeniser, Reader in, String token) throws ParserException, IOException {
        for (int i = 0;; i++) {
            try {
                return assertToken(tokeniser, in, StreamTokenizer.TT_WORD);
            } catch (ParserException exc) {
                //Skip a maximum of 10 newlines, linefeeds etc at the beginning
                if (i == IGNORE_BEGINNING_NON_WORD_COUNT) {
                    throw exc;
                }
            }
        }
    }

    /**
     * Ignore BOM character.
     * 
     * @param tokeniser
     * @param in
     * @param token
     * @return
     */
    private String getSvalIgnoringBom(StreamTokenizer tokeniser, Reader in, String token) {
        if(tokeniser.sval != null) {
            if(tokeniser.sval.contains(token)) {
                return token;
            }
        }
        return tokeniser.sval;
    }

    /**
     * Absorbs extraneous newlines.
     *
     * @param tokeniser
     * @param in
     * @return int value of the ttype field of the tokeniser
     * @throws IOException
     */
    private void absorbWhitespace(final StreamTokenizer tokeniser, Reader in) throws IOException, ParserException {
        while (nextToken(tokeniser, in, true) == StreamTokenizer.TT_EOL) {
            if (log.isTraceEnabled()) {
                log.trace("Absorbing extra whitespace..");
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("Aborting: absorbing extra whitespace complete");
        }
        /* In effect, we only want to absorb extra newlines after the current
         * token, and not absorb the current token, even if it is a newline.
         */
        tokeniser.pushBack();
    }

    /**
     * @param tokeniser
     * @param in
     * @return int value of the ttype field of the tokeniser
     */
    private int getLineNumber(StreamTokenizer tokeniser, Reader in) {
        int line = tokeniser.lineno();
        if (tokeniser.ttype == StreamTokenizer.TT_EOL) {
            line -= 1;
        }
        if (in instanceof UnfoldingReader) {
            // need to take unfolded lines into account
            final int unfolded = ((UnfoldingReader) in).getLinesUnfolded();
            line += unfolded;
        }
        return line;
    }

    /**
     * 
     * @param tokeniser
     * @param in
     * @return int value of the ttype field of the tokeniser
     * @throws IOException
     * @throws ParserException
     */
    private int nextToken(StreamTokenizer tokeniser, Reader in) throws IOException, ParserException {
        return nextToken(tokeniser, in, false);
    }

    /**
     * Reads the next token from the tokeniser.
     * This method throws a ParseException when reading EOF.
     *
     * @param tokeniser
     * @param in
     * @param ignoreEOF
     * @return int value of the ttype field of the tokeniser
     * @throws ParseException When reading EOF.
     */
    private int nextToken(StreamTokenizer tokeniser, Reader in, boolean ignoreEOF) throws IOException, ParserException {
        int token = tokeniser.nextToken();
        if (!ignoreEOF && token == StreamTokenizer.TT_EOF) {
            throw new ParserException("Unexpected end of file", getLineNumber(tokeniser, in));
        }
        return token;
    }
}
