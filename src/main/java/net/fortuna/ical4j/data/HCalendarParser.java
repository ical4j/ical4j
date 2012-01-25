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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import net.fortuna.ical4j.model.CalendarException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A {@link CalendarParser} that parses XHTML documents that include calendar data marked up with the hCalendar
 * microformat.
 * <p>
 * The parser treats the entire document as a single "vcalendar" context, ignoring any <code>vcalendar</code> elements
 * and adding all components in the document to a single generated calendar.
 * </p>
 * <p>
 * Since hCalendar does not include product information, the <code>PRODID</code> property is omitted from the generated
 * calendar. The hCalendar profile is supposed to define the iCalendar version that it represents, but it does not, so
 * version 2.0 is assumed.
 * </p>
 * <h3>Supported Components</h3>
 * <p>
 * This parser recognizes only "vevent" components.
 * </p>
 * <h3>Supported Properties</h3>
 * <p>
 * This parser recognizes the following properties:
 * </p>
 * <ul>
 * <li>"dtstart"</li>
 * <li>"dtend"</li>
 * <li>"duration"</li>
 * <li>"summary"</li>
 * <li>"uid"</li>
 * <li>"dtstamp"</li>
 * <li>"category"</li>
 * <li>"location"</li>
 * <li>"url"</li>
 * <li>"description"</li>
 * <li>"last-modified"</li>
 * <li>"status"</li>
 * <li>"class"</li>
 * <li>"attendee"</li>
 * <li>"contact"</li>
 * <li>"organizer"</li>
 * </ul>
 * <p>
 * hCalendar allows for some properties to be represented by nested microformat records, including hCard, adr and geo.
 * This parser does not recognize these records. It simply accumulates the text content of any child elements of the
 * property element and uses the resulting string as the property value.
 * </p>
 * <h4>Date and Date-Time Properties</h4>
 * <p>
 * hCalendar date-time values are formatted according to RFC 3339. There is no representation in this specification for
 * time zone ids. All date-times are specified either in UTC or with an offset that can be used to convert the local
 * time into UTC. Neither does hCal provide a reprsentation for floating date-times. Therefore, all date-time values
 * produced by this parser are in UTC.
 * </p>
 * <p>
 * Some examples in the wild provide date and date-time values in iCalendar format rather than RFC 3339 format. Although
 * not technically legal according to spec, these values are accepted. In this case, floating date-times are produced by
 * the parser.
 * </p>
 * <h3>Supported Parameters</h3>
 * <p>
 * hCalendar does not define attributes, nested elements or other information elements representing parameter data.
 * Therefore, this parser does not set any property parameters except as implied by property value data (e.g.
 * VALUE=DATE-TIME or VALUE=DATE for date-time properties).
 * </p>
 */
public class HCalendarParser implements CalendarParser {
    
    private static final Log LOG = LogFactory.getLog(HCalendarParser.class);
    
    private static final DocumentBuilderFactory BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();
    private static final XPathExpression XPATH_METHOD;
    private static final XPathExpression XPATH_VEVENTS;
    private static final XPathExpression XPATH_DTSTART;
    private static final XPathExpression XPATH_DTEND;
    private static final XPathExpression XPATH_DURATION;
    private static final XPathExpression XPATH_SUMMARY;
    private static final XPathExpression XPATH_UID;
    private static final XPathExpression XPATH_DTSTAMP;
    private static final XPathExpression XPATH_CATEGORY;
    private static final XPathExpression XPATH_LOCATION;
    private static final XPathExpression XPATH_URL;
    private static final XPathExpression XPATH_DESCRIPTION;
    private static final XPathExpression XPATH_LAST_MODIFIED;
    private static final XPathExpression XPATH_STATUS;
    private static final XPathExpression XPATH_CLASS;
    private static final XPathExpression XPATH_ATTENDEE;
    private static final XPathExpression XPATH_CONTACT;
    private static final XPathExpression XPATH_ORGANIZER;
    private static final XPathExpression XPATH_SEQUENCE;
    private static final XPathExpression XPATH_ATTACH;
    private static final String HCAL_DATE_PATTERN = "yyyy-MM-dd";
    private static final SimpleDateFormat HCAL_DATE_FORMAT = new SimpleDateFormat(HCAL_DATE_PATTERN);
    private static final String HCAL_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssz";
    private static final SimpleDateFormat HCAL_DATE_TIME_FORMAT = new SimpleDateFormat(HCAL_DATE_TIME_PATTERN);

