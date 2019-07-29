package net.fortuna.ical4j.transform;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.transform.rfc5545.RuleManager;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Rfc5545Transformer implements Transformer<Calendar> {

    @Override
    public Calendar transform(Calendar object) {

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
                        List<Component> components = (List<Component>) m.invoke(component);
                        for(Component c : components){
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
        private int maxApparitionNumber;
        private String name;

        CountableProperties(String name, int maxApparitionNumber){
            this.maxApparitionNumber = maxApparitionNumber;
            this.name = name;
        }

        protected void limitApparitionsNumberIn(Component component){
            PropertyList<? extends Property> propertyList = component.getProperties(name);

            if(propertyList.size() <= maxApparitionNumber){
                return;
            }
            int toRemove = propertyList.size() - maxApparitionNumber;
            for(int i = 0; i < toRemove; i++){
                component.getProperties().remove(propertyList.get(i));            }
        }

        private static void removeExceededPropertiesForComponent(Component component){
            for(CountableProperties cp: values()){
                cp.limitApparitionsNumberIn(component);
            }
        }
    }
}
