package net.fortuna.ical4j.model;

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
        if ("public".equals(locationTypeString)) {
            return Enum.valueOf(LocationType.class, "public_");
        } else {
            return Enum.valueOf(LocationType.class, locationTypeString.replace("-", "_"));
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
