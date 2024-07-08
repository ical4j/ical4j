/*
 *  Copyright (c) 2024, Ben Fortuna
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *   o Neither the name of Ben Fortuna nor the names of any other contributors
 *  may be used to endorse or promote products derived from this software
 *  without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.fortuna.ical4j.transform.compliance;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.transform.Transformer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Rfc5545Transformer implements Transformer<Calendar> {

    @Override
    public Calendar apply(Calendar object) {

        conformPropertiesToRfc5545(object.getProperties());

        for(Component component : object.getComponents()){
            CountableProperties.removeExceededPropertiesForComponent(component);

            //each component
            conformComponentToRfc5545(component);

            //each component property
            conformPropertiesToRfc5545(component.getProperties());

            for(java.lang.reflect.Method m : component.getClass().getDeclaredMethods()){
                if(ComponentList.class.isAssignableFrom(m.getReturnType()) &&
                        m.getName().startsWith("get")){

                    try {
                        ComponentList<Component> components = (ComponentList<Component>) m.invoke(component);
                        for(Component c : components.getAll()){
                            //each inner component
                            conformComponentToRfc5545(c);

                            //each inner component properties
                            conformPropertiesToRfc5545(c.getProperties());
                        }
                    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return object;
    }

    private static void conformPropertiesToRfc5545(List<Property> properties) {
        for (Property property : properties) {
            RuleManager.applyTo(property);
        }
    }

    private static void conformComponentToRfc5545(Component component){
        RuleManager.applyTo(component);
    }

    private enum CountableProperties{
        STATUS(Property.STATUS, 1);
        private final int maxApparitionNumber;
        private final String name;

        CountableProperties(String name, int maxApparitionNumber){
            this.maxApparitionNumber = maxApparitionNumber;
            this.name = name;
        }

        protected void limitApparitionsNumberIn(Component component){
            List<Property> propertyList = component.getProperties(name);

            if(propertyList.size() <= maxApparitionNumber){
                return;
            }
            int toRemove = propertyList.size() - maxApparitionNumber;
            for(int i = 0; i < toRemove; i++){
                component.remove(propertyList.get(i));            }
        }

        private static void removeExceededPropertiesForComponent(Component component){
            for(CountableProperties cp: values()){
                cp.limitApparitionsNumberIn(component);
            }
        }
    }
}
