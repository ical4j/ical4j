/*
 * $Id$ [Apr 5, 2004]
 *
 * Copyright (c) 2004, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	o Neither the name of Ben Fortuna nor the names of any other contributors
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
package net.fortuna.ical4j.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.URISyntaxException;
import java.text.ParseException;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parses and builds an iCalendar model from an input stream.
 *
 * @author benf
 */
public class CalendarBuilder {

    private static final int WORD_CHAR_START = 32;

    private static final int WORD_CHAR_END = 126;

    private static final int WHITESPACE_CHAR_START = 0;

    private static final int WHITESPACE_CHAR_END = 20;

    private static Log log = LogFactory.getLog(CalendarBuilder.class);

    /**
     * Builds an iCalendar model from the specified input stream.
     *
     * @param in
     * @return a calendar
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws BuilderException
     */
    public final Calendar build(final InputStream in) throws IOException,
            BuilderException {

        return build(new InputStreamReader(in));
    }

    /**
     * Builds an iCalendar model from the specified reader.
     * An <code>UnfoldingReader</code> is applied to the specified
     * reader to ensure the data stream is correctly unfolded where
     * appropriate.
     *
     * @param in
     * @return a calendar
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws BuilderException
     */
    public final Calendar build(final Reader in) throws IOException,
            BuilderException {

        try {
            UnfoldingReader uin = new UnfoldingReader(in);

            StreamTokenizer tokeniser = new StreamTokenizer(uin);
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

            PropertyList properties = null;
            ComponentList components = null;

            // BEGIN:VCALENDAR
            assertToken(tokeniser, Calendar.BEGIN);

            assertToken(tokeniser, ':');

            assertToken(tokeniser, Calendar.VCALENDAR);

            assertToken(tokeniser, StreamTokenizer.TT_EOL);

            // build calendar properties..
            properties = buildPropertyList(tokeniser);

            // build components..
            components = buildComponentList(tokeniser);

            // END:VCALENDAR
            //assertToken(tokeniser,Calendar.END);

            assertToken(tokeniser, ':');

            assertToken(tokeniser, Calendar.VCALENDAR);

            // construct calendar instance..
            return new Calendar(properties, components);
        }
        catch (Exception e) {

            if (e instanceof IOException) { throw (IOException) e; }
            if (e instanceof BuilderException) {
                throw (BuilderException) e;
            }
            else {
                throw new BuilderException("An error ocurred during parsing", e);
            }
        }
    }

    /**
     * Builds am iCalendar property list from the specified stream tokeniser.
     *
     * @param tokeniser
     * @return a property list
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws URISyntaxException
     * @throws BuilderException
     */
    private PropertyList buildPropertyList(final StreamTokenizer tokeniser)
            throws IOException, ParseException, URISyntaxException,
            BuilderException {

        PropertyList list = new PropertyList();

        assertToken(tokeniser, StreamTokenizer.TT_WORD);

        while (!Component.BEGIN.equals(tokeniser.sval)
                && !Component.END.equals(tokeniser.sval)) {

            list.add(buildProperty(tokeniser));

            assertToken(tokeniser, StreamTokenizer.TT_WORD);
        }

        return list;
    }

    /**
     * Builds an iCalendar property from the specified stream tokeniser.
     *
     * @param tokeniser
     * @return a property
     * @throws IOException
     * @throws BuilderException
     * @throws URISyntaxException
     * @throws ParseException
     */
    private Property buildProperty(final StreamTokenizer tokeniser)
            throws IOException, BuilderException, URISyntaxException,
            ParseException {

        String name = tokeniser.sval;

        // debugging..
        log.debug("Property [" + name + "]");

        ParameterList parameters = buildParameterList(tokeniser);

        // it appears that control tokens (ie. ':') are allowed
        // after the first instance on a line is used.. as such
        // we must continue appending to value until EOL is
        // reached..
        //assertToken(tokeniser, StreamTokenizer.TT_WORD);

        //String value = tokeniser.sval;
        StringBuffer value = new StringBuffer();

        //assertToken(tokeniser,StreamTokenizer.TT_EOL);

        while (tokeniser.nextToken() != StreamTokenizer.TT_EOL) {

            if (tokeniser.ttype == StreamTokenizer.TT_WORD) {
                value.append(tokeniser.sval);
            }
            else if (tokeniser.ttype == '"') {
                value.append((char) tokeniser.ttype);
                value.append(tokeniser.sval);
//                value.append((char) tokeniser.ttype);
            }
            else {
                value.append((char) tokeniser.ttype);
            }
        }

        return PropertyFactory.getInstance().createProperty(name, parameters,
                StringUtils.unescape(value.toString()));
    }

