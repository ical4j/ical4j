# iCal4j - iCalendar parser and object model

[RFC2445]: https://tools.ietf.org/html/rfc2445
[RFC2446]: https://tools.ietf.org/html/rfc2446
[RFC2447]: https://tools.ietf.org/html/rfc2447

[RFC5545]: https://tools.ietf.org/html/rfc5545
[RFC5546]: https://tools.ietf.org/html/rfc5546
[RFC6047]: https://datatracker.ietf.org/doc/html/rfc6047
[RFC6868]: https://datatracker.ietf.org/doc/html/rfc6868
[RFC7808]: https://datatracker.ietf.org/doc/html/rfc7808
[RFC7953]: https://datatracker.ietf.org/doc/html/rfc7953
[RFC7986]: https://datatracker.ietf.org/doc/html/rfc7986
[RFC7529]: https://datatracker.ietf.org/doc/html/rfc7529
[RFC9073]: https://datatracker.ietf.org/doc/html/rfc9073
[RFC9074]: https://datatracker.ietf.org/doc/html/rfc9074
[RFC9253]: https://www.rfc-editor.org/rfc/rfc9253.html

[mavenrepo]: https://mvnrepository.com/artifact/org.mnode.ical4j/ical4j

[Introduction]: #introduction

[Setup]: #setup
[System requirements]: #system-requirements
[Release downloads]: #release-downloads
[Install with Maven]: #install-with-maven
[Install with Gradle]: #install-with-gradle

[Usage]: #usage

[Examples]: #examples

[References]: #references
[Specifications]: #specifications

[Configuration]: #configuration
[Compatibility Hints]: #compatibility-hints

[Limitations]: #limitations

[Development]: #development
[Building with Gradle]: #building-with-gradle
[Redistribution]: #redistribution
[Contributing]: #contributing

#### Table of Contents

1. [Introduction - What is iCal4j?][Introduction]
2. [Setup - Download and installation of iCal4j][Setup]
    - [System requirements - What is required to use iCal4j][System requirements]
    - [Release downloads - Where to get iCal4j][Release downloads]
    - [Install with Maven]
    - [Install with Gradle]
3. [Usage - The iCal4j object model and how to use it][Usage]
    - [Examples - common usage scenarios][Examples]
4. [References][References]
5. [Configuration options][Configuration]
    - [Compatibility Hints]
6. [Limitations - CUA compatibility, etc.][Limitations]
7. [Development - Guide for contributing to the iCalj project][Development]
    - [Building with Gradle]
    - [Redistribution]
    - [Contributing to iCal4j][Contributing]

## Introduction

