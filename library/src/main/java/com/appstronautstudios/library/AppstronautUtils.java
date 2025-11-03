package com.appstronautstudios.library;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.exifinterface.media.ExifInterface;

import com.appstronautstudios.consentmanager.R;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AppstronautUtils {

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public static String getVersionName(Context context) {
        String version = "0.0";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * Generates an array of colours using Random(). These colours are true random and can be hues
     * of black and gray and might end up with poor distinctiveness.
     *
     * @param size size of colour set to generate
     * @return int colour array
     */
    private static int[] generateRandomColourSet(int size) {
        int[] colours = new int[size];
        Random rnd = new Random();

        for (int i = 0; i < size; i++) {
            colours[i] = Color.argb(
                    255,
                    rnd.nextInt(256),
                    rnd.nextInt(256),
                    rnd.nextInt(256)
            );
        }
        return colours;
    }

    /**
     * Generates an array of colours spaced evenly using HSV hue steps.
     * Produces predictable, well-spaced colours.
     *
     * @param size size of colour set to generate
     * @return int colour array
     */
    private static int[] generateDistinctColourSet(int size) {
        int[] colours = new int[size];

        float saturation = 0.6f;
        float brightness = 0.85f;

        for (int i = 0; i < size; i++) {
            float hue = (360f / size) * i;
            colours[i] = Color.HSVToColor(new float[]{hue, saturation, brightness});
        }
        return colours;
    }

    /**
     * Generates a colour set prefixed by provided base colour set. Can fill out the remainder with
     * either true random colours or HSV hue step colours.
     *
     * @param baseColourSet   any base colours to start the set with. Can be empty or null if unnecessary
     * @param size            size of colour set to generate
     * @param distinctColours if true, generate evenly spaced colours;
     *                        otherwise generate random colours.
     * @return - int colour array
     */
    public static int[] getColourSet(int[] baseColourSet, int size, boolean distinctColours) {
        int[] result = new int[size];

        // Copy all base colours needed from provided set (if present)
        int baseCount;
        if (baseColourSet != null) {
            baseCount = Math.min(baseColourSet.length, size);
        } else {
            baseCount = 0;
        }
        if (baseCount > 0) {
            System.arraycopy(baseColourSet, 0, result, 0, baseCount);
        }

        // Generate remaining colours (if necessary) and then copy them into the result set
        int remaining = size - baseCount;
        if (remaining > 0) {
            int[] generated;
            if (distinctColours) {
                generated = generateDistinctColourSet(remaining);
            } else {
                generated = generateRandomColourSet(remaining);
            }
            System.arraycopy(generated, 0, result, baseCount, remaining);
        }

        return result;
    }

    /**
     * Note this uses local time calendars for calculations NOT UTC
     *
     * @param startTimestamp - timestamp of where to start
     * @param endTimestamp   - timestamp of where to end
     * @param timeScale      - timescale to skip by. Valid values are "day", "month", "year"
     * @param fake           - create a fake grouping irrespective of other entered data
     * @return - ArrayList of Dates from the start of the startTimestamp day to end of the endTimestamp day
     */
    public static ArrayList<Date> setupNewXVals(long startTimestamp, long endTimestamp, String timeScale, boolean fake) {
        ArrayList<Date> xValsRet = new ArrayList<>();

        if (fake) {
            // one fake entry for each month
            Calendar cal = Calendar.getInstance();
            cal.set(2019, Calendar.JANUARY, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);

            for (int i = 0; i < 12; i++) {
                Calendar temp = (Calendar) cal.clone();
                temp.set(Calendar.MONTH, i);
                xValsRet.add(temp.getTime());
            }

            return xValsRet;
        }

        Calendar beginCalendar = Calendar.getInstance();
        Calendar finishCalendar = Calendar.getInstance();

        beginCalendar.setTimeInMillis(startTimestamp);
        beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
        beginCalendar.set(Calendar.MINUTE, 0);
        beginCalendar.set(Calendar.SECOND, 0);
        beginCalendar.set(Calendar.MILLISECOND, 0);

        finishCalendar.setTimeInMillis(endTimestamp);
        finishCalendar.set(Calendar.HOUR_OF_DAY, 23);
        finishCalendar.set(Calendar.MINUTE, 59);
        finishCalendar.set(Calendar.SECOND, 59);
        finishCalendar.set(Calendar.MILLISECOND, 999);

        // create xVals at intervals until we're at end date
        while (beginCalendar.getTimeInMillis() <= finishCalendar.getTimeInMillis()) {
            xValsRet.add(beginCalendar.getTime());

            switch (timeScale) {
                case "day":
                    beginCalendar.add(Calendar.DATE, 1);
                    break;
                case "week":
                    beginCalendar.add(Calendar.WEEK_OF_YEAR, 1);
                    break;
                case "month":
                    beginCalendar.add(Calendar.MONTH, 1);
                    break;
                default:
                    // better to crash if client supplies incorrect timescale than silently return
                    // month or something as a default
                    throw new IllegalArgumentException("Invalid timeScale: " + timeScale);
            }
        }

        return xValsRet;
    }

    public static boolean isInTimeWindow(long target, long windowStart, long windowEnd) {
        return target >= windowStart && target <= windowEnd;
    }

    public static Date utcDateToLocalMidnight(long utcTimestamp) {
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

    public static String timestampToCsvDate(long timestamp) {
        Date date = new Date(timestamp);
        String outDate = null;
        try {
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            outDate = fmtOut.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outDate;
    }

    public static String timestampToSimpleDate(long timestamp) {
        Date date = new Date(timestamp);
        String outDate = null;
        try {
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            outDate = fmtOut.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outDate;
    }

    public static Date csvDateToDateObject(String dateString) {
        Date outDate = null;
        try {
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            outDate = fmtOut.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outDate;
    }

    public static String sanitizeStringForCSV(String input) {
        return input
                .replace("\"", "")
                .replace("\'", "")
                .replace("\\", "");
    }

    public static byte[] getBytes(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        DataInputStream dis = new DataInputStream(bis);
        dis.readFully(bytes);
        return bytes;
    }

    public static String getMimeType(Context context, Uri uri) {
        String mimeType = null;

        // Try using ContentResolver first
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver contentResolver = context.getContentResolver();
            mimeType = contentResolver.getType(uri);
        }

        // Fallback to file extension
        if (mimeType == null) {
            mimeType = getMimeType(uri.toString());
        }

        return mimeType;
    }

    public static String getMimeType(String url) {
        // https://stackoverflow.com/a/8591230/740474
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static long getDaysSinceInstall(Context context) {
        long installTs = System.currentTimeMillis();
        PackageManager packMan = context.getPackageManager();
        try {
            PackageInfo pkgInfo = packMan.getPackageInfo(context.getPackageName(), 0);
            installTs = pkgInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - installTs);
    }

    /**
     * A string joined by the provided separator with duplicates removed. This function will also
     * delete any instances of the provided separator in the collection before joining. Order is
     * preserved
     *
     * @param collection collection of strings to join
     * @param separator  separator to join with
     * @return joined string
     */
    public static String safeJoin(Collection<String> collection, String separator) {
        Set<String> safeSet = new LinkedHashSet<>();
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

    public static int getRotationDegrees(int exifOrientation) {
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    public static void spoofOnResume(Activity activity) {
        // https://stackoverflow.com/a/15951960/740474
        Intent intent = new Intent(activity, activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
    }

    public static String sanitizeStringForFirebase(String input) {
        // sanitize illegal key characters
        String sanitizedString = input;
        sanitizedString = sanitizedString.replace(".", "");
        sanitizedString = sanitizedString.replace("$", "");
        sanitizedString = sanitizedString.replace("[", "");
        sanitizedString = sanitizedString.replace("]", "");
        sanitizedString = sanitizedString.replace("#", "");
        sanitizedString = sanitizedString.replace("/", "");
        // don't let them mess with the json structure
        sanitizedString = sanitizedString.replace("{", "");
        sanitizedString = sanitizedString.replace("}", "");
        // remove useless characters
        sanitizedString = sanitizedString.replace("\"", "");
        sanitizedString = sanitizedString.replace("\'", "");
        sanitizedString = sanitizedString.trim();

        return sanitizedString;
    }


    public static String getSystemLocale(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                } else {
                    return "non-CDMA fail";
                }
            } else {
                return "network & sim both fail";
            }
        } catch (Exception e) {
            return e.getClass().getCanonicalName();
        }
    }

    public static Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapFromView(View view) {
        // define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_4444);
        // bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        // set background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            // has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            // does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.TRANSPARENT);
        }
        // draw the view on the canvas
        view.draw(canvas);
        // return the bitmap
        return returnedBitmap;
    }

    public static String getNumberString(double number, int decimalPlaces, boolean signed) {
        return getNumberString(number, decimalPlaces, signed, false);
    }

    public static String getNumberString(double number, int decimalPlaces, boolean signed, boolean forcedZero) {
        DecimalFormat myFormatter = getDecimalFormat(decimalPlaces, forcedZero);

        String result = myFormatter.format(number);

        if (signed) {
            if (number > 0) {
                result = "+" + result;
            } else if (number < 0) {
                // number is negative, result already has "-"
                // nothing to add
            } else {
                // number == 0
                result = "+" + result;
            }
        }

        return result;
    }

    public static @NonNull DecimalFormat getDecimalFormat(int decimalPlaces, boolean forcedZero) {
        DecimalFormatSymbols DFS = new DecimalFormatSymbols();
        DFS.setDecimalSeparator('.');

        // Build pattern dynamically
        StringBuilder pattern = new StringBuilder("#");
        if (decimalPlaces > 0) {
            pattern.append(".");
            for (int i = 0; i < decimalPlaces; i++) {
                // # would indicate optional zero. E.g. 1.2 with 3 decimal places would
                // be 1.200 with forced zero and 1.2 with optional zero
                if (forcedZero) {
                    pattern.append("0");
                } else {
                    pattern.append("#");
                }
            }
        }

        DecimalFormat myFormatter = new DecimalFormat(pattern.toString());
        myFormatter.setDecimalFormatSymbols(DFS);
        return myFormatter;
    }
}
