package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fortuna on 13/09/15.
 */
public class CalendarValidatorImpl implements Validator<Calendar> {

    protected final List<Class<? extends Property>> calendarProperties = new ArrayList<Class<? extends Property>>();

    public CalendarValidatorImpl() {
        Collections.addAll(calendarProperties, CalScale.class, Method.class, ProdId.class, Version.class);
    }

    @Override
    public void validate(Calendar target) throws ValidationException {
        // 'prodid' and 'version' are both REQUIRED,
        // but MUST NOT occur more than once
        PropertyValidator.getInstance().assertOne(Property.PRODID, target.getProperties());
        PropertyValidator.getInstance().assertOne(Property.VERSION, target.getProperties());

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            // require VERSION:2.0 for RFC2445..
            if (!Version.VERSION_2_0.equals(target.getProperty(Property.VERSION))) {
                throw new ValidationException("Unsupported Version: " + target.getProperty(Property.VERSION).getValue());
            }
        }

        // 'calscale' and 'method' are optional,
        // but MUST NOT occur more than once
        PropertyValidator.getInstance().assertOneOrLess(Property.CALSCALE,
                target.getProperties());
        PropertyValidator.getInstance().assertOneOrLess(Property.METHOD,
                target.getProperties());

        // must contain at least one component
        if (target.getComponents().isEmpty()) {
            throw new ValidationException(
                    "Calendar must contain at least one component");
        }

        // validate properties..
        for (final Property property : target.getProperties()) {
            boolean isCalendarProperty = CollectionUtils.find(calendarProperties, new Predicate<Class<? extends Property>>() {
                @Override
                public boolean evaluate(Class<? extends Property> object) {
                    return object.isInstance(property);
                }
            }) != null;

            if (!(property instanceof XProperty) && !isCalendarProperty) {
                throw new ValidationException("Invalid property: " + property.getName());
            }
        }

//        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
        // validate method..
        final Method method = (Method) target.getProperty(Property.METHOD);
        if (Method.PUBLISH.equals(method)) {
            if (target.getComponent(Component.VEVENT) != null) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());

                if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
                    ComponentValidator.assertNone(Component.VTODO, target.getComponents());
                }
            }
            else if (target.getComponent(Component.VFREEBUSY) != null) {
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTIMEZONE, target.getComponents());
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO) != null) {
//                    ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                    ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
//                else if (target.getComponent(Component.VJOURNAL) != null) {
//                    ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                    ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
//                    ComponentValidator.assertNone(Component.VTODO, target.getComponents());
//                }
        }
        else if (Method.REQUEST.equals(target.getProperty(Property.METHOD))) {
            if (target.getComponent(Component.VEVENT) != null) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VFREEBUSY) != null) {
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTIMEZONE, target.getComponents());
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO) != null) {
//                  ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
        }
        else if (Method.REPLY.equals(target.getProperty(Property.METHOD))) {
            if (target.getComponent(Component.VEVENT) != null) {
                ComponentValidator.assertOneOrLess(Component.VTIMEZONE, target.getComponents());

                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VFREEBUSY) != null) {
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTIMEZONE, target.getComponents());
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO) != null) {
                ComponentValidator.assertOneOrLess(Component.VTIMEZONE, target.getComponents());

                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
//                  ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
        }
        else if (Method.ADD.equals(target.getProperty(Property.METHOD))) {
            if (target.getComponent(Component.VEVENT) != null) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO) != null) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
            else if (target.getComponent(Component.VJOURNAL) != null) {
                ComponentValidator.assertOneOrLess(Component.VTIMEZONE, target.getComponents());

                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
//                  ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
        }
        else if (Method.CANCEL.equals(target.getProperty(Property.METHOD))) {
            if (target.getComponent(Component.VEVENT) != null) {
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO) != null) {
                ComponentValidator.assertOneOrLess(Component.VTIMEZONE, target.getComponents());

                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
            else if (target.getComponent(Component.VJOURNAL) != null) {
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
//                  ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
        }
        else if (Method.REFRESH.equals(target.getProperty(Property.METHOD))) {
            if (target.getComponent(Component.VEVENT) != null) {
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO) != null) {
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTIMEZONE, target.getComponents());
            }
        }
        else if (Method.COUNTER.equals(target.getProperty(Property.METHOD))) {
            if (target.getComponent(Component.VEVENT) != null) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO) != null) {
                ComponentValidator.assertOneOrLess(Component.VTIMEZONE, target.getComponents());

                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
        }
        else if (Method.DECLINE_COUNTER.equals(target.getProperty(Property.METHOD))) {
            if (target.getComponent(Component.VEVENT) != null) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
                ComponentValidator.assertNone(Component.VTIMEZONE, target.getComponents());
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO) != null) {
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
        }
//        }

        // perform ITIP validation on components..
        if (method != null) {
            for (CalendarComponent component : target.getComponents()) {
                component.validate(method);
            }
        }
    }
}