    static {
        BUILDER_FACTORY.setNamespaceAware(true);
        BUILDER_FACTORY.setIgnoringComments(true);

        XPATH_METHOD = compileExpression("//*[contains(@class, 'method')]");
        XPATH_VEVENTS = compileExpression("//*[contains(@class, 'vevent')]");
        XPATH_DTSTART = compileExpression(".//*[contains(@class, 'dtstart')]");
        XPATH_DTEND = compileExpression(".//*[contains(@class, 'dtend')]");
        XPATH_DURATION = compileExpression(".//*[contains(@class, 'duration')]");
        XPATH_SUMMARY = compileExpression(".//*[contains(@class, 'summary')]");
        XPATH_UID = compileExpression(".//*[contains(@class, 'uid')]");
        XPATH_DTSTAMP = compileExpression(".//*[contains(@class, 'dtstamp')]");
        XPATH_CATEGORY = compileExpression(".//*[contains(@class, 'category')]");
        XPATH_LOCATION = compileExpression(".//*[contains(@class, 'location')]");
        XPATH_URL = compileExpression(".//*[contains(@class, 'url')]");
        XPATH_DESCRIPTION = compileExpression(".//*[contains(@class, 'description')]");
        XPATH_LAST_MODIFIED = compileExpression(".//*[contains(@class, 'last-modified')]");
        XPATH_STATUS = compileExpression(".//*[contains(@class, 'status')]");
        XPATH_CLASS = compileExpression(".//*[contains(@class, 'class')]");
        XPATH_ATTENDEE = compileExpression(".//*[contains(@class, 'attendee')]");
        XPATH_CONTACT = compileExpression(".//*[contains(@class, 'contact')]");
        XPATH_ORGANIZER = compileExpression(".//*[contains(@class, 'organizer')]");
        XPATH_SEQUENCE = compileExpression(".//*[contains(@class, 'sequence')]");
        XPATH_ATTACH = compileExpression(".//*[contains(@class, 'attach')]");
    }

    private static XPathExpression compileExpression(String expr) {
        try {
            return XPATH.compile(expr);
        } catch (XPathException e) {
            throw new CalendarException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void parse(InputStream in, ContentHandler handler) throws IOException, ParserException {
        parse(new InputSource(in), handler);
    }

    /**
     * {@inheritDoc}
     */
    public void parse(Reader in, ContentHandler handler) throws IOException, ParserException {
        parse(new InputSource(in), handler);
    }

    private void parse(InputSource in, ContentHandler handler) throws IOException, ParserException {
        try {
            Document d = BUILDER_FACTORY.newDocumentBuilder().parse(in);
            buildCalendar(d, handler);
        } catch (ParserConfigurationException e) {
            throw new CalendarException(e);
        } catch (SAXException e) {
            if (e instanceof SAXParseException) {
                SAXParseException pe = (SAXParseException) e;
                throw new ParserException("Could not parse XML", pe.getLineNumber(), e);
            }
            throw new ParserException(e.getMessage(), -1, e);
        }
    }

    private static NodeList findNodes(XPathExpression expr, Object context) throws ParserException {
        try {
            return (NodeList) expr.evaluate(context, XPathConstants.NODESET);
        } catch (XPathException e) {
            throw new ParserException("Unable to find nodes", -1, e);
        }
    }

    private static Node findNode(XPathExpression expr, Object context) throws ParserException {
        try {
            return (Node) expr.evaluate(context, XPathConstants.NODE);
        } catch (XPathException e) {
            throw new ParserException("Unable to find node", -1, e);
        }
    }

    private static List findElements(XPathExpression expr, Object context) throws ParserException {
        NodeList nodes = findNodes(expr, context);
        ArrayList elements = new ArrayList();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n instanceof Element)
                elements.add((Element) n);
        }
        return elements;
    }

