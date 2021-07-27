package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.property.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class DefaultPropertyFactorySupplier implements Supplier<List<PropertyFactory<? extends Property>>> {

    @Override
    public List<PropertyFactory<? extends Property>> get() {
        List<PropertyFactory<? extends Property>> rfc5545 =
                Arrays.asList(
                        new Acknowledged.Factory(),
                        new Action.Factory(),
                        new Attach.Factory(),
                        new Attendee.Factory(),
                        new CalScale.Factory(),
                        new Categories.Factory(),
                        new Clazz.Factory(),
                        new Comment.Factory(),
                        new Completed.Factory(),
                        new Contact.Factory(),
                        new Created.Factory(),
                        new Description.Factory(),
                        new DtEnd.Factory(),
                        new DtStamp.Factory(),
                        new DtStart.Factory(),
                        new Due.Factory(),
                        new Duration.Factory(),
                        new ExDate.Factory(),
                        new ExRule.Factory(),
                        new FreeBusy.Factory(),
                        new Geo.Factory(),
                        new LastModified.Factory(),
                        new Location.Factory(),
                        new Method.Factory(),
                        new Organizer.Factory(),
                        new PercentComplete.Factory(),
                        new Priority.Factory(),
                        new ProdId.Factory(),
                        new RDate.Factory(),
                        new RecurrenceId.Factory(),
                        new RelatedTo.Factory(),
                        new Repeat.Factory(),
                        new RequestStatus.Factory(),
                        new Resources.Factory(),
                        new RRule.Factory(),
                        new Sequence.Factory(),
                        new Status.Factory(),
                        new Summary.Factory(),
                        new Transp.Factory(),
                        new Trigger.Factory(),
                        new TzId.Factory(),
                        new TzName.Factory(),
                        new TzOffsetFrom.Factory(),
                        new TzOffsetTo.Factory(),
                        new TzUrl.Factory(),
                        new Uid.Factory(),
                        new Url.Factory(),
                        new Version.Factory());

        // New properties
        List<PropertyFactory<? extends Property>> rfc7986 =
                Arrays.asList(
                        new Color.Factory(),
                        new Conference.Factory(),
                        new Image.Factory(),
                        new Name.Factory(),
                        new RefreshInterval.Factory(),
                        new Source.Factory());

        List<PropertyFactory<? extends Property>> vvenue =
                Arrays.asList(
                        new Country.Factory(),
                        new ExtendedAddress.Factory(),
                        new Locality.Factory(),
                        new Postalcode.Factory(),
                        new Region.Factory(),
                        new StreetAddress.Factory(),
                        new Tel.Factory()
                );

        // Availability
        final List<PropertyFactory<? extends Property>> rfc7953 =
                Collections.singletonList(new BusyType.Factory());

        // Event pub
        final List<PropertyFactory<? extends Property>> rfc9073 =
                Arrays.asList(
                        new CalendarAddress.Factory(),
                        new LocationType.Factory(),
                        new ParticipantType.Factory(),
                        new ResourceType.Factory(),
                        new StructuredData.Factory(),
                        new StyledDescription.Factory());

        List<PropertyFactory<? extends Property>> factories = new ArrayList<>(rfc5545);
        factories.addAll(rfc7953);
        factories.addAll(rfc7986);
        factories.addAll(rfc9073);

        return factories;
    }
}
