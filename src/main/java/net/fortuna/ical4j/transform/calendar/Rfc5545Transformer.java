package net.fortuna.ical4j.transform.calendar;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.transform.RuleManager;
import net.fortuna.ical4j.transform.Transformer;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Rfc5545Transformer implements Transformer<Calendar> {

    @Override
    public Calendar transform(Calendar object) {

        conformPropertiesToRfc5545(object.getProperties().getAll());

        for(Component component : object.getComponents().getAll()){
            CountableProperties.removeExceededPropertiesForComponent(component);

            //each component
            conformComponentToRfc5545(component);

            //each component property
            conformPropertiesToRfc5545(component.getProperties().getAll());

            for(java.lang.reflect.Method m : component.getClass().getDeclaredMethods()){
                if(ComponentList.class.isAssignableFrom(m.getReturnType()) &&
                        m.getName().startsWith("get")){

                    try {
                        ComponentList<Component> components = (ComponentList<Component>) m.invoke(component);
                        for(Component c : components.getAll()){
                            //each inner component
                            conformComponentToRfc5545(c);

                            //each inner component properties
                            conformPropertiesToRfc5545(c.getProperties().getAll());
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