iCal4j is a Java library used to read and write iCalendar data streams as defined in [RFC2445]. The iCalendar standard
provides a common data format used to store information about calendar-specific data such as events, appointments, to-do
lists, etc. All of the popular calendaring tools, such as Lotus Notes, Outlook and Apple's iCal also support the iCalendar
standard.

 - For a concise description of the goals and directions of iCal4j please
 take a look at the [open issues](https://github.com/ical4j/ical4j/issues).

 - You will find examples of how to use iCal4j in [the official website](https://www.ical4j.org/examples/overview/)
 and throughout the [API documentation](https://ical4j.github.io/docs/ical4j/api).

 - Detailed descriptions of changes included in each release may be found
 in the [CHANGELOG](https://ical4j.github.io/docs/ical4j/release-notes).
 
 - iCal4j was created with the help of [Open Source](http://opensource.org) software.


## Setup

### System requirements

 - Version 4.x - Java 8 or later
 - Version 3.x - Java 8 or later
 - Version 2.x - Java 7 or later

### Dependencies

In the interests of portability and compatibility with as many environments as possible, the number of dependent
libraries for iCal4j is kept to a minimum. The following describes the required (and optional) dependencies and the
functionality they provide.

* slf4j-api [required] - A logging meta-library with integration to different logging framework implementations. Used in all classes that require logging.

* commons-lang3 [required] - Provides enhancements to the standard Java library, including support for custom `equals()` and `hashcode()`
implementations. Used in all classes requiring custom equality implementations.

* commons-collections4 [required] - Provides enhancements to the standard Java collections API, including support for closures. Used in `net.fortuna.ical4j.validate.Validator` implementations to reduce the duplication of code in validity checks.

* javax.cache.cache-api [optional*] - Supports caching timzeone definitions. * NOTE: when not included you must set
a value for the `net.fortuna.ical4j.timezone.cache.impl` configuration

* commons-codec [optional] - Provides support for encoding and decoding binary data in text form. Used in `net.fortuna.ical4j.model.property.Attach`
 
* groovy-all [optional] - The runtime for the Groovy language. Required for library enhancements such as iCalendar object construction using
the `net.fortuna.ical4j.model.ContentBuilder` DSL. This library is optional for all non-Groovy features of iCal4j.

* bndlib [optional] - A tool for generating OSGi library metadata and packaging OSGi bundles. This library is not a runtime requirement, and
is used only to generate version information in the javadoc API documentation.
 

### Release Downloads

* [mavenrepo]

### Install with Maven

### Install with Gradle


## Usage

### Examples


## References

* [RFC5545] - Internet Calendaring and Scheduling Core Object Specification (iCalendar)
* [RFC5546] - iCalendar Transport-Independent Interoperability Protocol (iTIP)
* [RFC6047] - iCalendar Message-Based Interoperability Protocol (iMIP)
* [RFC6868] - Parameter Value Encoding in iCalendar and vCard
* [RFC7953] - Calendar Availability
* [RFC7808] - Time Zone Data Distribution Service
* [RFC7986] - New Properties for iCalendar
* [RFC7529] - Non-Gregorian Recurrence Rules in iCalendar
* [RFC9073] - Event Publishing Extensions to iCalendar
* [RFC9074] - "VALARM" Extensions for iCalendar
* [RFC9253] - Support for iCalendar Relationships


## Configuration

    net.fortuna.ical4j.parser=net.fortuna.ical4j.data.HCalendarParserFactory

    net.fortuna.ical4j.timezone.registry=net.fortuna.ical4j.model.DefaultTimeZoneRegistryFactory

    net.fortuna.ical4j.timezone.update.enabled={true|false}

    net.fortuna.ical4j.factory.decoder=net.fortuna.ical4j.util.DefaultDecoderFactory

    net.fortuna.ical4j.factory.encoder=net.fortuna.ical4j.util.DefaultEncoderFactory

    net.fortuna.ical4j.recur.maxincrementcount=1000
    
    net.fortuna.ical4j.timezone.cache.impl=net.fortuna.ical4j.util.MapTimeZoneCache


### Compatibility Hints
 
#### Relaxed Parsing
    
    ical4j.parsing.relaxed={true|false}

 iCal4j now has the capability to "relax" its parsing rules to enable parsing of
 *.ics files that don't properly conform to the iCalendar specification (RFC2445)
 
 This property is intended as a general relaxation of parsing rules to allow for parsing
 otherwise invalid calendar files. Initially enabling this property will allow for the
 creation of properties and components with illegal names (e.g. Mozilla Calendar's "X"
 property). Note that although this will allow for parsing calendars with illegal names,
 validation will still identify such names as an error in the calendar model.
 
 - You can relax iCal4j's unfolding rules by specifying the following system property:
    
        ical4j.unfolding.relaxed={true|false}
 
 Note that I believe this problem is not restricted to Mozilla calendaring
 products, but rather may be caused by UNIX/Linux-based applications relying on the
 default newline character (LF) to fold long lines (KOrganizer also seems to have this
 problem). This is, however, still incorrect as by definition long lines are folded
 using a (CRLF) combination.
 
 I've obtained a couple of samples of non-standard iCalendar files that I've included
 in the latest release (0.9.11). There is a Sunbird, phpicalendar, and a KOrganizer
 sample there (open them in Notepad on Windows to see what I mean).

 It seems that phpicalendar and KOrganizer always use LF instead of CRLF, and in
 addition KOrganizer seems to fold all property parameters and values (similar to
 Mozilla Calendar/Sunbird).

 Mozilla Calendar/Sunbird uses CRLF to fold all property parameter/values, however it
 uses just LF to fold long lines (i.e. longer than 75 characters).

 The latest release of iCal4j includes changes to UnfoldingReader that should work
 correctly with Mozilla Calendar/Sunbird, as long as the ical4j.unfolding.relaxed
 system property is set to true.

 KOrganizer/phpicalendar files should also work with the relaxed property, although
 because ALL lines are separated with just LF it also relies on the StreamTokenizer to
 correctly identify LF as a newline on Windows, and CRLF as a newline on UNIX/Linux. The
 API documentation for Java 1.5 says that it does do this, so if you still see problems
 with parsing it could be a bug in the Java implementation.

 
 The full set of system properties may be found in
 net.fortuna.ical4j.util.CompatibilityHints.


#### iCal4j and Timezones

    net.fortuna.ical4j.timezone.date.floating={true|false}

 Supporting timezones in an iCalendar implementation can be a complicated process,
 mostly due to the fact that there is not a definitive list of timezone definitions
 used by all iCalendar implementations. This means that an iCalendar file may be
 produced by one implementation and, if the file does not include all definitions
 for timezones relevant to the calendar properties, an alternate implementation
 may not know how to interpret the timezone identified in the calendar (or worse,
 it may interpret the timezone differently to the original implementation). All
 of these possibilities mean unpredictable behaviour which, to put it nicely, is
 not desireable.
 
 iCal4j approaches the problem of timezones in two ways: The first and by far the
 preferred approach is for iCalendar files to include definitions for all timezones
 referenced in the calendar object. To support this, when an existing calendar is
 parsed a list of VTimeZone definitions contained in the calendar is constructed.
 This list may then be queried whenever a VTimeZone definition is required.
 
 The second approach is to rely on a registry of VTimeZone definitions. iCal4j
 includes a default registry of timezone definitions (derived from the Olson timezone
 database - a defacto standard for timezone definitions), or you may also provide your
 own registry implementation from which to retreieve timezones. This approach is
 required when constructing new iCalendar files.
 
 Note that the intention of the iCal4j model is not to provide continuous validation
 feedback for every change in the model. For this reason you are free to change
 timezones on Time objects, remove or add TzId parameters, remove or add VTimeZone
 definitions, etc. without restriction. However when validation is run (automatically
 on output of the calendar) you will be notified if the changes are invalid.

#### Validation
    
    ical4j.validation.relaxed={true|false}


#### Micosoft Outlook compatibility
    
    ical4j.compatibility.outlook={true|false}

Behaviour:

* Enforces a folding length of 75 characters (by default ical4j will fold at 73 characters)
* Allows for spaces when parsing a WEEKDAY list

Microsoft Outlook also appears to provide quoted TZID parameter values, as follows:
 
    DTSTART;TZID="Pacific Time (US & Canada),Tijuana":20041011T223000

#### Lotus Notes compatibility

    ical4j.compatibility.notes={true|false}
 

## Limitations


 
## Development

### Building with Gradle

iCal4j includes the Gradle wrapper for a simpler and more consistent build.

**Run unit tests**

    ./gradlew clean test

**Build a new release**

    ./gradlew clean test release -Prelease.forceVersion=2.0.0

**Upload release binaries and packages**

    RELEASE_VERSION=2.0.0 ./gradlew uploadArchives uploadDist

### Redistribution

If you intend to use and distribute iCal4j in your own project please
follow these very simple guidelines:
 
 - Make a copy of the LICENSE, rename it to LICENSE.ical4j, and save
 it to the directory where you are re-distributing the iCal4j JAR.
 
 - I don't recommend extracting the iCal4j classes from its JAR and package
 in another JAR along with other classes. It may lead to version incompatibilites
 in the future. Rather I would suggest to include the ical4j.jar in your classpath
 as required.

### Contributing

Open source software is made stronger by the community that supports it. Through participation you not only contribute to the quality of the software, but also gain a deeper insight into the inner workings.

Contributions may be in the form of feature enhancements, bug fixes, test cases, documentation and forum participation. If you have a question, just ask. If you have an answer, write it down.

And if you are somehow constrained from participation, through corporate policy or otherwise, consider financial support. After all, if you are profiting from open source it's only fair to give something back to the community that make it all possible.
