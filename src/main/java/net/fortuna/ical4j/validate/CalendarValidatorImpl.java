package net.fortuna.ical4j.validate;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.fortuna.ical4j.model.property.immutable.ImmutableVersion.VERSION_2_0;

/**
 * Created by fortuna on 13/09/15.
 */
public class CalendarValidatorImpl implements Validator<Calendar> {

    protected final List<Class<? extends Property>> calendarProperties = new ArrayList<>();

    private final PropertyContainerRuleSet<Calendar> rules;

    @SafeVarargs
    public CalendarValidatorImpl(ValidationRule<Calendar>... rules) {
        this.rules = new PropertyContainerRuleSet<>(rules);

        Collections.addAll(calendarProperties, CalScale.class, Method.class, ProdId.class, Version.class,
                Uid.class, LastModified.class, Url.class, RefreshInterval.class, Source.class, Color.class,
                Name.class, Description.class, Categories.class, Image.class);
    }

    @Override
    public ValidationResult validate(Calendar target) throws ValidationException {
        ValidationResult result = new ValidationResult(rules.apply(Calendar.VCALENDAR, target));

        if (!CompatibilityHints.isHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION)) {
            // require VERSION:2.0 for RFC2445..
            Optional<Version> version = target.getVersion();
            if (version.isPresent() && !VERSION_2_0.equals(version.get())) {
                result.getEntries().add(new ValidationEntry("Unsupported Version: " + version.get().getValue(),
                        ValidationEntry.Severity.ERROR, Calendar.VCALENDAR));
            }
        }

        // must contain at least one component
        if (target.getComponents().isEmpty()) {
            result.getEntries().add(new ValidationEntry("Calendar must contain at least one component",
                    ValidationEntry.Severity.ERROR, Calendar.VCALENDAR));
        }

        // validate properties..
        for (final Property property : target.getProperties()) {
            boolean isCalendarProperty = calendarProperties.stream().filter(calProp -> calProp.isInstance(property)) != null;

            if (!(property instanceof XProperty) && !isCalendarProperty) {
                result.getEntries().add(new ValidationEntry("Invalid property: " + property.getName(),
                        ValidationEntry.Severity.ERROR, Calendar.VCALENDAR));
            }
        }

        // validate method..
        final Optional<Method> method = target.getMethod();
        if (method.isPresent()) {
            result = result.merge(new ITIPValidator().validate(target));

            // perform ITIP validation on components..
            for (CalendarComponent component : target.getComponents()) {
                component.validate(method.get());
            }
        }
        return result;
    }
}
