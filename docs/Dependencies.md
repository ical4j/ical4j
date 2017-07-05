# iCal4j Dependencies

In the interests of portability and compatibility with as many environments as possible, the number of dependent
libraries for iCal4j is kept to a minimum. The following describes the required (and optional) dependencies and the
functionality they provide.

## slf4j-api [required]

A logging meta-library with integration to different logging framework implementations.

Used in all classes that require logging.

## commons-lang3 [required]

Provides enhancements to the standard Java library, including support for custom `equals()` and `hashcode()`
implementations.

Used in all classes requiring custom equality implementations.

## commons-collections4 [required]

Provides enhancements to the standard Java collections API, including support for closures.

Used in `net.fortuna.ical4j.validate.Validator` implementations to reduce the duplication of code in validity checks.

## commons-codec [optional]

Provides support for encoding and decoding binary data in text form.

Used in `net.fortuna.ical4j.model.property.Attach`

## groovy-all [optional]

The runtime for the Groovy language. Required for library enhancements such as iCalendar object construction using
the `net.fortuna.ical4j.model.ContentBuilder` DSL. This library is optional for all non-Groovy features of iCal4j.

## bndlib [optional]

A tool for generating OSGi library metadata and packaging OSGi bundles. This library is not a runtime requirement, and
is used only to generate version information in the javadoc API documentation.