    private static Element findElement(XPathExpression expr, Object context) throws ParserException {
        Node n = findNode(expr, context);
        if (n == null || (!(n instanceof Element)))
            return null;
        return (Element) n;
    }

    private static String getTextContent(Element element) throws ParserException {
        try {
            String content = element.getFirstChild().getNodeValue();
            if (content != null) {
                return content.trim().replaceAll("\\s+", " ");
            }
            return content;
        } catch (DOMException e) {
            throw new ParserException("Unable to get text content for element " + element.getNodeName(), -1, e);
        }
    }

    private void buildCalendar(Document d, ContentHandler handler) throws ParserException {
        // "The root class name for hCalendar is "vcalendar". An element with a
        // class name of "vcalendar" is itself called an hCalendar.
        //
        // The root class name for events is "vevent". An element with a class
        // name of "vevent" is itself called an hCalender event.
        //
        // For authoring convenience, both "vevent" and "vcalendar" are
        // treated as root class names for parsing purposes. If a document
        // contains elements with class name "vevent" but not "vcalendar", the
        // entire document has an implied "vcalendar" context."

        // XXX: We assume that the entire document has a single vcalendar
        // context. It is possible that the document contains more than one
        // vcalendar element. In this case, we should probably only process
        // that element and log a warning about skipping the others.

        if (LOG.isDebugEnabled())
            LOG.debug("Building calendar");

        handler.startCalendar();

        // no PRODID, as the using application should set that itself

        handler.startProperty(Property.VERSION);
        try {
            handler.propertyValue(Version.VERSION_2_0.getValue());
        } catch (Exception e) {
        }
        ;
        handler.endProperty(Property.VERSION);

        Element method = findElement(XPATH_METHOD, d);
        if (method != null) {
            buildProperty(method, Property.METHOD, handler);
        }

        List vevents = findElements(XPATH_VEVENTS, d);
        for (Iterator i = vevents.iterator(); i.hasNext();) {
            Element vevent = (Element) i.next();
            buildEvent(vevent, handler);
        }

        // XXX: support other "first class components": vjournal, vtodo,
        // vfreebusy, vavailability, vvenue

        handler.endCalendar();
    }

    private void buildEvent(Element element, ContentHandler handler) throws ParserException {
        if (LOG.isDebugEnabled())
            LOG.debug("Building event");

        handler.startComponent(Component.VEVENT);

        buildProperty(findElement(XPATH_DTSTART, element), Property.DTSTART, handler);
        buildProperty(findElement(XPATH_DTEND, element), Property.DTEND, handler);
        buildProperty(findElement(XPATH_DURATION, element), Property.DURATION, handler);
        buildProperty(findElement(XPATH_SUMMARY, element), Property.SUMMARY, handler);
        buildProperty(findElement(XPATH_UID, element), Property.UID, handler);
        buildProperty(findElement(XPATH_DTSTAMP, element), Property.DTSTAMP, handler);
        List categories = findElements(XPATH_CATEGORY, element);
        for (Iterator i = categories.iterator(); i.hasNext();) {
            Element category = (Element) i.next();
            buildProperty(category, Property.CATEGORIES, handler);
        }
        buildProperty(findElement(XPATH_LOCATION, element), Property.LOCATION, handler);
        buildProperty(findElement(XPATH_URL, element), Property.URL, handler);
        buildProperty(findElement(XPATH_DESCRIPTION, element), Property.DESCRIPTION, handler);
        buildProperty(findElement(XPATH_LAST_MODIFIED, element), Property.LAST_MODIFIED, handler);
        buildProperty(findElement(XPATH_STATUS, element), Property.STATUS, handler);
        buildProperty(findElement(XPATH_CLASS, element), Property.CLASS, handler);
        List attendees = findElements(XPATH_ATTENDEE, element);
        for (Iterator i = attendees.iterator(); i.hasNext();) {
            Element attendee = (Element) i.next();
            buildProperty(attendee, Property.ATTENDEE, handler);
        }
        buildProperty(findElement(XPATH_CONTACT, element), Property.CONTACT, handler);
        buildProperty(findElement(XPATH_ORGANIZER, element), Property.ORGANIZER, handler);
        buildProperty(findElement(XPATH_SEQUENCE, element), Property.SEQUENCE, handler);
        buildProperty(findElement(XPATH_ATTACH, element), Property.ATTACH, handler);

        handler.endComponent(Component.VEVENT);
    }

