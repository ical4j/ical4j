/*
 * $Id$ [Nov
 * 5, 2004]
 * 
 * Copyright (c) 2004, Ben Fortuna All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * o Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * o Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * o Neither the name of Ben Fortuna nor the names of any other contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.URISyntaxException;
import java.text.ParseException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.util.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The default implementation of a calendar parser.
 * 
 * @author Ben Fortuna
 */
public class CalendarParserImpl implements CalendarParser {

    private static final int WORD_CHAR_START = 32;

    private static final int WORD_CHAR_END = 126;

    private static final int WHITESPACE_CHAR_START = 0;

    private static final int WHITESPACE_CHAR_END = 20;

    private static Log log = LogFactory.getLog(CalendarParserImpl.class);

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.data.CalendarParser#parse(java.io.InputStream, net.fortuna.ical4j.data.ContentHandler)
     */
    public final void parse(final InputStream in, final ContentHandler handler)
            throws IOException, ParserException {
        parse(new InputStreamReader(in), handler);
    }

    /* (non-Javadoc)
     * @see net.fortuna.ical4j.data.CalendarParser#parse(java.io.Reader, net.fortuna.ical4j.data.ContentHandler)
     */
    public final void parse(final Reader in, final ContentHandler handler)
            throws IOException, ParserException {

        StreamTokenizer tokeniser = null;

        try {
            tokeniser = new StreamTokenizer(in);
            tokeniser.resetSyntax();
            tokeniser.wordChars(WORD_CHAR_START, WORD_CHAR_END);
            tokeniser.whitespaceChars(WHITESPACE_CHAR_START,
                    WHITESPACE_CHAR_END);
            tokeniser.ordinaryChar(':');
            tokeniser.ordinaryChar(';');
            tokeniser.ordinaryChar('=');
            tokeniser.eolIsSignificant(true);
            tokeniser.whitespaceChars(0, 0);
            tokeniser.quoteChar('"');

            // BEGIN:VCALENDAR
            assertToken(tokeniser, Calendar.BEGIN);

            assertToken(tokeniser, ':');

            assertToken(tokeniser, Calendar.VCALENDAR, true);

            assertToken(tokeniser, StreamTokenizer.TT_EOL);

            handler.startCalendar();

            // parse calendar properties..
            parsePropertyList(tokeniser, handler);

            // parse components..
            parseComponentList(tokeniser, handler);

            // END:VCALENDAR
            //assertToken(tokeniser,Calendar.END);

            assertToken(tokeniser, ':');

            assertToken(tokeniser, Calendar.VCALENDAR, true);

            handler.endCalendar();
        } catch (Exception e) {

            if (e instanceof IOException) {
                throw (IOException) e;
            }
            if (e instanceof ParserException) {
                throw (ParserException) e;
            } else {
                String error = "An error ocurred during parsing";

                if (tokeniser != null) {
                    int line = tokeniser.lineno();

                    if (in instanceof UnfoldingReader) {
                        // need to take unfolded lines into account
                        line += ((UnfoldingReader) in).getLinesUnfolded();
                    }

                    error += " - line: " + line;
                }

                throw new ParserException(error, e);
            }
        }
    }

    /**
     * Parses an iCalendar property list from the specified stream tokeniser.
     * 
     * @param tokeniser
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws URISyntaxException
     * @throws ParserException
     */
    private void parsePropertyList(final StreamTokenizer tokeniser,
            final ContentHandler handler) throws IOException, ParseException,
            URISyntaxException, ParserException {

        assertToken(tokeniser, StreamTokenizer.TT_WORD);

        while (!Component.BEGIN.equals(tokeniser.sval)
                && !Component.END.equals(tokeniser.sval)) {

            parseProperty(tokeniser, handler);

            assertToken(tokeniser, StreamTokenizer.TT_WORD);
        }
    }

    /**
     * Parses an iCalendar property from the specified stream tokeniser.
     * 
     * @param tokeniser
     * @throws IOException
     * @throws ParserException
     * @throws URISyntaxException
     * @throws ParseException
     */
    private void parseProperty(final StreamTokenizer tokeniser,
            final ContentHandler handler) throws IOException, ParserException,
            URISyntaxException, ParseException {

        String name = tokeniser.sval;

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Property [" + name + "]");
        }

        handler.startProperty(name);

        parseParameterList(tokeniser, handler);

        // it appears that control tokens (ie. ':') are allowed
        // after the first instance on a line is used.. as such
        // we must continue appending to value until EOL is
        // reached..
        //assertToken(tokeniser, StreamTokenizer.TT_WORD);

        //String value = tokeniser.sval;
        StringBuffer value = new StringBuffer();

        //assertToken(tokeniser,StreamTokenizer.TT_EOL);

        int nextToken = tokeniser.nextToken();

        while (nextToken != StreamTokenizer.TT_EOL
                && nextToken != StreamTokenizer.TT_EOF) {

            if (tokeniser.ttype == StreamTokenizer.TT_WORD) {
                value.append(tokeniser.sval);
            } else if (tokeniser.ttype == '"') {
                value.append((char) tokeniser.ttype);
                value.append(tokeniser.sval);
                //                value.append((char) tokeniser.ttype);
            } else {
                value.append((char) tokeniser.ttype);
            }

            nextToken = tokeniser.nextToken();
        }

        if (nextToken == StreamTokenizer.TT_EOF) {
            throw new ParserException("Unexpected end of file at line " + tokeniser.lineno());
        }

