/*
 * Created on 10/02/2005
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
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model.component;

import java.io.FileInputStream;
import java.util.Date;

import junit.framework.TestCase;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ben Fortuna
 *
 */
public class VFreeBusyTest extends TestCase {
    
    private static final long ONE_HOUR = 3600000;
    
    private static Log log = LogFactory.getLog(VFreeBusyTest.class);

    /*
     * Class under test for void VFreeBusy(ComponentList)
     */
    public final void testVFreeBusyComponentList() throws Exception {
        ComponentList components = new ComponentList();

        Date startDate = new Date(0);
        Date endDate = new Date();
        
        VEvent event = new VEvent();
        event.getProperties().add(new DtStart(startDate));
//        event.getProperties().add(new DtEnd(new Date()));
        event.getProperties().add(new Duration(ONE_HOUR));
        components.add(event);        
        
        VEvent event2 = new VEvent();
        event2.getProperties().add(new DtStart(startDate));
        event2.getProperties().add(new DtEnd(endDate));
        components.add(event2);        
        
        VFreeBusy fb = new VFreeBusy(startDate, endDate, components);
        
        log.info("\n==\n" + fb.toString());
    }

    /*
     * Class under test for void VFreeBusy(ComponentList)
     */
    public final void testVFreeBusyComponentList2() throws Exception {
        FileInputStream fin = new FileInputStream("etc/samples/core.ics");

        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(fin);

        Date startDate = new Date(0);
        Date endDate = new Date();
        
        VFreeBusy fb = new VFreeBusy(startDate, endDate, calendar.getComponents());
        
        log.info("\n==\n" + fb.toString());
    }
}
