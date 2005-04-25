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
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.fortuna.ical4j.model.component;

import junit.framework.TestCase;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.RRule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.util.Date;

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
        
        VFreeBusy request = new VFreeBusy(startDate, endDate);
        
        VFreeBusy fb = new VFreeBusy(request, components);

        if (log.isDebugEnabled()) {
            log.debug("\n==\n" + fb.toString());
        }
    }

    /*
     * Class under test for void VFreeBusy(ComponentList)
     */
    public final void testVFreeBusyComponentList2() throws Exception {
        FileInputStream fin = new FileInputStream("etc/samples/valid/core.ics");

        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(fin);

        Date startDate = new Date(0);
        Date endDate = new Date();

        // request all busy time between 1970 and now..
        VFreeBusy requestBusy = new VFreeBusy(startDate, endDate);
        
        VFreeBusy fb = new VFreeBusy(requestBusy, calendar.getComponents());
        
        log.info("\n==\n" + fb.toString());

        // request all free time between 1970 and now of duration 2 hours or more..
        VFreeBusy requestFree = new VFreeBusy(startDate, endDate, 2 * 60 * 60 * 1000);
        
        VFreeBusy fb2 = new VFreeBusy(requestFree, calendar.getComponents());
        
        log.debug("\n==\n" + fb2.toString());
    }
    
    public final void testVFreeBusyComponentList3() throws Exception {
        ComponentList components = new ComponentList();

        Date startDate = new Date(0);
        Date endDate = new Date();
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(java.util.Calendar.HOUR_OF_DAY, 1);
        
        VEvent event = new VEvent(startDate, ONE_HOUR, "Progress Meeting");
//        VEvent event = new VEvent(startDate, cal.getTime(), "Progress Meeting");
        // add timezone information..
        VTimeZone tz = VTimeZone.getDefault();
        TzId tzParam = new TzId(tz.getProperties().getProperty(Property.TZID).getValue());
        event.getProperties().getProperty(Property.DTSTART).getParameters().add(tzParam);
        components.add(event);
        
        // add recurrence..
        Recur recur = new Recur(Recur.YEARLY, 20);
        recur.getMonthList().add(new Integer(1));
        recur.getMonthDayList().add(new Integer(26));
        recur.getHourList().add(new Integer(9));
        recur.getMinuteList().add(new Integer(30));
        event.getProperties().add(new RRule(recur));
        
        log.debug("\n==\n" + event.toString());
        
        VFreeBusy request = new VFreeBusy(startDate, endDate);
        
        VFreeBusy fb = new VFreeBusy(request, components);
        
        log.info("\n==\n" + fb.toString());
    }
    
    public final void testVFreeBusyComponentList4() throws Exception {
        ComponentList components = new ComponentList();

        java.util.Calendar cal = java.util.Calendar.getInstance();

        Date startDate = cal.getTime();
        cal.add(java.util.Calendar.DATE, 3);
        Date endDate = cal.getTime();
        
        VEvent event = new VEvent();
        event.getProperties().add(new DtStart(startDate));
//        event.getProperties().add(new DtEnd(new Date()));
        event.getProperties().add(new Duration(ONE_HOUR));
        components.add(event);        
        
        VEvent event2 = new VEvent();
        event2.getProperties().add(new DtStart(startDate));
        event2.getProperties().add(new DtEnd(endDate));
        components.add(event2);
        
        VFreeBusy request = new VFreeBusy(startDate, endDate, ONE_HOUR);
        
        VFreeBusy fb = new VFreeBusy(request, components);
        
        log.debug("\n==\n" + fb.toString());
    }
    
    public final void testAngelites() {
        log.info("angelites test:\n================");
        
        Calendar FreeBusyTest = new Calendar();
             
//             add an event 
            java.util.Calendar start = java.util.Calendar.getInstance(); 
            java.util.Calendar end = java.util.Calendar.getInstance(); 
            start.add(java.util.Calendar.DATE, -1); 
            VEvent dteStartOnly = new VEvent(start.getTime(), "DATE START ONLY"); 
            VEvent dteEnd = new VEvent(start.getTime(), end.getTime(), "DATE END INCLUDED"); 
            VEvent duration = new VEvent(start.getTime(), 60 * 60 * 1000, "DURATION"); 
            FreeBusyTest.getComponents().add(dteEnd); 
            FreeBusyTest.getComponents().add(duration); 
             
            java.util.Calendar dtstart = java.util.Calendar.getInstance();
            java.util.Calendar dtend = java.util.Calendar.getInstance(); 
            dtstart.add(java.util.Calendar.DATE, -2); 
            VFreeBusy getBusy = new VFreeBusy(dtstart.getTime(), dtend.getTime()); 
            VFreeBusy requestFree = new VFreeBusy(dtstart.getTime(), dtend.getTime(), 30 * 60 * 1000); 
             
            log.debug("GET BUSY: \n" + getBusy.toString());
            log.debug("REQUEST FREE: \n" + requestFree.toString());
             
            Calendar FreeBusyTest2 = new Calendar();
             
             
            VFreeBusy replyBusy = new VFreeBusy(getBusy, FreeBusyTest.getComponents()); 
            VFreeBusy replyFree = new VFreeBusy(requestFree, FreeBusyTest.getComponents()); 
             
            log.debug("REPLY BUSY: \n" + replyBusy.toString());
            log.debug("REPLY FREE: \n" + replyFree.toString());
             
            FreeBusyTest2.getComponents().add(replyBusy); 
            VFreeBusy replyBusy2 = new VFreeBusy(getBusy, FreeBusyTest2.getComponents()); 
            VFreeBusy replyFree2 = new VFreeBusy(requestFree, FreeBusyTest2.getComponents()); 
             
            log.debug("REPLY BUSY2: \n" + replyBusy2.toString());
            log.debug("REPLY FREE2: \n" + replyFree2.toString());
    }
}
