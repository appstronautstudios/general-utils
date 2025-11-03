package com.appstronautstudios.generalutils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Utility class for safe boxing/unboxing of Java wrapper types.
 * <p>
 * Useful when working with data binding from Kotlin (which only has boxed
 * types at the Java boundary), preventing {@link NullPointerException}
 * when nullable boxed values are bound directly to primitive views.
 *
 * <p>Android includes a similar helper (e.g. {@code SafeUnbox}), but it does
 * not support two-way data binding. These helpers allow safe conversion
 * in both directions.
 */
public final class Boxer {

    private Boxer() {
    }  // prevent instantiation

    private static final char DEFAULT_CHAR = ' ';
    private static final int DEFAULT_NUMBER = 0;
    private static final float DEFAULT_FLOAT = 0f;
    private static final double DEFAULT_DOUBLE = 0d;

    public static boolean unbox(@Nullable Boolean b) {
        return b != null && b;
    }

    @NonNull
    public static Boolean boxBoolean(boolean b) {
        return b;
    }

    public static char unbox(@Nullable Character c) {
        return c != null ? c : DEFAULT_CHAR;
    }

    @NonNull
    public static Character boxChar(char c) {
        return c;
    }

    public static byte unbox(@Nullable Byte b) {
        return b != null ? b : DEFAULT_NUMBER;
    }

    @NonNull
    public static Byte boxByte(byte b) {
        return b;
    }

    public static short unbox(@Nullable Short s) {
        return s != null ? s : DEFAULT_NUMBER;
    }

    @NonNull
    public static Short boxShort(short s) {
        return s;
    }

    public static int unbox(@Nullable Integer i) {
        return i != null ? i : DEFAULT_NUMBER;
    }

    @NonNull
    public static Integer boxInteger(int i) {
        return i;
    }

    public static long unbox(@Nullable Long l) {
        return l != null ? l : DEFAULT_NUMBER;
    }

    @NonNull
    public static Long boxLong(long l) {
        return l;
    }

    public static float unbox(@Nullable Float f) {
        return f != null ? f : DEFAULT_FLOAT;
    }

    @NonNull
    public static Float boxFloat(float f) {
        return f;
    }

    public static double unbox(@Nullable Double d) {
        return d != null ? d : DEFAULT_DOUBLE;
    }

    @NonNull
    public static Double boxDouble(double d) {
        return d;
    }
}