    /**
     * Build a list of iCalendar parameters by parsing the specified stream
     * tokeniser.
     *
     * @param tokeniser
     * @return @throws
     *         IOException
     * @throws BuilderException
     * @throws URISyntaxException
     * @throws ParseException
     */
    private ParameterList buildParameterList(final StreamTokenizer tokeniser)
            throws IOException, BuilderException, URISyntaxException,
            ParseException {

        ParameterList parameters = new ParameterList();

        while (tokeniser.nextToken() == ';') {
            parameters.add(buildParameter(tokeniser));
        }

        return parameters;
    }

    private Parameter buildParameter(final StreamTokenizer tokeniser)
            throws IOException, BuilderException, URISyntaxException,
            ParseException {

        assertToken(tokeniser, StreamTokenizer.TT_WORD);

        String paramName = tokeniser.sval;

        // debugging..
        log.debug("Parameter [" + paramName + "]");

        assertToken(tokeniser, '=');

        StringBuffer paramValue = new StringBuffer();

        // preserve quote chars..
        if (tokeniser.nextToken() == '"') {
            paramValue.append('"');
            paramValue.append(tokeniser.sval);
            paramValue.append('"');
        }
        else {
            paramValue.append(tokeniser.sval);
        }

        return ParameterFactory.getInstance().createParameter(paramName,
                StringUtils.unescape(paramValue.toString()));
    }

    /**
     * Builds an iCalendar component list from the specified stream tokeniser.
     *
     * @param tokeniser
     * @return a component list
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws BuilderException
     */
    private ComponentList buildComponentList(final StreamTokenizer tokeniser)
            throws IOException, ParseException, URISyntaxException,
            BuilderException {

        ComponentList list = new ComponentList();

        while (Component.BEGIN.equals(tokeniser.sval)) {

            list.add(buildComponent(tokeniser));

            assertToken(tokeniser, StreamTokenizer.TT_WORD);
        }

        return list;
    }

    /**
     * Builds an iCalendar component from the specified stream tokeniser.
     *
     * @param tokeniser
     * @return a component
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     * @throws BuilderException
     */
    private Component buildComponent(final StreamTokenizer tokeniser)
            throws IOException, ParseException, URISyntaxException,
            BuilderException {

        assertToken(tokeniser, ':');

        assertToken(tokeniser, StreamTokenizer.TT_WORD);

        String name = tokeniser.sval;

        assertToken(tokeniser, StreamTokenizer.TT_EOL);

        PropertyList properties = buildPropertyList(tokeniser);

        ComponentList subComponents = null;

        // a special case for VTIMEZONE component which contains
        // sub-components..
        if (Component.VTIMEZONE.equals(name)) {

            subComponents = buildComponentList(tokeniser);
        }
        // VEVENT components may optionally have embedded VALARM
        // components..
        else if (Component.VEVENT.equals(name)
                && Component.BEGIN.equals(tokeniser.sval)) {

            subComponents = buildComponentList(tokeniser);
        }

        assertToken(tokeniser, ':');

        assertToken(tokeniser, name);

        assertToken(tokeniser, StreamTokenizer.TT_EOL);

        return ComponentFactory.getInstance().createComponent(name, properties,
                subComponents);
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
     * @throws ParseException
     *             when next token in the stream does not match the expected
     *             token
     */
    private void assertToken(final StreamTokenizer tokeniser, final int token)
            throws IOException, BuilderException, ParseException {

        if (tokeniser.nextToken() != token) {

        throw new BuilderException("Expected [" + token + "], read ["
                + tokeniser.ttype + "] at line " + tokeniser.lineno()); }

        log.debug("[" + token + "]");
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
     * @throws ParseException
     *             when next token in the stream does not match the expected
     *             token
     */
    private void assertToken(final StreamTokenizer tokeniser, final String token)
            throws IOException, ParseException, BuilderException {

        // ensure next token is a word token..
        assertToken(tokeniser, StreamTokenizer.TT_WORD);

        if (!token.equals(tokeniser.sval)) {

        throw new BuilderException("Expected [" + token + "], read ["
                + tokeniser.sval + "] at line " + tokeniser.lineno()); }

        log.debug("[" + token + "]");
    }
}