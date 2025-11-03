package com.appstronautstudios.generalutilsdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appstronautstudios.library.AppstronautUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView miscDataTV = findViewById(R.id.misc_data);
        TextView stringDataTV = findViewById(R.id.string_data);
        TextView dateDataTV = findViewById(R.id.date_data);
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
        stringData.add("string join \"[\"1,,\",\"2\",\"three\",\"\",\"5.55\",\",\"]\": " + AppstronautUtils.safeJoin(List.of(new String[]{"1,,", "2", "three", "", "5.55", ","}), ","));
        stringData.add("5.678, 2 decimals, not signed: " + AppstronautUtils.getNumberString(5.678, 2, false));
        stringData.add("5.678, 2 decimals, signed: " + AppstronautUtils.getNumberString(5.678, 2, true));
        stringData.add("-5.678, 2 decimals, signed: " + AppstronautUtils.getNumberString(-5.678, 2, true));
        stringData.add("0, 3 decimals, signed: " + AppstronautUtils.getNumberString(0, 3, true));
        stringData.add("0.01234, 3 decimals, not signed: " + AppstronautUtils.getNumberString(0.01234, 3, false));
        stringData.add("12345.6789, 0 decimals, not signed: " + AppstronautUtils.getNumberString(12345.6789, 0, false)); // 12346
        stringDataTV.setText(AppstronautUtils.safeJoin(stringData, "\n"));

        // string data example
        ArrayList<String> dateData = new ArrayList<>();
        long now = System.currentTimeMillis();
        Date today = new Date();
        Date shiftedUTC = AppstronautUtils.utcDateToLocalMidnight(now);
        dateData.add("Current timestamp: " + now);
        dateData.add("isInTimeWindow(now, now-1000, now+1000): " + AppstronautUtils.isInTimeWindow(now, now - 1000, now + 1000));
        dateData.add("isInTimeWindow(now, now+1000, now+2000): " + AppstronautUtils.isInTimeWindow(now, now + 1000, now + 2000));
        dateData.add("utcDateToLocalMidnight(now): " + shiftedUTC);
        dateData.add("startOfDate(today): " + AppstronautUtils.startOfDate(today));
        dateData.add("endOfDate(today): " + AppstronautUtils.endOfDate(today));
        dateData.add("timestampToCsvDate(now): " + AppstronautUtils.timestampToCsvDate(now));
        dateData.add("timestampToSimpleDate(now): " + AppstronautUtils.timestampToSimpleDate(now));
        String csvStr = "2025-11-03 07:00";
        dateData.add("csvDateToDateObject(\"" + csvStr + "\"): " + AppstronautUtils.csvDateToDateObject(csvStr));
        dateDataTV.setText(AppstronautUtils.safeJoin(dateData, "\n"));

        // colour example
        findViewById(R.id.btnDistinct).setOnClickListener(v -> {
            showColours(AppstronautUtils.getColourSet(null, 10, true));
        });
        findViewById(R.id.btnRandom).setOnClickListener(v -> {
            showColours(AppstronautUtils.getColourSet(null, 10, false));
        });
        showColours(AppstronautUtils.getColourSet(null, 10, false));
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
