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
public class CalendarValidatorImpl implements Validator<Calendar>, ContentValidator<Property> {

    protected final List<Class<? extends Property>> calendarProperties = new ArrayList<>();

    private final List<ValidationRule<Calendar>> rules;

    @SafeVarargs
    public CalendarValidatorImpl(ValidationRule<Calendar>... rules) {
        this.rules = Arrays.asList(rules);

        Collections.addAll(calendarProperties, CalScale.class, Method.class, ProdId.class, Version.class,
                Uid.class, LastModified.class, Url.class, RefreshInterval.class, Source.class, Color.class,
                Name.class, Description.class, Categories.class, Image.class);
    }

    @Override
    public void validate(Calendar target) throws ValidationException {
        for (ValidationRule<Calendar> rule : rules) {
            boolean warnOnly = CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)
                    && rule.isRelaxedModeSupported();

            if (rule.getPredicate().test(target)) {
                switch (rule.getType()) {
                    case None:
                        rule.getInstances().forEach(s -> assertNone(s, target.getProperties().getAll(), warnOnly));
                        break;
                    case One:
                        rule.getInstances().forEach(s -> assertOne(s, target.getProperties().getAll(), warnOnly));
                        break;
                    case OneOrLess:
                        rule.getInstances().forEach(s -> assertOneOrLess(s, target.getProperties().getAll(), warnOnly));
                        break;
                    case OneOrMore:
                        rule.getInstances().forEach(s -> assertOneOrMore(s, target.getProperties().getAll(), warnOnly));
                        break;
                }
            }
        }

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            // require VERSION:2.0 for RFC2445..
            Optional<Version> version = target.getProperties().getFirst(Property.VERSION);
            if (version.isPresent() && !Version.VERSION_2_0.equals(version.get())) {
                throw new ValidationException("Unsupported Version: " + version.get().getValue());
            }
        }

        // must contain at least one component
        if (target.getComponents().getAll().isEmpty()) {
            throw new ValidationException("Calendar must contain at least one component");
        }

        // validate properties..
        for (final Property property : target.getProperties().getAll()) {
            boolean isCalendarProperty = calendarProperties.parallelStream().anyMatch(calProp -> calProp.isInstance(property));

            if (!(property instanceof XProperty) && !isCalendarProperty) {
                throw new ValidationException("Invalid property: " + property.getName());
            }
        }

//        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
        // validate method..
        final Optional<Method> method = target.getProperties().getFirst(Property.METHOD);
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
            for (CalendarComponent component : target.getComponents().getAll()) {
                component.validate(method.get());
            }
        }
    }

    public static class PublishValidator implements Validator<Calendar>, ContentValidator<CalendarComponent> {

        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponents().getFirst(Component.VEVENT).isPresent()) {
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);

                boolean warnOnly = CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION);
                assertNone(Component.VTODO, target.getComponents().getAll(), warnOnly);
            }
            else if (target.getComponents().getFirst(Component.VFREEBUSY).isPresent()) {
                assertNone(Component.VTODO, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTIMEZONE, target.getComponents().getAll(), false);
                assertNone(Component.VALARM, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VTODO).isPresent()) {
//                    assertNone(Component.VFREEBUSY, target.getComponents());
//                    assertNone(Component.VEVENT, target.getComponents());
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
            }
