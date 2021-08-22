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

import net.fortuna.ical4j.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Parses and builds an iCalendar model from an input stream. Note that this class is not thread-safe.
 *
 * @author Ben Fortuna
 *         <p/>
 *         <pre>
 *                         $Id$
 *
 *                         Created: Apr 5, 2004
 *                         </pre>
 * @version 2.0
 */
public class CalendarBuilder implements Consumer<Calendar> {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final CalendarParser parser;

    private final ContentHandler contentHandler;

    private final TimeZoneRegistry tzRegistry;

    /**
     * The calendar instance created by the builder.
     */
    private Calendar calendar;

    /**
     * Default constructor.
     */
    public CalendarBuilder() {
        this.parser = CalendarParserFactory.getInstance().get();
        this.tzRegistry = TimeZoneRegistryFactory.getInstance().createRegistry();
        this.contentHandler = new DefaultContentHandler(this, tzRegistry);
    }

    /**
     * Constructs a new calendar builder using the specified calendar parser.
     *
     * @param parser a calendar parser used to parse calendar files
     */
    public CalendarBuilder(final CalendarParser parser) {
        this.parser = parser;
        this.tzRegistry = TimeZoneRegistryFactory.getInstance().createRegistry();
        this.contentHandler = new DefaultContentHandler(this, tzRegistry);
    }

    /**
     * Constructs a new calendar builder using the specified timezone registry.
     *
     * @param tzRegistry a timezone registry to populate with discovered timezones
     */
    public CalendarBuilder(final TimeZoneRegistry tzRegistry) {
        this.parser = CalendarParserFactory.getInstance().get();
        this.tzRegistry = tzRegistry;
        this.contentHandler = new DefaultContentHandler(this, tzRegistry);
    }

    /**
     * Constructs a new instance using the specified parser and registry.
     *
     * @param parser     a calendar parser used to construct the calendar
     * @param tzRegistry a timezone registry used to retrieve {@link TimeZone}s and
     *                   register additional timezone information found
     *                   in the calendar
     */
    public CalendarBuilder(CalendarParser parser, TimeZoneRegistry tzRegistry) {
        this.parser = parser;
        this.tzRegistry = tzRegistry;
        this.contentHandler = new DefaultContentHandler(this, tzRegistry);
    }

    /**
     * @param parser                   a custom calendar parser
     * @param tzRegistry               a custom timezone registry
     */
    @Deprecated
    public CalendarBuilder(CalendarParser parser, PropertyFactoryRegistry propertyFactoryRegistry,
                           ParameterFactoryRegistry parameterFactoryRegistry, TimeZoneRegistry tzRegistry) {

        this(parser, new ContentHandlerContext().withParameterFactorySupplier(parameterFactoryRegistry)
                        .withPropertyFactorySupplier(propertyFactoryRegistry), tzRegistry);
    }

    /**
     * @param parser                   a custom calendar parser
     * @param tzRegistry               a custom timezone registry
     * @deprecated use {@link CalendarBuilder#CalendarBuilder(CalendarParser, ContentHandlerContext, TimeZoneRegistry)}
     */
    @Deprecated
    public CalendarBuilder(CalendarParser parser, Supplier<List<ParameterFactory<?>>> parameterFactorySupplier,
                           Supplier<List<PropertyFactory<?>>> propertyFactorySupplier,
                           Supplier<List<ComponentFactory<?>>> componentFactorySupplier,
                           TimeZoneRegistry tzRegistry) {

        this(parser, new ContentHandlerContext().withParameterFactorySupplier(parameterFactorySupplier)
                .withPropertyFactorySupplier(propertyFactorySupplier)
                .withComponentFactorySupplier(componentFactorySupplier), tzRegistry);
    }

    /**
     * @param parser                   a custom calendar parser
     * @param tzRegistry               a custom timezone registry
     */
    public CalendarBuilder(CalendarParser parser, ContentHandlerContext contentHandlerContext,
                           TimeZoneRegistry tzRegistry) {

        this.parser = parser;
        this.tzRegistry = tzRegistry;
        this.contentHandler = new DefaultContentHandler(this, tzRegistry, contentHandlerContext);
    }

    @Override
    public void accept(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * Builds an iCalendar model from the specified input stream.
     *
     * @param in an input stream to read calendar data from
     * @return a calendar parsed from the specified input stream
     * @throws IOException     where an error occurs reading data from the specified stream
     * @throws ParserException where an error occurs parsing data from the stream
     */
    public Calendar build(final InputStream in) throws IOException, ParserException {
        return build(new InputStreamReader(in, DEFAULT_CHARSET));
    }

    /**
     * Builds an iCalendar model from the specified reader. An <code>UnfoldingReader</code> is applied to the
     * specified reader to ensure the data stream is correctly unfolded where appropriate.
     *
     * @param in a reader to read calendar data from
     * @return a calendar parsed from the specified reader
     * @throws IOException     where an error occurs reading data from the specified reader
     * @throws ParserException where an error occurs parsing data from the reader
     */
    public Calendar build(final Reader in) throws IOException, ParserException {
        return build(new UnfoldingReader(in));
    }

    /**
     * Build an iCalendar model by parsing data from the specified reader.
     *
     * @param uin an unfolding reader to read data from
     * @return a calendar parsed from the specified reader
     * @throws IOException     where an error occurs reading data from the specified reader
     * @throws ParserException where an error occurs parsing data from the reader
     */
    public Calendar build(final UnfoldingReader uin) throws IOException, ParserException {
        parser.parse(uin, contentHandler);
        return calendar;
    }

    /**
     * Returns the timezone registry used in the construction of calendars.
     *
     * @return a timezone registry
     */
    public final TimeZoneRegistry getRegistry() {
        return tzRegistry;
    }
}
