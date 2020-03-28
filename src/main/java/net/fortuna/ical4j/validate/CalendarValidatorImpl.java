package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.util.*;

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
        for (ValidationRule rule : rules) {
            if (CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
                    && rule.isRelaxedModeSupported()) {
                continue;
            }

            switch (rule.getType()) {
                case None:
                    rule.getInstances().forEach(s -> PropertyValidator.assertNone(s,
                            target.getProperties()));
                    break;
                case One:
                    rule.getInstances().forEach(s -> PropertyValidator.assertOne(s,
                            target.getProperties()));
                    break;
                case OneOrLess:
                    rule.getInstances().forEach(s -> PropertyValidator.assertOneOrLess(s,
                            target.getProperties()));
                    break;
                case OneOrMore:
                    rule.getInstances().forEach(s -> PropertyValidator.assertOneOrMore(s,
                            target.getProperties()));
                    break;
            }
        }

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            // require VERSION:2.0 for RFC2445..
            Optional<Version> version = target.getProperty(Property.VERSION);
            if (version.isPresent() && !Version.VERSION_2_0.equals(version.get())) {
                throw new ValidationException("Unsupported Version: " + version.get().getValue());
            }
        }

        // must contain at least one component
        if (target.getComponents().isEmpty()) {
            throw new ValidationException("Calendar must contain at least one component");
        }

        // validate properties..
        for (final Property property : target.getProperties()) {
            boolean isCalendarProperty = calendarProperties.stream().anyMatch(calProp -> calProp.isInstance(property));

            if (!(property instanceof XProperty) && !isCalendarProperty) {
                throw new ValidationException("Invalid property: " + property.getName());
            }
        }

//        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
        // validate method..
        final Optional<Method> method = target.getProperty(Property.METHOD);
        if (method.isPresent()) {
            if (Method.PUBLISH.equals(method.get())) {
                new PublishValidator().validate(target);
            } else if (Method.REQUEST.equals(method.get())) {
                new RequestValidator().validate(target);
            } else if (Method.REPLY.equals(method.get())) {
                new ReplyValidator().validate(target);
            } else if (Method.ADD.equals(method.get())) {
                new AddValidator().validate(target);
            } else if (Method.CANCEL.equals(method.get())) {
                new CancelValidator().validate(target);
            } else if (Method.REFRESH.equals(method.get())) {
                new RefreshValidator().validate(target);
            } else if (Method.COUNTER.equals(method.get())) {
                new CounterValidator().validate(target);
            } else if (Method.DECLINE_COUNTER.equals(method.get())) {
                new DeclineCounterValidator().validate(target);
            }

            // perform ITIP validation on components..
            for (CalendarComponent component : target.getComponents()) {
                component.validate(method.get());
            }
        }
    }

    public static class PublishValidator implements Validator<Calendar> {

        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponent(Component.VEVENT).isPresent()) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());

                if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
                    ComponentValidator.assertNone(Component.VTODO, target.getComponents());
                }
            }
            else if (target.getComponent(Component.VFREEBUSY).isPresent()) {
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTIMEZONE, target.getComponents());
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO).isPresent()) {
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
            if (target.getComponent(Component.VEVENT).isPresent()) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VFREEBUSY).isPresent()) {
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTIMEZONE, target.getComponents());
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO).isPresent()) {
//                  ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
        }
    }

    public static class ReplyValidator implements Validator<Calendar> {

        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponent(Component.VEVENT).isPresent()) {
                ComponentValidator.assertOneOrLess(Component.VTIMEZONE, target.getComponents());

                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VFREEBUSY).isPresent()) {
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTIMEZONE, target.getComponents());
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO).isPresent()) {
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
            if (target.getComponent(Component.VEVENT).isPresent()) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO).isPresent()) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
            else if (target.getComponent(Component.VJOURNAL).isPresent()) {
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
            if (target.getComponent(Component.VEVENT).isPresent()) {
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO).isPresent()) {
                ComponentValidator.assertOneOrLess(Component.VTIMEZONE, target.getComponents());

                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
            else if (target.getComponent(Component.VJOURNAL).isPresent()) {
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
            if (target.getComponent(Component.VEVENT).isPresent()) {
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO).isPresent()) {
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
            if (target.getComponent(Component.VEVENT).isPresent()) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO).isPresent()) {
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
            if (target.getComponent(Component.VEVENT).isPresent()) {
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
                ComponentValidator.assertNone(Component.VTODO, target.getComponents());
                ComponentValidator.assertNone(Component.VTIMEZONE, target.getComponents());
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
            }
            else if (target.getComponent(Component.VTODO).isPresent()) {
                ComponentValidator.assertNone(Component.VALARM, target.getComponents());
                ComponentValidator.assertNone(Component.VFREEBUSY, target.getComponents());
//                  ComponentValidator.assertNone(Component.VEVENT, target.getComponents());
                ComponentValidator.assertNone(Component.VJOURNAL, target.getComponents());
            }
        }
    }
}
