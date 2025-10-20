package com.appstronautstudios.library;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import com.appstronautstudios.consentmanager.R;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

public class AppstronautUtils {

    public static Date shiftUTCToLocalDatePreservingAllCalendarComponents(long utcTimestamp) {
        // Step 1: Interpret the UTC timestamp as a UTC calendar
        Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCal.setTimeInMillis(utcTimestamp);

        int year = utcCal.get(Calendar.YEAR);
        int month = utcCal.get(Calendar.MONTH); // zero-based
        int day = utcCal.get(Calendar.DAY_OF_MONTH);

        // Step 2: Create a local calendar at 00:00:00 on that same date
        Calendar localCal = Calendar.getInstance(); // local time zone
        localCal.set(Calendar.YEAR, year);
        localCal.set(Calendar.MONTH, month);
        localCal.set(Calendar.DAY_OF_MONTH, day);
        localCal.set(Calendar.HOUR_OF_DAY, 0);
        localCal.set(Calendar.MINUTE, 0);
        localCal.set(Calendar.SECOND, 0);
        localCal.set(Calendar.MILLISECOND, 0);

        return localCal.getTime();
    }

    public static Date startOfDate(Date date) {
        Calendar calendar = Calendar.getInstance(); // local time zone
        calendar.setTime(date);

        // Set time fields to zero
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date endOfDate(Date date) {
        Calendar calendar = Calendar.getInstance(); // local time zone
        calendar.setTime(date);

        // Set to the end of the day
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    /**
     * 31.07.2025 - because of terrible foresight when we join strings for set storage in the
     * DB we don't actually check if our separator is in the set items themselves. This means
     * you can join a single tag like "rice, brown" and end up with two separate tags when you
     * parse back out again.
     *
     * @param collection - collection of strings to join
     * @param separator  - separator to join with
     * @return - a string joined by the provided separator with no duplicates. This function will
     * delete any instances of the provided separator in the collection before joining
     */
    public static String safeJoin(Collection<String> collection, String separator) {
        Set<String> safeSet = new HashSet<>();
        for (String setItem : collection) {
            String safeSetItem = setItem.replace(separator, "");
            safeSet.add(safeSetItem);
        }
        return String.join(separator, safeSet);
    }

    public static void showSimpleAlert(Activity activity, String title, String message) {
        showSimpleAlert(activity, title, message, activity.getString(R.string.ok));
    }

    public static void showSimpleAlert(Activity activity, String title, String message, String okButtonText) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(okButtonText, null)
                .show();
    }
}