    private void buildProperty(Element element, String propName, ContentHandler handler) throws ParserException {
        if (element == null)
            return;

        if (LOG.isDebugEnabled())
            LOG.debug("Building property " + propName);

        String className = className(propName);
        String elementName = element.getLocalName().toLowerCase();

        String value = null;
        if (elementName.equals("abbr")) {
            // "If an <abbr> element is used for a property, then the 'title'
            // attribute of the <abbr> element is the value of the property,
            // instead of the contents of the element, which instead provide a
            // human presentable version of the value."
            value = element.getAttribute("title");
            if (StringUtils.isBlank(value))
                throw new ParserException("Abbr element '" + className + "' requires a non-empty title", -1);
            if (LOG.isDebugEnabled())
                LOG.debug("Setting value '" + value + "' from title attribute");
        } else if (isHeaderElement(elementName)) {
            // try title first. if that's not set, fall back to text content.
            value = element.getAttribute("title");
            if (!StringUtils.isBlank(value)) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Setting value '" + value + "' from title attribute");
            } else {
                value = getTextContent(element);
                if (LOG.isDebugEnabled())
                    LOG.debug("Setting value '" + value + "' from text content");
            }
        } else if (elementName.equals("a") && isUrlProperty(propName)) {
            value = element.getAttribute("href");
            if (StringUtils.isBlank(value))
                throw new ParserException("A element '" + className + "' requires a non-empty href", -1);
            if (LOG.isDebugEnabled())
                LOG.debug("Setting value '" + value + "' from href attribute");
        } else if (elementName.equals("img")) {
            if (isUrlProperty(propName)) {
                value = element.getAttribute("src");
                if (StringUtils.isBlank(value))
                    throw new ParserException("Img element '" + className + "' requires a non-empty src", -1);
                if (LOG.isDebugEnabled())
                    LOG.debug("Setting value '" + value + "' from src attribute");
            } else {
                value = element.getAttribute("alt");
                if (StringUtils.isBlank(value))
                    throw new ParserException("Img element '" + className + "' requires a non-empty alt", -1);
                if (LOG.isDebugEnabled())
                    LOG.debug("Setting value '" + value + "' from alt attribute");
            }
        } else {
            value = getTextContent(element);
            if (!StringUtils.isBlank(value)) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Setting value '" + value + "' from text content");
            }
        }

        if (StringUtils.isBlank(value)) {
            if (LOG.isDebugEnabled())
                LOG.debug("Skipping property with empty value");
            return;
        }

        handler.startProperty(propName);

        // if it's a date property, we have to convert from the
        // hCalendar-formatted date (RFC 3339) to an iCalendar-formatted date
        if (isDateProperty(propName)) {
            try {
                Date date = icalDate(value);
                value = date.toString();

                if (!(date instanceof DateTime))
                    try {
                        handler.parameter(Parameter.VALUE, Value.DATE.getValue());
                    } catch (Exception e) {
                    }
            } catch (ParseException e) {
                throw new ParserException("Malformed date value for element '" + className + "'", -1, e);
            }
        }

        if (isTextProperty(propName)) {
            String lang = element.getAttributeNS(XMLConstants.XML_NS_URI, "lang");
            if (!StringUtils.isBlank(lang))
                try {
                    handler.parameter(Parameter.LANGUAGE, lang);
                } catch (Exception e) {
                }
        }

        // XXX: other parameters?

        try {
            handler.propertyValue(value);
        } catch (URISyntaxException e) {
            throw new ParserException("Malformed URI value for element '" + className + "'", -1, e);
        } catch (ParseException e) {
            throw new ParserException("Malformed value for element '" + className + "'", -1, e);
        } catch (IOException e) {
            throw new CalendarException(e);
        }

        handler.endProperty(propName);
    }

    // "The basic format of hCalendar is to use iCalendar object/property
    // names in lower-case for class names ..."
    /*
     * private static String _icalName(Element element) { return element.getAttribute("class").toUpperCase(); }
     */

    private static String className(String propName) {
        return propName.toLowerCase();
    }

    private static boolean isHeaderElement(String name) {
        return (name.equals("h1") || name.equals("h2") || name.equals("h3")
                || name.equals("h4") || name.equals("h5") || name
                .equals("h6"));
    }

    private static boolean isDateProperty(String name) {
        return (name.equals(Property.DTSTART) || name.equals(Property.DTEND) || name.equals(Property.DTSTAMP) || name
                .equals(Property.LAST_MODIFIED));
    }

    private static boolean isUrlProperty(String name) {
        return (name.equals(Property.URL));
    }

    private static boolean isTextProperty(String name) {
        return (name.equals(Property.SUMMARY) || name.equals(Property.LOCATION) || name.equals(Property.CATEGORIES)
                || name.equals(Property.DESCRIPTION) || name.equals(Property.ATTENDEE)
                || name.equals(Property.CONTACT) || name
                .equals(Property.ORGANIZER));
    }

    private static Date icalDate(String original) throws ParseException {
        // in the real world, some generators use iCalendar formatted
        // dates and date-times, so try parsing those formats first before
        // going to RFC 3339 formats

        if (original.indexOf('T') == -1) {
            // date-only
            try {
                // for some reason Date's pattern matches yyyy-MM-dd, so
                // don't check it if we find -
                if (original.indexOf('-') == -1)
                    return new Date(original);
            } catch (Exception e) {
            }
            return new Date(HCAL_DATE_FORMAT.parse(original));
        }

        try {
            return new DateTime(original);
        } catch (Exception e) {
        }

        // the date-time value can represent its time zone in a few different
        // ways. we have to normalize those to match our pattern.

        String normalized = null;

        if (LOG.isDebugEnabled())
            LOG.debug("normalizing date-time " + original);

        // 2002-10-09T19:00:00Z
        if (original.charAt(original.length() - 1) == 'Z') {
            normalized = original.replaceAll("Z", "GMT-00:00");
        }
        // 2002-10-10T00:00:00+05:00
        else if (original.indexOf("GMT") == -1
                && (original.charAt(original.length() - 6) == '+' || original.charAt(original.length() - 6) == '-')) {
            String tzId = "GMT" + original.substring(original.length() - 6);
            normalized = original.substring(0, original.length() - 6) + tzId;
        } else {
            // 2002-10-10T00:00:00GMT+05:00
            normalized = original;
        }

        DateTime dt = new DateTime(HCAL_DATE_TIME_FORMAT.parse(normalized));

        // hCalendar does not specify a representation for timezone ids
        // or any other sort of timezone information. the best it does is
        // give us a timezone offset that we can use to convert the local
        // time to UTC. furthermore, it has no representation for floating
        // date-times. therefore, all dates are converted to UTC.

        dt.setUtc(true);

        return dt;
    }
}
