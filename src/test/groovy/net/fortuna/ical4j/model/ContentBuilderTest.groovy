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
package net.fortuna.ical4j.model

import net.fortuna.ical4j.model.property.Version
import net.fortuna.ical4j.model.component.VFreeBusyimport net.fortuna.ical4j.util.UidGeneratorimport net.fortuna.ical4j.util.SimpleHostInfo/**
 * $Id$
 *
 * Created on: 03/08/2009
 *
 * @author fortuna
 *
 */
public class ContentBuilderTest extends GroovyTestCase {

    void testBuildCalendar() {
        def builder = new ContentBuilder()
        def calendar = builder.calendar() {
            prodid('-//Ben Fortuna//iCal4j 1.0//EN')
            version('2.0')
            vevent() {
                uid('1')
                dtstamp()
                dtstart('20090810', parameters: parameters() {
                    value('DATE')})
                action('DISPLAY')
                attach('http://example.com/attachment', parameters: parameters() {
                    value('URI')})
            }
        }
        calendar.validate()
        
        assert calendar.properties.size() == 2
        assert calendar.components.size() == 1
        
        println(calendar)
    }
    
    void testBuildParameterList() {
        def parameters = new ContentBuilder().parameters() {
            value('TIME')
            type('test')
            role('CHAIR')
        }
        
        assert parameters.size() == 3
        println(parameters)
    }
    
    void testBuildVFreeBusy() {
        def builder = new ContentBuilder()
        
        def request = new VFreeBusy(builder.vfreebusy() {
            dtstart('20080101', parameters: parameters() {
                value('DATE')})
            dtend('20100101', parameters: parameters() {
                value('DATE')})
        }, new ComponentList())
        
        def vfreebusy1 = builder.vfreebusy(request)
        
        assert vfreebusy1 == request
        println(vfreebusy1)
    }
    
    void testBuildDtStamp() {
        def dtStamp = new ContentBuilder().dtstamp('20090803T093000Z')
        assert dtStamp.value == '20090803T093000Z'
        println(dtStamp)
    }
    
    void testBuildProdId() {
        def prodId = new ContentBuilder().prodid('-//Ben Fortuna//iCal4j 1.0//EN')
        assert prodId.value == '-//Ben Fortuna//iCal4j 1.0//EN'
        println(prodId)
    }
    
    void testBuildUid() {
        def uid = new ContentBuilder().uid('1')
        assert uid.value == '1'
        println(uid)
        
        uid = new UidGenerator(new SimpleHostInfo('example.com'), '1').generateUid()
        assert uid == new ContentBuilder().uid(uid)
        println(uid)
    }
    
    void testBuildVersion() {
        def version = new ContentBuilder().version(value: '2.0')
        assert version == Version.VERSION_2_0
        
        version = new ContentBuilder().version('2.0')
        assert version == Version.VERSION_2_0
    }
    
    void testBuildAbbrev() {
        def abbrev = new ContentBuilder().abbrev('abb')
        assert abbrev.value == 'abb'
        println(abbrev)
    }
    
    void testBuildAltRep() {
        def altRep = new ContentBuilder().altrep('http://example.com/alt')
        assert altRep.value == 'http://example.com/alt'
        println(altRep)
    }
    
    void testBuildCn() {
        def cn = new ContentBuilder().cn('Doe, John')
        assert cn.value == 'Doe, John'
        println(cn)
    }
    
    void testBuildXComponent() {
        def xcomponent = new ContentBuilder().xcomponent('test')
        assert xcomponent.name == 'test'
        
        xcomponent = new ContentBuilder().xcomponent(name: 'test')
        assert xcomponent.name == 'test'
    }
    
    void testBuildXProperty() {
        def xproperty = new ContentBuilder().xproperty('test')
        assert xproperty.name == 'test'
        
        xproperty = new ContentBuilder().xproperty(name: 'test')
        assert xproperty.name == 'test'
    }
    
    void testBuildXParameter() {
        def xparameter = new ContentBuilder().xparameter('test')
        assert xparameter.name == 'test'
        
        xparameter = new ContentBuilder().xparameter(name: 'test')
        assert xparameter.name == 'test'
    }
}
