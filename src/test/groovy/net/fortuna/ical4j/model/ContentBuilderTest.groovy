/*
 * Copyright (c) 2009, Ben Fortuna
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
/**
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
            event() {
                uid('1')
                dtstamp('20090803T093000Z')
            }
        }
        calendar.validate()
        
//        assert calendar.properties.size == 2
//        assert calendar.components.size == 1
        
        println(calendar)
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
    }
    
    void testBuildVersion() {
        def version = new ContentBuilder().version(value: '2.0')
        assert version == Version.VERSION_2_0
        
        version = new ContentBuilder().version('2.0')
        assert version == Version.VERSION_2_0
    }
}
