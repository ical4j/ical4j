## ADDED Requirements

### Requirement: Validator factory lookup SHALL fall back to the default implementation

`AbstractCalendarValidatorFactory.getInstance()` SHALL return the `CalendarValidatorFactory` registered via `ServiceLoader` when one is visible to `DefaultCalendarValidatorFactory`'s class loader. When no registration is visible (for example because `META-INF/services` was stripped by ProGuard/R8 or a packaging tool), it SHALL return a `DefaultCalendarValidatorFactory` instance. Class initialisation SHALL NOT fail because a registration is absent.

#### Scenario: No service registration visible

- **WHEN** `AbstractCalendarValidatorFactory` is initialised in a class loader that returns no resources for `META-INF/services/net.fortuna.ical4j.validate.CalendarValidatorFactory`
- **THEN** no `ExceptionInInitializerError` or `NoSuchElementException` is thrown
- **AND** `getInstance()` returns a `DefaultCalendarValidatorFactory`

#### Scenario: Service registration present

- **WHEN** the bundled service registration is on the classpath
- **THEN** `getInstance()` returns the registered factory (`DefaultCalendarValidatorFactory` by default)
