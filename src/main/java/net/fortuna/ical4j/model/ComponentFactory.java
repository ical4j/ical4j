package net.fortuna.ical4j.model;

import net.java.sezpoz.Indexable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by fortuna on 12/09/14.
 */
public interface ComponentFactory<T extends Component> {

    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)
    @Indexable(type= ComponentFactory.class)
    public @interface Service {}

    T createComponent();

    T createComponent(PropertyList properties);

    T createComponent(PropertyList properties, ComponentList subComponents);

    boolean supports(String name);
}
