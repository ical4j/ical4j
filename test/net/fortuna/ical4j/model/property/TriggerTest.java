/*
 * Created on 7/03/2005
 *
 * $Id$
 *
 * Copyright (c) 2005, Ben Fortuna
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.fortuna.ical4j.model.property;

import java.text.ParseException;
import java.util.Date;

import junit.framework.TestCase;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.util.DateTimeFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ben
 *
 */
public class TriggerTest extends TestCase {
    
    private static Log log = LogFactory.getLog(TriggerTest.class);

    public void testSetValue() throws ParseException {
        Trigger trigger = new Trigger();
        trigger.setValue(DateTimeFormat.getInstance().format(new Date(0)));
        
        log.info(DateTimeFormat.getInstance().format(new Date(0)));
        log.info(trigger);

//        trigger.setValue(DurationFormat.getInstance().format(5000));
        trigger.setValue(new Dur(0, 0, 0, 5).toString());
        
//        log.info(DurationFormat.getInstance().format(5000));
        log.info(new Dur(0, 0, 0, 5));
        log.info(trigger);
    }

}
