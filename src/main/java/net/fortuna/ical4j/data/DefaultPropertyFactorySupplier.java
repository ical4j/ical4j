package net.fortuna.ical4j.data;

import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.property.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class DefaultPropertyFactorySupplier implements Supplier<List<PropertyFactory>> {

    @Override
    public List<PropertyFactory> get() {
        List<PropertyFactory> rfc5545 = Arrays.asList(new Acknowledged.Factory(),
                new Action.Factory(), new Attach.Factory(), new Attendee.Factory(), new BusyType.Factory(),
                new CalScale.Factory(), new Categories.Factory(), new Clazz.Factory(), new Comment.Factory(),
                new Completed.Factory(), new Contact.Factory(), new Country.Factory(), new Created.Factory(),
                new Description.Factory(), new DtEnd.Factory(), new DtStamp.Factory(), new DtStart.Factory(),
                new Due.Factory(), new Duration.Factory(), new ExDate.Factory(), new ExRule.Factory(),
                new ExtendedAddress.Factory(), new FreeBusy.Factory(), new Geo.Factory(), new LastModified.Factory(),
                new Locality.Factory(), new Location.Factory(), new LocationType.Factory(), new Method.Factory(),
                new Name.Factory(), new Organizer.Factory(), new PercentComplete.Factory(), new Postalcode.Factory(),
                new Priority.Factory(), new ProdId.Factory(), new RDate.Factory(), new RecurrenceId.Factory(),
                new Region.Factory(), new RelatedTo.Factory(), new Repeat.Factory(), new RequestStatus.Factory(),
                new Resources.Factory(), new RRule.Factory(), new Sequence.Factory(), new Status.Factory(),
                new StreetAddress.Factory(), new Summary.Factory(), new Tel.Factory(), new Transp.Factory(),
                new Trigger.Factory(), new TzId.Factory(), new TzName.Factory(), new TzOffsetFrom.Factory(),
                new TzOffsetTo.Factory(), new TzUrl.Factory(), new Uid.Factory(), new Url.Factory(),
                new Version.Factory());

        return rfc5545;
    }
}