        handler.propertyValue(StringUtils.unescape(value.toString()));
        handler.endProperty(name);
    }

    /**
     * Parses a list of iCalendar parameters by parsing the specified stream
     * tokeniser.
     * 
     * @param tokeniser
     * @throws IOException
     * @throws ParserException
     * @throws URISyntaxException
     */
    private void parseParameterList(final StreamTokenizer tokeniser,
            final ContentHandler handler) throws IOException, ParserException,
            URISyntaxException {

        while (tokeniser.nextToken() == ';') {
            parseParameter(tokeniser, handler);
        }
    }

    /**
     * @param tokeniser
     * @param handler
     * @throws IOException
     * @throws ParserException
     * @throws URISyntaxException
     */
    private void parseParameter(final StreamTokenizer tokeniser,
            final ContentHandler handler) throws IOException, ParserException,
            URISyntaxException {

        assertToken(tokeniser, StreamTokenizer.TT_WORD);

        String paramName = tokeniser.sval;

        // debugging..
        if (log.isDebugEnabled()) {
            log.debug("Parameter [" + paramName + "]");
        }

        assertToken(tokeniser, '=');

        StringBuffer paramValue = new StringBuffer();

        // preserve quote chars..
        if (tokeniser.nextToken() == '"') {
            paramValue.append('"');
            paramValue.append(tokeniser.sval);
            paramValue.append('"');
        } else {
            paramValue.append(tokeniser.sval);
        }

        handler.parameter(paramName, StringUtils
                .unescape(paramValue.toString()));
    }

    /**
     * Parses an iCalendar component list from the specified stream tokeniser.
     * 
     * @param tokeniser
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws ParserException
     */
    private void parseComponentList(final StreamTokenizer tokeniser,
            final ContentHandler handler) throws IOException, ParseException,
            URISyntaxException, ParserException {

        while (Component.BEGIN.equals(tokeniser.sval)) {
            parseComponent(tokeniser, handler);
            
            // HACK: absorb extraneous whitespace between components (KOrganizer)..
            try {
                while (true) {
                    assertToken(tokeniser, StreamTokenizer.TT_EOL);
                }
            }
            catch (ParserException pe) {
                if (log.isDebugEnabled()) {
                    log.debug("Caught exception absorbing extra whitespace", pe);
                }
            }
//            assertToken(tokeniser, StreamTokenizer.TT_WORD);
        }
    }

    /**
     * Parses an iCalendar component from the specified stream tokeniser.
     * 
     * @param tokeniser
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws ParserException
     */
    private void parseComponent(final StreamTokenizer tokeniser,
            final ContentHandler handler) throws IOException, ParseException,
            URISyntaxException, ParserException {

        assertToken(tokeniser, ':');

        assertToken(tokeniser, StreamTokenizer.TT_WORD);

        String name = tokeniser.sval;

        handler.startComponent(name);

        assertToken(tokeniser, StreamTokenizer.TT_EOL);

        parsePropertyList(tokeniser, handler);

        // a special case for VTIMEZONE component which contains
        // sub-components..
        if (Component.VTIMEZONE.equals(name)) {
            parseComponentList(tokeniser, handler);
        }
        // VEVENT/VTODO components may optionally have embedded VALARM
        // components..
        else if ((Component.VEVENT.equals(name) || Component.VTODO.equals(name))
                && Component.BEGIN.equals(tokeniser.sval)) {
            parseComponentList(tokeniser, handler);
        }

        assertToken(tokeniser, ':');

        assertToken(tokeniser, name);

        assertToken(tokeniser, StreamTokenizer.TT_EOL);

        handler.endComponent(name);
    }

    /**
     * Asserts that the next token in the stream matches the specified token.
     * 
     * @param tokeniser
     *            stream tokeniser to perform assertion on
     * @param token
     *            expected token
     * @throws IOException
     *             when unable to read from stream
     * @throws ParserException
     *             when next token in the stream does not match the expected
     *             token
     */
    private static void assertToken(final StreamTokenizer tokeniser, final int token)
            throws IOException, ParserException {

        if (tokeniser.nextToken() != token) {
            throw new ParserException("Expected [" + token + "], read ["
                    + tokeniser.ttype + "] at line " + tokeniser.lineno());
        }

        if (log.isDebugEnabled()) {
            log.debug("[" + token + "]");
        }
    }

    /**
     * Asserts that the next token in the stream matches the specified token. This method
     * is case-sensitive.
     * @param tokeniser
     * @param token
     * @throws IOException
     * @throws ParserException
     */
    private static void assertToken(final StreamTokenizer tokeniser, final String token) throws IOException, ParserException {
        assertToken(tokeniser, token, false);
    }
    
    /**
     * Asserts that the next token in the stream matches the specified token.
     * 
     * @param tokeniser
     *            stream tokeniser to perform assertion on
     * @param token
     *            expected token
     * @throws IOException
     *             when unable to read from stream
     * @throws ParserException
     *             when next token in the stream does not match the expected
     *             token
     */
    private static void assertToken(final StreamTokenizer tokeniser, final String token, final boolean ignoreCase)
            throws IOException, ParserException {

        // ensure next token is a word token..
        assertToken(tokeniser, StreamTokenizer.TT_WORD);

        if (ignoreCase) {
            if (!token.equalsIgnoreCase(tokeniser.sval)) {
                throw new ParserException("Expected [" + token + "], read ["
                        + tokeniser.sval + "] at line " + tokeniser.lineno());
            }
        }
        else if (!token.equals(tokeniser.sval)) {
            throw new ParserException("Expected [" + token + "], read ["
                    + tokeniser.sval + "] at line " + tokeniser.lineno());
        }

        if (log.isDebugEnabled()) {
            log.debug("[" + token + "]");
        }
    }
}