//                else if (target.getComponents().getFirst(Component.VJOURNAL) != null) {
//                    assertNone(Component.VFREEBUSY, target.getComponents());
//                    assertNone(Component.VEVENT, target.getComponents());
//                    assertNone(Component.VTODO, target.getComponents());
//                }
        }
    }

    public static class RequestValidator implements Validator<Calendar>, ContentValidator<CalendarComponent> {

        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponents().getFirst(Component.VEVENT).isPresent()) {
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTODO, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VFREEBUSY).isPresent()) {
                assertNone(Component.VTODO, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTIMEZONE, target.getComponents().getAll(), false);
                assertNone(Component.VALARM, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VTODO).isPresent()) {
//                  assertNone(Component.VFREEBUSY, target.getComponents());
//                  assertNone(Component.VEVENT, target.getComponents());
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
            }
        }
    }

    public static class ReplyValidator implements Validator<Calendar>, ContentValidator<CalendarComponent> {

        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponents().getFirst(Component.VEVENT).isPresent()) {
                assertOneOrLess(Component.VTIMEZONE, target.getComponents().getAll(), false);

                assertNone(Component.VALARM, target.getComponents().getAll(), false);
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTODO, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VFREEBUSY).isPresent()) {
                assertNone(Component.VTODO, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTIMEZONE, target.getComponents().getAll(), false);
                assertNone(Component.VALARM, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VTODO).isPresent()) {
                assertOneOrLess(Component.VTIMEZONE, target.getComponents().getAll(), false);

                assertNone(Component.VALARM, target.getComponents().getAll(), false);
//                  assertNone(Component.VFREEBUSY, target.getComponents());
//                  assertNone(Component.VEVENT, target.getComponents());
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
            }
        }
    }

    public static class AddValidator implements Validator<Calendar>, ContentValidator<CalendarComponent> {

        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponents().getFirst(Component.VEVENT).isPresent()) {
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTODO, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VTODO).isPresent()) {
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
//                  assertNone(Component.VEVENT, target.getComponents());
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VJOURNAL).isPresent()) {
                assertOneOrLess(Component.VTIMEZONE, target.getComponents().getAll(), false);

                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
//                  assertNone(Component.VEVENT, target.getComponents());
//                  assertNone(Component.VTODO, target.getComponents());
            }
        }
    }

    public static class CancelValidator implements Validator<Calendar>, ContentValidator<CalendarComponent> {

        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponents().getFirst(Component.VEVENT).isPresent()) {
                assertNone(Component.VALARM, target.getComponents().getAll(), false);
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTODO, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VTODO).isPresent()) {
                assertOneOrLess(Component.VTIMEZONE, target.getComponents().getAll(), false);

                assertNone(Component.VALARM, target.getComponents().getAll(), false);
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
//                  assertNone(Component.VEVENT, target.getComponents());
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VJOURNAL).isPresent()) {
                assertNone(Component.VALARM, target.getComponents().getAll(), false);
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
//                  assertNone(Component.VEVENT, target.getComponents());
//                  assertNone(Component.VTODO, target.getComponents());
            }
        }
    }

    public static class RefreshValidator implements Validator<Calendar>, ContentValidator<CalendarComponent> {

        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponents().getFirst(Component.VEVENT).isPresent()) {
                assertNone(Component.VALARM, target.getComponents().getAll(), false);
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTODO, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VTODO).isPresent()) {
                assertNone(Component.VALARM, target.getComponents().getAll(), false);
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
//                  assertNone(Component.VEVENT, target.getComponents());
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTIMEZONE, target.getComponents().getAll(), false);
            }
        }
    }

    public static class CounterValidator implements Validator<Calendar>, ContentValidator<CalendarComponent> {

        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponents().getFirst(Component.VEVENT).isPresent()) {
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTODO, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VTODO).isPresent()) {
                assertOneOrLess(Component.VTIMEZONE, target.getComponents().getAll(), false);

                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
//                  assertNone(Component.VEVENT, target.getComponents());
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
            }
        }
    }

    public static class DeclineCounterValidator implements Validator<Calendar>, ContentValidator<CalendarComponent> {

        @Override
        public void validate(Calendar target) throws ValidationException {
            if (target.getComponents().getFirst(Component.VEVENT).isPresent()) {
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
                assertNone(Component.VTODO, target.getComponents().getAll(), false);
                assertNone(Component.VTIMEZONE, target.getComponents().getAll(), false);
                assertNone(Component.VALARM, target.getComponents().getAll(), false);
            }
            else if (target.getComponents().getFirst(Component.VTODO).isPresent()) {
                assertNone(Component.VALARM, target.getComponents().getAll(), false);
                assertNone(Component.VFREEBUSY, target.getComponents().getAll(), false);
//                  assertNone(Component.VEVENT, target.getComponents());
                assertNone(Component.VJOURNAL, target.getComponents().getAll(), false);
            }
        }
    }
}
