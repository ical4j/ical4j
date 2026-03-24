package net.fortuna.ical4j.util;

public final class Enums {

    private Enums() {
    }

    public static <T extends Enum<T>> T parse(Class<T> enumClass, String value) {
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumClass.getCanonicalName() + "." + value);
    }
}
