package net.fortuna.ical4j.util;

public final class Enums {

    private Enums() {
    }

    public static <T extends Enum<T>> T parse(Class<T> enumClass, String value, String parameter) {
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Invalid value " + value + " for " + parameter);
    }

    public static <T extends Enum<T>> T parse(Class<T> enumClass, String value) {
        return parse(enumClass, value, enumClass.getSimpleName());
    }
}
