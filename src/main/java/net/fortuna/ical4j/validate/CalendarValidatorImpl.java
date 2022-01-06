package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyContainer;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by fortuna on 13/09/15.
 */
public class CalendarValidatorImpl implements Validator<Calendar> {

    protected final List<Class<? extends Property>> calendarProperties = new ArrayList<>();

    private final List<ValidationRule> rules;

    public CalendarValidatorImpl(ValidationRule... rules) {
        this.rules = Arrays.asList(rules);

        Collections.addAll(calendarProperties, CalScale.class, Method.class, ProdId.class, Version.class,
                Uid.class, LastModified.class, Url.class, RefreshInterval.class, Source.class, Color.class,
                Name.class, Description.class, Categories.class, Image.class);
    }

    @Override
    public void validate(Calendar target) throws ValidationException {
        ValidationResult result = new ValidationResult();

        for (ValidationRule rule : rules) {
            boolean warnOnly = CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
                    && rule.isRelaxedModeSupported();

            if (warnOnly) {
                result.getWarnings().addAll(apply(rule, (PropertyContainer) target));
            } else {
                result.getErrors().addAll(apply(rule, (PropertyContainer) target));
            }
        }

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            // require VERSION:2.0 for RFC2445..
            if (!Version.VERSION_2_0.equals(target.getProperty(Property.VERSION))) {
                result.getErrors().add("Unsupported Version: " + target.getProperty(Property.VERSION).getValue());
            }
        }

        // must contain at least one component
        if (target.getComponents().isEmpty()) {
            result.getErrors().add("Calendar must contain at least one component");
        }

        // validate properties..
        for (final Property property : target.getProperties()) {
            boolean isCalendarProperty = calendarProperties.stream().filter(calProp -> calProp.isInstance(property)) != null;

            if (!(property instanceof XProperty) && !isCalendarProperty) {
                result.getErrors().add("Invalid property: " + property.getName());
            }
        }

//        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
        // validate method..
        final Method method = target.getProperty(Property.METHOD);
        if (Method.PUBLISH.equals(method)) {
            new PublishValidator().validate(target);
        }
        else if (Method.REQUEST.equals(target.getProperty(Property.METHOD))) {
            new RequestValidator().validate(target);
        }
        else if (Method.REPLY.equals(target.getProperty(Property.METHOD))) {
            new ReplyValidator().validate(target);
        }
        else if (Method.ADD.equals(target.getProperty(Property.METHOD))) {
            new AddValidator().validate(target);
        }
        else if (Method.CANCEL.equals(target.getProperty(Property.METHOD))) {
            new CancelValidator().validate(target);
        }
        else if (Method.REFRESH.equals(target.getProperty(Property.METHOD))) {
            new RefreshValidator().validate(target);
        }
        else if (Method.COUNTER.equals(target.getProperty(Property.METHOD))) {
            new CounterValidator().validate(target);
        }
        else if (Method.DECLINE_COUNTER.equals(target.getProperty(Property.METHOD))) {
            new DeclineCounterValidator().validate(target);
        }
//        }

        // perform ITIP validation on components..
        if (method != null) {
            for (CalendarComponent component : target.getComponents()) {
                component.validate(method);
            }
        }

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
    }

    public static class PublishValidator implements Validator<Calendar> {

        @Override
        public void validate(Calendar target) throws ValidationException {
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
    }

    public static class RequestValidator implements Validator<Calendar> {

        @Override
        public void validate(Calendar target) throws ValidationException {
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
    }

    public static class ReplyValidator implements Validator<Calendar> {

        @Override
        public void validate(Calendar target) throws ValidationException {
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
    }

    public static class AddValidator implements Validator<Calendar> {

        @Override
        public void validate(Calendar target) throws ValidationException {
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
    }

    public static class CancelValidator implements Validator<Calendar> {

        @Override
        public void validate(Calendar target) throws ValidationException {
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
    }

    public static class RefreshValidator implements Validator<Calendar> {

        @Override
        public void validate(Calendar target) throws ValidationException {
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
    }

    public static class CounterValidator implements Validator<Calendar> {

        @Override
        public void validate(Calendar target) throws ValidationException {
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
    }

    public static class DeclineCounterValidator implements Validator<Calendar> {

        @Override
        public void validate(Calendar target) throws ValidationException {
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
    }
}
