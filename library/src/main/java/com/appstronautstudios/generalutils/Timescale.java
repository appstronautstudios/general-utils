package com.appstronautstudios.generalutils;

import java.util.Calendar;

public enum Timescale {
    HOUR("hour", Calendar.HOUR_OF_DAY),
    DAY("day", Calendar.DATE),
    WEEK("week", Calendar.WEEK_OF_YEAR),
    MONTH("month", Calendar.MONTH);

    private final String key;
    private final int calendarField;

    Timescale(String key, int calendarField) {
        this.key = key;
        this.calendarField = calendarField;
    }

    public String getKey() {
        return key;
    }

    public int getCalendarField() {
        return calendarField;
    }

    /**
     * Safely resolves a raw string key (e.g., from DB or API) to a Timescale.
     */
    public static Timescale fromKey(String key) {
        if (key != null) {
            for (Timescale scale : values()) {
                if (scale.key.equalsIgnoreCase(key)) {
                    return scale;
                }
            }
        }
        return DAY; // Safe default
    }
}