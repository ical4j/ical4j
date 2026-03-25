package net.fortuna.ical4j.model;

import net.fortuna.ical4j.util.Enums;

/**
 * Location types as defined by the Location Types Registry (RFC4589): https://tools.ietf.org/html/rfc4589
 */
public enum LocationType {
    aircraft, airport, arena, automobile, bank, bar, bicycle, bus, bus_station, cafe, classroom,
    club, construction, convention_center, government, hospital, hotel, industrial, library,
    motorcycle, office, other, outdoors, parking, place_of_worship, prison, public_, public_transport,
    residence, restaurant, school, shopping_area, stadium, store, street, theater, train, train_station,
    truck, underway, unknown, warehouse, water, watercraft;

    public static LocationType from(String locationTypeString) {
        if ("public".equalsIgnoreCase(locationTypeString)) {
            return Enums.parse(LocationType.class, "public_", LocationType.class.getSimpleName());
        } else {
            return Enums.parse(LocationType.class, locationTypeString.replace("-", "_"), LocationType.class.getSimpleName());
        }
    }

    @Override
    public String toString() {
        if (this == public_) {
            return "public";
        } else {
            return super.toString().replace("_", "-");
        }
    }
}
