package com.appstronautstudios.generalutilsdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appstronautstudios.generalutils.AppstronautUtils;
import com.appstronautstudios.generalutils.Boxer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView miscDataTV = findViewById(R.id.misc_data);
        TextView stringDataTV = findViewById(R.id.string_data);
        TextView dateDataTV = findViewById(R.id.date_data);
        TextView boxerDataTV = findViewById(R.id.boxer_data);
        container = findViewById(R.id.container);

        // misc data example
        ArrayList<String> miscData = new ArrayList<>();
        miscData.add("device id: " + AppstronautUtils.getDeviceId(this));
        miscData.add("device name: " + AppstronautUtils.getDeviceName());
        miscData.add("app version: " + AppstronautUtils.getVersionName(this));
        miscData.add("system locale: " + AppstronautUtils.getSystemLocale(this));
        miscDataTV.setText(AppstronautUtils.safeJoin(miscData, "\n"));

        // string data example
        ArrayList<String> stringData = new ArrayList<>();
        stringData.add("capitalization \"TEST\": " + AppstronautUtils.capitalize("TEST"));
        stringData.add("capitalization \"test\": " + AppstronautUtils.capitalize("test"));
        stringData.add(testSafeJoin(List.of("Apple", "Banana", "Orange"), ", "));
        stringData.add(testSafeJoin(List.of("1,2", "3,4", "5"), ","));
        stringData.add(testSafeJoin(List.of("Red", "Blue", "Red", "Green"), "-"));
        stringData.add(testSafeJoin(java.util.Arrays.asList("A", null, "B"), "|"));
        stringData.add(testSafeJoin(List.of("Start", "", "End"), "->"));
        stringData.add(testSafeJoin(List.of("X", "Y", "Z"), null));
        stringData.add(testSafeJoin(null, ","));
        stringData.add(testSafeJoin(List.of("1,,", "2", "three", "", "5.55", ","), ","));
        stringDataTV.setText(AppstronautUtils.safeJoin(stringData, "\n\n"));
        stringData.add("5.678, 2 decimals, not signed: " + AppstronautUtils.getNumberString(5.678, 2, false));
        stringData.add("5.678, 2 decimals, signed: " + AppstronautUtils.getNumberString(5.678, 2, true));
        stringData.add("-5.678, 2 decimals, signed: " + AppstronautUtils.getNumberString(-5.678, 2, true));
        stringData.add("0, 3 decimals, signed: " + AppstronautUtils.getNumberString(0, 3, true));
        stringData.add("0.01234, 3 decimals, not signed: " + AppstronautUtils.getNumberString(0.01234, 3, false));
        stringData.add("12345.6789, 0 decimals, not signed: " + AppstronautUtils.getNumberString(12345.6789, 0, false)); // 12346
        stringData.add("Rounding 0.4: " + AppstronautUtils.getNumberString(0.4, 0, false));
        stringData.add("Rounding 0.5: " + AppstronautUtils.getNumberString(0.5, 0, false));
        stringDataTV.setText(AppstronautUtils.safeJoin(stringData, "\n"));

        // date data example
        ArrayList<String> dateData = new ArrayList<>();
        long now = System.currentTimeMillis();
        Date today = new Date();

        // Calculate tomorrow using Calendar to avoid DST issues
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = cal.getTime();

        Date shiftedUTC = AppstronautUtils.utcDateToLocalMidnight(now);
        dateData.add("Current timestamp: " + now);
        dateData.add("TimeZone: " + TimeZone.getDefault().getID());
        dateData.add("isInTimeWindow(now, now-1000, now+1000): " + AppstronautUtils.isInTimeWindow(now, now - 1000, now + 1000));
        dateData.add("isInTimeWindow(now, now+1000, now+2000): " + AppstronautUtils.isInTimeWindow(now, now + 1000, now + 2000));
        dateData.add("isSameDay(today, today) [same timestamp]: " + AppstronautUtils.isSameDay(today, today));
        dateData.add("isSameDay(today, tomorrow) [next day]: " + AppstronautUtils.isSameDay(today, tomorrow));
        dateData.add("utcDateToLocalMidnight(now): " + shiftedUTC);
        dateData.add("startOfDate(today): " + AppstronautUtils.startOfDate(today));
        dateData.add("endOfDate(today): " + AppstronautUtils.endOfDate(today));
        dateData.add("timestampToCsvDate(now): " + AppstronautUtils.timestampToCsvDate(now));
        dateData.add("timestampToSimpleDate(now): " + AppstronautUtils.timestampToSimpleDate(now));
        dateData.add("timestampToReadableDateString(now): " + AppstronautUtils.timestampToReadableDateString(now));
        dateData.add("timestampToReadableTime12hr(now): " + AppstronautUtils.timestampToReadableTime12hr(now));
        dateData.add("timestampToReadableTime24hr(now): " + AppstronautUtils.timestampToReadableTime24hr(now));
        dateData.add("timestampToReadableTimeManual(now): " + AppstronautUtils.timestampToReadableTimeManual(now, true));
        dateData.add("timestampToReadableTimeAuto(now): " + AppstronautUtils.timestampToReadableTimeAuto(this, now));
        String csvStr = "2025-11-03 07:00";
        dateData.add("csvDateToDateObject(\"" + csvStr + "\"): " + AppstronautUtils.csvDateToDateObject(csvStr));
        dateDataTV.setText(AppstronautUtils.safeJoin(dateData, "\n"));

        // boxer data example
        ArrayList<String> boxerData = new ArrayList<>();
        Integer nullInt = null;
        Integer intVal = 42;
        Double nullDouble = null;
        Double doubleVal = 3.14159;
        Float nullFloat = null;
        Float floatVal = 2.5f;
        Long nullLong = null;
        Long longVal = 987654321L;
        Short nullShort = null;
        Short shortVal = 12;
        Byte nullByte = null;
        Byte byteVal = 8;
        boxerData.add("unbox(Integer null) → " + Boxer.unbox(nullInt));          // expect 0
        boxerData.add("unbox(Integer 42) → " + Boxer.unbox(intVal));             // expect 42
        boxerData.add("unbox(Double null) → " + Boxer.unbox(nullDouble));        // expect 0.0
        boxerData.add("unbox(Double 3.14159) → " + Boxer.unbox(doubleVal));      // expect 3.14159
        boxerData.add("unbox(Float null) → " + Boxer.unbox(nullFloat));          // expect 0.0
        boxerData.add("unbox(Float 2.5f) → " + Boxer.unbox(floatVal));           // expect 2.5
        boxerData.add("unbox(Long null) → " + Boxer.unbox(nullLong));            // expect 0
        boxerData.add("unbox(Long 987654321) → " + Boxer.unbox(longVal));        // expect 987654321
        boxerData.add("unbox(Short null) → " + Boxer.unbox(nullShort));          // expect 0
        boxerData.add("unbox(Short 12) → " + Boxer.unbox(shortVal));             // expect 12
        boxerData.add("unbox(Byte null) → " + Boxer.unbox(nullByte));            // expect 0
        boxerData.add("unbox(Byte 8) → " + Boxer.unbox(byteVal));                // expect 8
        boxerDataTV.setText(AppstronautUtils.safeJoin(boxerData, "\n"));

        // colour example
        findViewById(R.id.btnDistinct).setOnClickListener(v -> {
            showColours(AppstronautUtils.getColourSet(null, 10, true));
        });
        findViewById(R.id.btnRandom).setOnClickListener(v -> {
            showColours(AppstronautUtils.getColourSet(null, 10, false));
        });
        showColours(AppstronautUtils.getColourSet(null, 10, false));
    }

    private String testSafeJoin(List<String> input, String separator) {
        String inputString;
        if (input == null) {
            inputString = "NULL";
        } else {
            List<String> quotedInputs = new ArrayList<>();
            for (String s : input) {
                quotedInputs.add(s == null ? "null" : "\"" + s + "\"");
            }
            inputString = "[" + String.join(", ", quotedInputs) + "]";
        }

        String result = AppstronautUtils.safeJoin(input, separator);

        // Result format: safeJoin(["1,,", "2", ""] | ","): "12"
        return String.format("safeJoin(%s | \"%s\"): \"%s\"",
                inputString, (separator == null ? "null" : separator), result);
    }

    private void showColours(int[] colours) {
        container.setVisibility(View.VISIBLE);
        container.removeAllViews();

        int swatchSize = (int) (48 * getResources().getDisplayMetrics().density);

        for (int colour : colours) {
            View swatch = new View(this);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(swatchSize, swatchSize);
            params.setMargins(8, 8, 8, 8);
            swatch.setLayoutParams(params);
            swatch.setBackgroundColor(colour);
            container.addView(swatch);
        }
    }
}