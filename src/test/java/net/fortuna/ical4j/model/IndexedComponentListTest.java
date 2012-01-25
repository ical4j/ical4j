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
package net.fortuna.ical4j.model;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import net.fortuna.ical4j.data.CalendarBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id$
 *
 * Created on 5/02/2006
 *
 * Unit tests for indexed component list.
 * @author Ben Fortuna
 */
public class IndexedComponentListTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(IndexedComponentListTest.class);
    
    private Calendar calendar;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        CalendarBuilder builder = new CalendarBuilder();
        calendar = builder.build(new FileReader("etc/samples/valid/Australian_TV_Melbourne.ics"));
    }

    /**
     * Indexing with IndexedComponentList.
     */
    public void testIndexing() {
        long start = System.currentTimeMillis();
        IndexedComponentList list = new IndexedComponentList(calendar.getComponents(),
                Property.LOCATION);
        LOG.info(list.getComponents("ABC").size() + " programs on ABC."
            + " (" + (System.currentTimeMillis() - start) + "ms)");
    }
    
    /**
     * Perform manual indexing.
     */
    public void testManualIndexing() {
        long start = System.currentTimeMillis();
        List list = new ArrayList();
        for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
            Component c = (Component) i.next();
            if (c.getProperty(Property.LOCATION) != null
                    && "ABC".equals(c.getProperty(Property.LOCATION).getValue())) {
                list.add(c);
            }
        }
        LOG.info(list.size() + " programs on ABC."
            + " (" + (System.currentTimeMillis() - start) + "ms)");
    }
}
