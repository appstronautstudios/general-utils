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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.appcompat.app.AlertDialog;

import com.appstronautstudios.consentmanager.R;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
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
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static boolean isInTimeWindow(long target, long windowStart, long windowEnd) {
        return target >= windowStart && target <= windowEnd;
    }

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


    public static String getNumberString(double number, int sigFigs, boolean signed) {
        DecimalFormatSymbols DFS = new DecimalFormatSymbols();
        DFS.setDecimalSeparator('.');
        DecimalFormat myFormatter;

        switch (sigFigs) {
            default:
            case 0: {
                myFormatter = new DecimalFormat("#");
                break;
            }
            case 1: {
                myFormatter = new DecimalFormat("#.#");
                break;
            }
            case 2: {
                myFormatter = new DecimalFormat("#.##");
                break;
            }
            case 3: {
                myFormatter = new DecimalFormat("#.###");
                break;
            }
        }
        myFormatter.setDecimalFormatSymbols(DFS);
        if (signed) {
            String sign = "-";
            if (number > 0) {
                sign = "+";
            } else {
                sign = "";
            }
            return sign + myFormatter.format(number);
        } else {
            return myFormatter.format(number);
        }
    }
}
