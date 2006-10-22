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

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.AbstractPropertyTest;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.parameter.Value;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Ben Fortuna
 *
 */
public class TriggerTest extends AbstractPropertyTest {
    
    private static Log log = LogFactory.getLog(TriggerTest.class);

    /**
     * @throws ParseException
     */
    public void testSetValue() throws ParseException {
        Trigger trigger = new Trigger();
        trigger.setValue(new DateTime(new Date(0).getTime()).toString());
        
        log.info(new DateTime(new Date(0).getTime()));
        log.info(trigger);

//        trigger.setValue(DurationFormat.getInstance().format(5000));
        trigger.setValue(new Dur(0, 0, 0, 5).toString());
        
//        log.info(DurationFormat.getInstance().format(5000));
        log.info(new Dur(0, 0, 0, 5));
        log.info(trigger);
    }

    /**
     * Unit test on a duration trigger.
     */
    public void testTriggerDuration() {
        Trigger trigger = new Trigger(new Dur(1, 0, 0, 0));
        
        assertNotNull(trigger.getDuration());
        assertNull(trigger.getDate());
        assertNull(trigger.getDateTime());
    }

    /**
     * Unit test on a date-time trigger.
     */
    public void testTriggerDateTime() throws ValidationException {
        Trigger trigger = new Trigger(new DateTime(new Date()));
        
        assertNull(trigger.getDuration());
        assertNotNull(trigger.getDate());
        assertNotNull(trigger.getDateTime());
        trigger.validate();

        trigger.getParameters().add(Value.DURATION);
        assertValidationException(trigger);
    }
}
